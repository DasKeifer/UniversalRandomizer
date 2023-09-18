package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.PeekPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;

public abstract class MultiRandomizer<T, P> 
{	
	private MultiSetter<T, P> setter;
	private Getter<T, Integer> countGetter;
	private Random rand;
	private PeekPool<P> pool;
	private EnforceParams<T> enforceActions;
	
	// TODO have way to pass in int instead of getter
	public MultiRandomizer(MultiSetter<T, P> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce)
	{
		this.setter = setter;
		this.countGetter = countGetter;
		
		pool = null;
		rand = null;

		if (enforce == null)
		{
			this.enforceActions = EnforceParams.createNoEnforce();
		}
		else
		{
			this.enforceActions = enforce;
		}
	}

	protected abstract void resetPool();
	protected abstract P peekNext(Random rand);
	protected abstract void selectPeeked();
	
	public boolean perform(Stream<T> objStream, PeekPool<P> pool) 
	{
		return perform(objStream, pool, null);
	}
	
	public boolean perform(Stream<T> objStream, PeekPool<P> pool, Random rand) 
	{
		this.pool = pool;
		if (rand == null && this.rand == null)
		{
			this.rand = new Random();
		}
		
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<T> streamAsList = objStream.toList();
		return attemptRandomization(streamAsList);
	}
	
	// Handles RESET
	protected boolean attemptRandomization(List<T> streamAsList)
	{		
		// Attempt to assign randomized values for each item in the stream
		List<T> failed = randomize(streamAsList.stream());
		
		// While we have failed and have resets left
		for (int reset = 0; reset < enforceActions.getMaxResets(); reset++)
		{
			if (failed.isEmpty())
			{
				break;
			}
			resetPool();
			failed = randomize(streamAsList.stream());
		}
		
		return failed.isEmpty();
	}

	protected List<T> randomize(Stream<T> objStream)
	{
		return objStream.filter(this::assignValueNegated).toList();
	}
	
	protected boolean assignValueNegated(T obj)
	{
		return !assignValue(obj);
	}
	
	protected boolean assignValue(T obj)
	{
		boolean success = true;
		int setCount = countGetter.get(obj);
		for (int count = 0; count < setCount; count++)
		{
			success = success && attemptAssignValue(obj, count);
		}
		return success;
	}

	protected boolean attemptAssignValue(T obj, int count)
	{
		P selectedVal = peekNext(rand);
		
		// While its a good index and fails the enforce check, retry if
		// we have attempts left
		boolean success = assignAndCheckEnforce(obj, selectedVal, count);
		for (int retry = 0; retry < enforceActions.getMaxRetries(); retry++)
		{
			if (success || selectedVal == null)
			{
				break;
			}
			selectedVal = peekNext(rand);
			success = assignAndCheckEnforce(obj, selectedVal, count);
		}
		
		if (success)
		{
			selectPeeked();
		}
		
		return success;
	}
	
	protected boolean assignAndCheckEnforce(T obj, P value, int count)
	{
		return setter.setReturn(obj, value, count) && enforceActions.evaluateEnforce(obj);
	}

	protected MultiSetter<T, P> getSetter() 
	{
		return setter;
	}

	protected Random getRandom() 
	{
		return rand;
	}

	protected PeekPool<P> getPool() 
	{
		return pool;
	}

	protected EnforceParams<T> getEnforceActions() 
	{
		return enforceActions;
	}
}