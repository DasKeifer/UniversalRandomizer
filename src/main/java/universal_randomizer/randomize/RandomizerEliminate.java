package universal_randomizer.randomize;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.wrappers.ReflectionObject;

public class RandomizerEliminate<T, P> extends Randomizer<T, P> 
{	
	private EliminateParams poolEnforceActions;
	
	// Internal tracking
	private List<Pool<P>> workingPools;
	private int lastPeekedIndex;
	
	protected RandomizerEliminate(String pathToField, Pool<P> pool, Random rand, EnforceParams<T> enforce, EliminateParams poolEnforce)
	{
		super(pathToField, pool, rand, enforce);

		if (poolEnforce != null)
		{			
			this.poolEnforceActions = poolEnforce.copy();
		}
		else
		{
			this.poolEnforceActions = EliminateParams.createNoAdditionalPools();
		}
		
		workingPools = new ArrayList<>();
		lastPeekedIndex = -1;
	}
	
	public static <V, S> RandomizerEliminate<V, S> create(String pathToField, Pool<S> pool, Random rand, EnforceParams<V> enforce, EliminateParams poolEnforce)
	{
		return new RandomizerEliminate<>(pathToField, pool, rand, enforce, poolEnforce);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createWithPoolNoEnforce(String pathToField, Pool<S> pool, Random rand)
	{
		return new RandomizerEliminate<>(pathToField, pool, rand, null, null);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createPoolFromStream(String pathToField, Random rand, EnforceParams<V> enforce, EliminateParams poolEnforce)
	{
		return new RandomizerEliminate<>(pathToField, null, rand, enforce, poolEnforce);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createPoolFromStreamNoEnforce(String pathToField, Random rand)
	{
		return new RandomizerEliminate<>(pathToField, null, rand, null, null);
	}
	
	@Override
	protected boolean attemptAssignValue(ReflectionObject<T> obj)
	{
		boolean success = false;
		clearPoolLocation();
		// We do a separate loop to make sure we don't get an index
		// past the last pool
		while (nextPool())
		{
			success = super.attemptAssignValue(obj);
			if (success)
			{
				break;
			}
		}
		return success;
	}

	@Override
	protected void resetPool() 
	{
		for (Pool<P> pool : workingPools)
		{
			pool.reset();
		}
		lastPeekedIndex = -1;
	}

	@Override
	protected P peekNext(Random rand) 
	{
		if (lastPeekedIndex >= 0)
		{
			// Get an item from the next pool
			return workingPools.get(lastPeekedIndex).peek(rand);
		}
		else
		{
			// TODO: Warning of unset pool and whatnot
		}
		return null;
	}

	@Override
	protected void selectPeeked() 
	{
		if (lastPeekedIndex >= 0)
		{
			workingPools.get(lastPeekedIndex).popPeeked();
		}
		clearPoolLocation();
	}
	
	protected boolean nextPool()
	{
		if (pool == null || lastPeekedIndex >= poolEnforceActions.getMaxDepth() - 1)
		{
			return false;
		}
		lastPeekedIndex++;
		
		// If we ran out of pools, add a new one
		if (lastPeekedIndex >= workingPools.size())
		{
			if (workingPools.size() == 0)
			{
				workingPools.add(pool);
			}
			else
			{
				workingPools.add(pool.copy());
			}
		}
		return true;
	}

	private void clearPoolLocation() 
	{
		for (int peeked = 0; peeked <= lastPeekedIndex; peeked++)
		{
			workingPools.get(peeked).resetPeeked();
		}
		lastPeekedIndex = -1;
	}
}
