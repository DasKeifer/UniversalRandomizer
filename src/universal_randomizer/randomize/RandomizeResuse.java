package universal_randomizer.randomize;

import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.wrappers.ReflectionObject;

public class RandomizeResuse<T, P> extends Randomize<T, P> 
{
	// RETRY, RESET, [ALTERNATE], <IGNORE/ABORT>
	public RandomizeResuse(String pathToField, Pool<P> pool, Random rand)
	{
		super(pathToField, pool, rand);
	}
	
	public static <V, S> RandomizeResuse<V, S> createRandomPoolFromStream(String pathToField)
	{
		return new RandomizeResuse<>(pathToField, null, null);
	}
	
	public static <V, S> RandomizeResuse<V, S> createSeededPoolFromStream(String pathToField, long seed)
	{
		return new RandomizeResuse<>(pathToField, null, new Random(seed));
	}
	
	public static <V, S> RandomizeResuse<V, S> createRandomWithPool(String pathToField, Pool<S> pool)
	{
		return new RandomizeResuse<>(pathToField, pool, null);
	}
			
	public static <V, S> RandomizeResuse<V, S> createSeededWithPool(String pathToField, Pool<S> pool, long seed)
	{
		return new RandomizeResuse<>(pathToField, pool, new Random(seed));
	}
	
	@Override
	protected boolean attemptAssignValue(ReflectionObject<T> obj)
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
		// TODO: Temp
		return false;
	}
}
