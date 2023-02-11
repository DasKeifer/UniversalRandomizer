package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.SortedSet;

import universal_randomizer.Pool;

public class RandomizerEliminate<T, P> extends Randomizer<T, P> {
	
	List<Pool<P>> workingPools;
	
	public RandomizerEliminate(String pathToField, Pool<P> pool, Random rand)
	{
		super(pathToField, pool, rand);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createRandomPoolFromStream(String pathToField)
	{
		return new RandomizerEliminate<>(pathToField, null, null);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createSeededPoolFromStream(String pathToField, long seed)
	{
		return new RandomizerEliminate<>(pathToField, null, new Random(seed));
	}
	
	public static <V, S> RandomizerEliminate<V, S> createRandomWithPool(String pathToField, Pool<S> pool)
	{
		return new RandomizerEliminate<>(pathToField, pool, null);
	}
			
	public static <V, S> RandomizerEliminate<V, S> createSeededWithPool(String pathToField, Pool<S> pool, long seed)
	{
		return new RandomizerEliminate<>(pathToField, pool, new Random(seed));
	}

	@Override
	protected P removeAtIndex(int index) 
	{
		// TODO: temp
		sourcePool.remove(index);

		// Null means don't reassign - use value returned for trial
		// TODO: Copy here?
		return null;
	}

	@Override
	protected P trialAtIndex(int index) 
	{
		// TODO: temp
		return sourcePool.get(index);
	}

	@Override
	protected int getNextIndex(SortedSet<Integer> excludedIndexes) 
	{
		// TODO: temp
		return sourcePool.getRandomKey(rand, excludedIndexes);
	}
}
