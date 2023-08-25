package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.Pool;
import universal_randomizer.Utils;

public abstract class Randomizer<T, P> 
{	
	private String pathToField;
	private Random rand;
	private Pool<P> pool;
	private EnforceParams<T> enforceActions;
	
	protected Randomizer(String pathToField, Pool<P> pool, EnforceParams<T> enforce)
	{
		this.pathToField = pathToField;

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

	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<ReflectionObject<T>> streamAsList = objStream.toList();
		if (getPool() == null)
		{
			// TODO: need some logic to handle map fields
			pool = Pool.create(false, Utils.narrowToField(pathToField, streamAsList.stream()));
		}
		return attemptRandomization(streamAsList);
	}
	
	// Handles RESET
	protected boolean attemptRandomization(List<ReflectionObject<T>> streamAsList)
	{		
		// Attempt to assign randomized values for each item in the stream
		List<ReflectionObject<T>> failed = randomize(streamAsList.stream());
		
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

	protected List<ReflectionObject<T>> randomize(Stream<ReflectionObject<T>> objStream)
	{
		return objStream.filter(this::assignValueNegated).toList();
	}
	
	protected boolean assignValueNegated(ReflectionObject<T> obj)
	{
		return !assignValue(obj);
	}
	
	protected boolean assignValue(ReflectionObject<T> obj)
	{
		boolean success = attemptAssignValue(obj);
		
		// Select regardless
		selectPeeked();
		return success;
	}

	protected boolean attemptAssignValue(ReflectionObject<T> obj)
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
	
	@SuppressWarnings("unchecked")
	protected boolean assignAndCheckEnforce(ReflectionObject<T> obj, P value)
	{
		if (value == null)
		{
			return false;
		}
		obj.setField(pathToField, value, (Class<P>) value.getClass());
		return enforceActions.evaluateEnforce(obj);
	}

	protected String getPathToField() 
	{
		return pathToField;
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