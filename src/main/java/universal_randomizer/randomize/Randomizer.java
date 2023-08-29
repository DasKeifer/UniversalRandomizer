package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.Pool;
import universal_randomizer.Utils;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;

public abstract class Randomizer<T, P> 
{	
	private Setter<T, P> setter;
	private Random rand;
	private Pool<P> pool;
	private EnforceParams<T> enforceActions;
	
	protected Randomizer(Setter<T, P> setter, Pool<P> pool, EnforceParams<T> enforce)
	{
		this.setter = setter;

		if (pool == null)
		{
			this.pool = null;
		}
		else
		{
			this.pool = pool.copy();
		}
		
		rand = new Random();

		if (enforce == null)
		{
			this.enforceActions = EnforceParams.createNoEnforce();
		}
		else
		{
			this.enforceActions = enforce;
		}
	}
	
	public void setRandom(long seed)
	{
		this.rand = new Random(seed);
	}
	
	public void setRandom(Random rand)
	{
		this.rand = rand;
	}
	
	public void unseedRandom()
	{
		this.rand = new Random();
	}

	protected abstract void resetPool();
	protected abstract P peekNext(Random rand);
	protected abstract void selectPeeked();

	public  boolean perform(Stream<T> objStream) 
	{
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<T> streamAsList = objStream.toList();
		if (getPool() == null)
		{
			return false;
		}
		return attemptRandomization(streamAsList);
	}
	
	public  boolean perform(Stream<T> objStream, Getter<T, P> getter) 
	{
		boolean result = false;
		
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<T> streamAsList = objStream.toList();
		if (getPool() == null && getter != null)
		{
			// TODO: need some logic to other field types - maybe pass in the function to use?
			pool = Pool.create(false, Utils.convertToField(getter, streamAsList.stream()));
			result = attemptRandomization(streamAsList);
		}
		else if (getPool() != null && getter == null)
		{
			result = attemptRandomization(streamAsList);
		}
		return result;
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
		boolean success = attemptAssignValue(obj);
		
		// Select regardless
		selectPeeked();
		return success;
	}

	protected boolean attemptAssignValue(T obj)
	{
		P selectedVal = peekNext(rand);
		
		// While its a good index and fails the enforce check, retry if
		// we have attempts left
		boolean success = assignAndCheckEnforce(obj, selectedVal);
		for (int retry = 0; retry < enforceActions.getMaxRetries(); retry++)
		{
			if (success || selectedVal == null)
			{
				break;
			}
			selectedVal = peekNext(rand);
			success = assignAndCheckEnforce(obj, selectedVal);
		}
		return success;
	}
	
	protected boolean assignAndCheckEnforce(T obj, P value)
	{
		return setter.setReturn(obj, value) && enforceActions.evaluateEnforce(obj);
	}

	protected Setter<T, P> getSetter() 
	{
		return setter;
	}

	protected Random getRandom() 
	{
		return rand;
	}

	protected Pool<P> getPool() 
	{
		return pool;
	}

	protected EnforceParams<T> getEnforceActions() 
	{
		return enforceActions;
	}
}