package universal_randomizer.randomize;

import java.util.Random;
import java.util.SortedSet;

import universal_randomizer.Pool;

public class RandomizerResuse<T, P> extends Randomizer<T, P> 
{
	// RETRY, RESET, [ALTERNATE], <IGNORE/ABORT>
	private RandomizerResuse(String pathToField, Pool<P> pool, Random rand)
	{
		super(pathToField, pool, rand);
	}
	
	public static <V, S> RandomizerResuse<V, S> createRandomPoolFromStream(String pathToField)
	{
		return new RandomizerResuse<>(pathToField, null, null);
	}
	
	public static <V, S> RandomizerResuse<V, S> createSeededPoolFromStream(String pathToField, long seed)
	{
		return new RandomizerResuse<>(pathToField, null, new Random(seed));
	}
	
	public static <V, S> RandomizerResuse<V, S> createRandomWithPool(String pathToField, Pool<S> pool)
	{
		return new RandomizerResuse<>(pathToField, pool, null);
	}
			
	public static <V, S> RandomizerResuse<V, S> createSeededWithPool(String pathToField, Pool<S> pool, long seed)
	{
		return new RandomizerResuse<>(pathToField, pool, new Random(seed));
	}

	@Override
	protected P removeAtIndex(int index) 
	{
		// Null means don't reassign - use value returned for trial
		// TODO: Copy here?
		return null;
	}

	@Override
	protected P trialAtIndex(int index) 
	{
		return removeAtIndex(index);
	}

	@Override
	protected int getNextIndex(SortedSet<Integer> excludedIndexes) 
	{
		return sourcePool.getRandomKey(rand, excludedIndexes);
	}
}
