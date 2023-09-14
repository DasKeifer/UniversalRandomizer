package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.Pool;
import universal_randomizer.Utils;
import universal_randomizer.stream.RandomizeMultiStream;
import universal_randomizer.stream.RandomizeStream;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;

public abstract class Randomizer<T, P> 
{	
	// TODO multiRandomizer - set multiple at a time or take a collection/array
	
	private Setter<T, P> setter;
	private Random rand;
	private Pool<P> pool;
	private Getter<T, P> poolGetter;
	private EnforceParams<T> enforceActions;
	
	protected Randomizer(Setter<T, P> setter, Pool<P> pool, Getter<T, P> poolGetter, EnforceParams<T> enforce)
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
		this.poolGetter = poolGetter;
		
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

	// Multi stream ->  multi  pool (Try mapping 1:1, or have an arg)
	// MultiStream ->   single pool (collapse and treat as single stream)
	// single stream -> multi  pool (need arg or way to downselect)
	public boolean perform(RandomizeMultiStream<T> objStream) 
	{
		// Extract streams instead?
		RandomizeStream<Boolean> results = objStream.mapStreams(this::perform);
		return results.toStream().allMatch(b -> b);
	}
	
	public boolean perform(RandomizeStream<T> objStream) 
	{
		return perform(objStream.toStream());
	}
	
	public  boolean perform(Stream<T> objStream) 
	{
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<T> streamAsList = objStream.toList();
		if (poolGetter != null)
		{
			pool = Pool.create(false, Utils.convertToField(streamAsList.stream(), poolGetter));
			return attemptRandomization(streamAsList);
		}
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
	
	protected Getter<T, P> getPoolGetter() {
		return poolGetter;
	}

	protected EnforceParams<T> getEnforceActions() 
	{
		return enforceActions;
	}
}