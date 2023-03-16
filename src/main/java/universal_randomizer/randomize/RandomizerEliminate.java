package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.wrappers.ReflectionObject;

public class RandomizerEliminate<T, P> extends Randomizer<T, P> {

	// TODO: Relook at constructors
	
	PoolEnforceActions poolEnforceActions;
	List<Pool<P>> workingPools;
	int lastPeekedIndex;
	
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
	protected boolean attemptAssignValue(ReflectionObject<T> obj)
	{
		boolean success = false;
		clearPoolLocation();
		for (int pool = 0; pool < poolEnforceActions.getMaxDepth(); pool++)
		{
			nextPool();
			success = super.attemptAssignValue(obj);
			if (success)
			{
				break;
			}
		}
		return success;
	}

	// TODO: Keep instead of clearing pools - we have
	// that functionality in pools already
	
	@Override
	protected void resetPool() 
	{
		workingPools.clear();
		lastPeekedIndex = -1;
	}

	@Override
	protected P peekNext(Random rand) 
	{
		// TODO: Warning of unset pool and whatnot
		if (lastPeekedIndex >= 0 && lastPeekedIndex < workingPools.size())
		{
			// Get an item from the next pool
			return workingPools.get(lastPeekedIndex).peek(rand);
		}
		return null;
	}

	@Override
	protected void selectPeeked() 
	{
		if (lastPeekedIndex >= 0 && lastPeekedIndex < workingPools.size())
		{
			workingPools.get(lastPeekedIndex).popPeeked();
			if (workingPools.isEmpty())
			{
				workingPools.remove(lastPeekedIndex);
			}
		}
		clearPoolLocation();
	}
	
	protected boolean nextPool()
	{
		lastPeekedIndex++;
		if (lastPeekedIndex >= poolEnforceActions.getMaxDepth())
		{
			return false;
		}
		
		// If we ran out of pools, add a new one
		if (lastPeekedIndex >= workingPools.size())
		{
			workingPools.add(Pool.createCopy(pool));
		}
		return true;
	}

	private void clearPoolLocation() 
	{
		for (int peeked = 0; peeked <= lastPeekedIndex && peeked < workingPools.size(); peeked++)
		{
			workingPools.get(peeked).resetPeeked();
		}
		lastPeekedIndex = -1;
	}
}
