package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.wrappers.ReflectionObject;

public class RandomizeEliminate<T, P> extends Randomize<T, P> {
	
	List<Pool<P>> workingPools;
	
	public RandomizeEliminate(String pathToField, Pool<P> pool, Random rand)
	{
		super(pathToField, pool, rand);
	}
	
	public static <V, S> RandomizeEliminate<V, S> createRandomPoolFromStream(String pathToField)
	{
		return new RandomizeEliminate<>(pathToField, null, null);
	}
	
	public static <V, S> RandomizeEliminate<V, S> createSeededPoolFromStream(String pathToField, long seed)
	{
		return new RandomizeEliminate<>(pathToField, null, new Random(seed));
	}
	
	public static <V, S> RandomizeEliminate<V, S> createRandomWithPool(String pathToField, Pool<S> pool)
	{
		return new RandomizeEliminate<>(pathToField, pool, null);
	}
			
	public static <V, S> RandomizeEliminate<V, S> createSeededWithPool(String pathToField, Pool<S> pool, long seed)
	{
		return new RandomizeEliminate<>(pathToField, pool, new Random(seed));
	}
	@Override
	protected boolean attemptAssignValue(ReflectionObject<T> obj)
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
		
		// TODO: Implement NEW_POOL functionality
		
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
		// TODO: Temp
		return false;
	}
}
