package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.pool.PeekPool;
import universal_randomizer.pool.RandomizerPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.wrappers.SetterAsMultiSetter;

public class Randomizer<T, P> 
{	
	private MultiSetter<T, P> setter;
	private Getter<T, Integer> countGetter;
	private RandomizerPool<P> randPool;
	private Random rand;
	private EnforceParams<T> enforceActions;
	
	protected Randomizer(MultiSetter<T, P> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce)
	{
		this.setter = setter;
		this.countGetter = countGetter;
		
		randPool = null;
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

	public <T2, P2> Randomizer<T2, P2> create(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
	{
		return new Randomizer<>(setter, countGetter, enforce);
	}
	
	public <T2, P2> Randomizer<T2, P2> create(MultiSetter<T2, P2> setter, int count, EnforceParams<T2> enforce)
	{
		return new Randomizer<>(setter, o -> count, enforce);
	}
	
	public <T2, P2> Randomizer<T2, P2> create(Setter<T2, P2> setter, EnforceParams<T2> enforce)
	{
		return create(new SetterAsMultiSetter<>(setter), 1, enforce);
	}

	public <T2, P2> Randomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter)
	{
		return create(setter, countGetter, null);
	}
	
	public <T2, P2> Randomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter, int count)
	{
		return create(setter, o -> count, null);
	}
	
	public <T2, P2> Randomizer<T2, P2> createNoEnforce(Setter<T2, P2> setter)
	{
		return create(setter, null);
	}
	
	public boolean perform(Stream<T> objStream, PeekPool<P> pool) 
	{
		return perform(objStream, pool, null);
	}
	
	public boolean perform(Stream<T> objStream, RandomizerPool<P> pool, Random rand) 
	{
		this.randPool = pool;
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
			randPool.reset();
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
		do 
		{
			success = true;
			int setCount = countGetter.get(obj);
			for (int count = 0; count < setCount; count++)
			{
				success = success && attemptAssignValue(obj, count);
			}
			if (success)
			{
				break;
			}
		} while (randPool.useNextPool());		
		
		return success;
	}

	protected boolean attemptAssignValue(T obj, int count)
	{
		P selectedVal = randPool.peek(rand);
		
		// While its a good index and fails the enforce check, retry if
		// we have attempts left
		boolean success = assignAndCheckEnforce(obj, selectedVal, count);
		for (int retry = 0; retry < enforceActions.getMaxRetries(); retry++)
		{
			if (success || selectedVal == null)
			{
				break;
			}
			selectedVal = randPool.peek(rand);
			success = assignAndCheckEnforce(obj, selectedVal, count);
		}
		
		if (success)
		{
			randPool.selectPeeked();
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

	protected RandomizerPool<P> getPool() 
	{
		return randPool;
	}

	protected EnforceParams<T> getEnforceActions() 
	{
		return enforceActions;
	}
}