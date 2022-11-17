package universal_randomizer.randomize;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import condition.CompoundCondition;
import universal_randomizer.Pool;
import universal_randomizer.action.ReflObjStreamAction;
import universal_randomizer.utils.AttemptCounter;
import universal_randomizer.wrappers.ReflectionObject;

public abstract class Randomize<T, P> implements ReflObjStreamAction<T>
{
	String pathToField;
	Random rand;
	Pool<P> sourcePool;
	CompoundCondition<P> enforce;
	AttemptCounter retries;
	AttemptCounter resets;
	OnFail onFail;
	
	// Make this a set? ALTERNATE repeats previous set if no specific is given]
	// Define specific order for fail actions?
	// RETRY, RESET, [NEW_POOL, ALTERNATE], <IGNORE/ABORT>
	
	
	protected Randomize(String pathToField, Pool<P> pool, Random rand)
	{
		this.pathToField = pathToField;

		if (pool == null)
		{
			this.sourcePool = null;
		}
		else
		{
			this.sourcePool = Pool.createCopy(pool);
		}
		
		if (rand == null)
		{
			rand = new Random();
		}
		else
		{
			this.rand = rand;
		}
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		if (sourcePool == null)
		{
			//TODO: have a flatten option (Which is how it behaves now) vs a "pairwise" option which would
			//treat arrays/collections as a single entry
			sourcePool = Pool.createFromStream(pathToField, objStream);
		}
		
		// TODO: need to copy the stream for this to work. May just need to convert to list first...

		// Attempt to assign randomized values for each item in the stream
		List<ReflectionObject<T>> failed = objStream.filter(this::attemptAssignValue).collect(Collectors.toList());

		// if we failed, reset as many times as we are allowed 
		while (!failed.isEmpty() && resets.attemptIfAnyRemaining())
		{
			// TODO log error info
			failed = objStream.filter(this::attemptAssignValue).collect(Collectors.toList());
		}
		
		// if we still didn't find any after resetting, try the next thing
		if (!failed.isEmpty())
		{
			// TODO: ALTERNATE
			// TODO: ABORT/IGNORE
		}
		
		// TODO: Randomize Each/All/Set
		
		// TODO: handle enforce
		
		// TODO: Handle multi-value sets
		return true;
	}
	
	protected abstract boolean attemptAssignValue(ReflectionObject<T> obj);
	
	
	protected int getNextIndex(ReflectionObject<T> obj, Pool<P> pool)
	{
		// Get a random index
		int randIndex = pool.getRandomIndex(rand);
		
		// If we got a valid index, have retries but have a failing constraint
		if (randIndex >= 0 && retries.anyAttemptsLeft() && enforce != null /*&& enforce condition fails on index*/)
		{
			// Try again as long as we get valid indexes, have attempts left, and still fail the constraint
			SortedSet<Integer> exlcudedIndexes = new TreeSet<>();
			do 
			{
				// Add the index that failed the check
				exlcudedIndexes.add(randIndex);
				
				// get the next index
				randIndex = pool.getRandomIndex(rand, exlcudedIndexes);
				
				// If its valid, retry the check
				if (randIndex > 0 /*&& enforce condition fails on index*/)
				{
					break;
				}
				
			// the index was valid and we have retries but the constraint failed, retry
			} while (randIndex > 0 && retries.accountForAttempt());
			
			// If we ran out of attempts, set the index to invalid
			if (!retries.anyAttemptsLeft())
			{
				randIndex = -1;
			}
		}
		
		return randIndex;
	}
}
