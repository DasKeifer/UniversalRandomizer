package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.Pool;

public abstract class Randomizer<T, P> 
{	
	String pathToField;
	Random rand;
	Pool<P> pool;
	EnforceActions<T> enforceActions;
	
	// Make this a set? ALTERNATE repeats previous set if no specific is given]
	// Define specific order for fail actions?
	// Set order: Retry, alternate then retry again, repeat
	
	// RETRY, RESET, [NEW_POOL, ALTERNATE], <IGNORE/ABORT>
	
	protected Randomizer(String pathToField, Pool<P> pool, Random rand)
	{
		this.pathToField = pathToField;

		if (pool == null)
		{
			this.pool = null;
		}
		else
		{
			this.pool = Pool.createCopy(pool);
		}
		
		if (rand == null)
		{
			rand = new Random();
		}
		else
		{
			this.rand = rand;
		}
		
		enforceActions = EnforceActions.createNone();
	}

	public void setEnforceActions(EnforceActions<T> enforce)
	{
		enforceActions = enforce;
	}

	protected abstract void resetPool();
	protected abstract P peekNext(Random rand);
	protected abstract void selectPeeked();

	public RandomizeResult perform(Stream<ReflectionObject<T>> objStream) 
	{
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<ReflectionObject<T>> streamAsList = objStream.collect(Collectors.toList());
		if (pool == null)
		{
			pool = Pool.createFromStream(pathToField, streamAsList.stream());
		}
		
		return attemptRandomization(streamAsList);
	}
	
	// Handles RESET
	protected RandomizeResult attemptRandomization(List<ReflectionObject<T>> streamAsList)
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
		
		RandomizeResult result = RandomizeResult.INVALID;
		if (failed.isEmpty())
		{
			result = RandomizeResult.SUCCESS;
		}
		else 
		{
			switch (enforceActions.getFailAction())
			{
				case ABORT:
					result = RandomizeResult.FAILED;
					break;
				case IGNORE:
					result = RandomizeResult.PARTIAL;
				default:
					// TODO: Error
					break;	
			}
		}
		return result;
	}

	protected List<ReflectionObject<T>> randomize(Stream<ReflectionObject<T>> objStream)
	{
		return objStream.filter(this::assignValue).collect(Collectors.toList());
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
	
	protected boolean assignAndCheckEnforce(ReflectionObject<T> obj, P value)
	{
		if (value == null)
		{
			return false;
		}
		obj.setVariableValue(pathToField, value);
		return enforceActions.evaluateEnforce(obj);
	}
}