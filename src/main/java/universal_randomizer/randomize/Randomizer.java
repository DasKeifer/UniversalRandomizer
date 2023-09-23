package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.pool.RandomizerBasicPool;
import universal_randomizer.pool.RandomizerMultiPool;
import universal_randomizer.pool.RandomizerPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;

public abstract class Randomizer<T, O, P, S> 
{	
	private RandomizerPool<P> pool;
	private RandomizerMultiPool<T, P> multiPool;
	private Random rand;
	private EnforceParams<T> enforceActions;
	private Getter<T, Integer> countGetter;
	private MultiSetter<O, S> setter;

	protected Randomizer(MultiSetter<O, S> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce)
	{
		pool = null;
		rand = null;
		
		this.setter = setter;
		this.countGetter = countGetter;
		
		if (enforce == null)
		{
			this.enforceActions = EnforceParams.createNoEnforce();
		}
		else
		{
			this.enforceActions = enforce;
		}
	}

	public boolean perform(Stream<T> objStream, RandomizerBasicPool<P> pool) 
	{
		return perform(objStream, pool, null);
	}

	public boolean perform(Stream<T> objStream, RandomizerMultiPool<T, P> pool) 
	{
		return perform(objStream, pool, null);
	}
	
	public boolean perform(Stream<T> objStream, RandomizerBasicPool<P> pool, Random rand) 
	{
		this.pool = pool;
		this.multiPool = null;
		return performCommon(objStream, rand);
	}
	
	public boolean perform(Stream<T> objStream, RandomizerMultiPool<T, P> pool, Random rand) 
	{
		this.pool = pool;
		this.multiPool = pool;
		return performCommon(objStream, rand);
	}
	
	private boolean performCommon(Stream<T> objStream, Random rand)
	{
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
			pool.reset();
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
		// Set the pool by the key
		if (multiPool != null)
		{
			multiPool.setPool(obj, count);
		}

		boolean success = true;
		do // Loop on pool depth (if pool supports it)
		{
			// Get the next item and try it
			P selectedVal = getPool().peek(getRandom());
			
			// While its a good index and fails the enforce check, retry if
			// we have attempts left
			success = assignAndCheckEnforce(obj, selectedVal, count);
			for (int retry = 0; retry < getEnforceActions().getMaxRetries(); retry++)
			{
				if (success || selectedVal == null)
				{
					break;
				}
				selectedVal = getPool().peek(getRandom());
				success = assignAndCheckEnforce(obj, selectedVal, count);
			}
		} while (!success && getPool().useNextPool());	

		if (success)
		{
			getPool().selectPeeked();
		}		
		
		return success;
	}

	protected abstract boolean assignAndCheckEnforce(T obj, P poolValue, int count);

	protected Random getRandom() 
	{
		return rand;
	}

	protected RandomizerPool<P> getPool() 
	{
		return pool;
	}
	
	protected Getter<T, Integer> getCountGetter() 
	{
		return countGetter;
	}

	protected MultiSetter<O, S> getSetter() 
	{
		return setter;
	}

	protected EnforceParams<T> getEnforceActions() 
	{
		return enforceActions;
	}
}