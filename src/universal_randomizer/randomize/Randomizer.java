package universal_randomizer.randomize;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.condition.CompoundCondition;
import universal_randomizer.Pool;
import universal_randomizer.wrappers.ReflectionObject;

public class Randomizer<T, P> 
{
	String pathToField;
	Random rand;
	Pool<P> sourcePool;
	List<Pool<P>> workingPools; // For onAssign REMOVE only
	OnAssign onAssign;
	CompoundCondition<P> enforce;
	List<OnFailAction> sourceOnFailActions;
	List<OnFailAction> workingOnFailActions;
	OnFailAction currentOnFailAction;
	// Make this a set? ALTERNATE repeats previous set if no specific is given]
	// Define specific order for fail actions?
	// Set order: Retry, alternate then retry again, repeat
	
	// RETRY, RESET, [NEW_POOL, ALTERNATE], <IGNORE/ABORT>
	
	
	private Randomizer(String pathToField, Pool<P> pool, Random rand)
	{
		this.pathToField = pathToField;

		setSourcePool(pool);
		workingPools = new LinkedList<>();
		setOnAssign(OnAssign.KEEP);
		
		if (rand == null)
		{
			rand = new Random();
		}
		else
		{
			this.rand = rand;
		}
	}

	public static <V, S> Randomizer<V, S> createRandomPoolFromStream(String pathToField)
	{
		return new Randomizer<>(pathToField, null, null);
	}
	
	public static <V, S> Randomizer<V, S> createSeededPoolFromStream(String pathToField, long seed)
	{
		return new Randomizer<>(pathToField, null, new Random(seed));
	}
	
	public static <V, S> Randomizer<V, S> createRandomWithPool(String pathToField, Pool<S> pool)
	{
		return new Randomizer<>(pathToField, pool, null);
	}
			
	public static <V, S> Randomizer<V, S> createSeededWithPool(String pathToField, Pool<S> pool, long seed)
	{
		return new Randomizer<>(pathToField, pool, new Random(seed));
	}
	
	private void setSourcePool(Pool<P> pool)
	{
		if (pool == null)
		{
			this.sourcePool = null;
		}
		else
		{
			this.sourcePool = Pool.createCopy(pool);
		}
	}
	
	public void setOnAssign(OnAssign onAssign)
	{
		this.onAssign = onAssign;
	}

	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. This will store the list if needed
		List<ReflectionObject<T>> asList = null;
		if (sourcePool == null)
		{
			//TODO: have a flatten option (Which is how it behaves now) vs a "pairwise" option which would
			//treat arrays/collections as a single entry
			asList = objStream.collect(Collectors.toList());
			setSourcePool(Pool.createFromStream(pathToField, asList.stream()));
		}
		
		// TODO: If retries, create a copy of the list if not presetn
		
		switch (onAssign)
		{
			case REMOVE:
				objStream.forEach(this::assignValueAndRemove);
				break;
			case KEEP:
				objStream.forEach(this::assignValue);
				break;
			default:
				System.err.println("Unrecognized OnAssign value passed: " + onAssign);
				break;
		}
			
		
		// TODO: Randomize Each/All/Set
		
		// TODO: handle enforce
		
		// TODO: Handle multi-value sets
		
		return true;
	}
	
	private void assignValue(ReflectionObject<T> obj)
	{
		int randIndex = getNextIndex(obj, sourcePool);
		if (randIndex >= 0)
		{
			obj.setVariableValue(pathToField, sourcePool.get(randIndex));
		}
		else
		{
			System.err.println("Failed to find an item in the pool - either it is empty or no items exist that satisfies enforcements");
		}
	}
	
	private void assignValueAndRemove(ReflectionObject<T> obj)
	{
		int randIndex = -1;
		int poolIndex = 0;
		for (/*set above*/; poolIndex < workingPools.size(); poolIndex++)
		{
			randIndex = getNextIndex(obj, workingPools.get(poolIndex));
			if (randIndex >= 0)
			{
				break;
			}
		}
		
		// Failed to find a valid entry in the working pools
		if (randIndex < 0)
		{
			workingPools.add(Pool.createCopy(sourcePool));
			poolIndex = workingPools.size() - 1;
			randIndex = getNextIndex(obj, workingPools.get(poolIndex));
		}
		
		// If we still failed to find it, log an error
		if (randIndex >= 0)
		{
			obj.setVariableValue(pathToField, workingPools.get(poolIndex).pop(randIndex));
		}
		else
		{
			System.err.println("Failed to find an item in the pool - either it is empty or no items exist that satisfies enforcements");
		}
	}
	
	private int getNextIndex(ReflectionObject<T> obj, Pool<P> pool)
	{
		int randIndex = pool.getRandomIndex(rand);
		if (randIndex >= 0)
		{
			// TODO check constraints
			// if Constraint exist and it failed
			if (enforce != null)
			{
				// Need to think on this whole part more specifically chaining them
				if (currentOnFailAction == null)
				{
					currentOnFailAction = OnFailActionAttempts.createRetryUntilExhaustedAction();
				}
				
				switch (currentOnFailAction.actionType)
				{
					case RETRY:
						SortedSet<Integer> exlcudedIndexes = new TreeSet<>();
						OnFailActionAttempts retryAction = (OnFailActionAttempts) currentOnFailAction;
						do 
						{
							if (retryAction.attempt())
							{
								exlcudedIndexes.add(randIndex);
								randIndex = pool.getRandomIndex(rand);
							}
							else
							{
								break;
							}
						} while (randIndex >= 0 && enforce != null);
						break;
					case ABORT:
						throw new RuntimeException("Failed to assign value from pool!");
					case ALTERNATE:
						// Do an alternate condition - not implemented yet
						break;
					// All of these do nothing at this level
					case IGNORE:
					case RESET:
					case NEW_POOL:
						break;
					default:
						// Throw error & abort
						break;
				}
			}
		}
		
		return randIndex;
	}
}
