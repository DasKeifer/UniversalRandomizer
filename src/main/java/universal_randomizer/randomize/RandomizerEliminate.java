package universal_randomizer.randomize;

import java.util.List;
import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.wrappers.ReflectionObject;

public class RandomizerEliminate<T, P> extends Randomizer<T, P> 
{	
	private PoolEnforceActions poolEnforceActions;
	
	// Internal tracking
	private List<Pool<P>> workingPools;
	private int lastPeekedIndex;
	
	protected RandomizerEliminate(String pathToField, Pool<P> pool, Random rand, EnforceActions<T> enforce, PoolEnforceActions poolEnforce)
	{
		super(pathToField, pool, rand, enforce);

		if (poolEnforce == null || enforce == null)
		{
			if (enforce == null)
			{
				//TODO: Error? Warning?
			}
			
			this.poolEnforceActions = PoolEnforceActions.createNone();
		}
		else
		{
			this.poolEnforceActions = poolEnforce.copy();
		}
	}
	
	public static <V, S> RandomizerEliminate<V, S> create(String pathToField, Pool<S> pool, Random rand, EnforceActions<V> enforce, PoolEnforceActions poolEnforce)
	{
		return new RandomizerEliminate<>(pathToField, pool, rand, enforce, poolEnforce);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createPoolFromStreamWithEnforce(String pathToField)
	{
		return new RandomizerEliminate<>(pathToField, null, null, null, null);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createWithPoolAndEnforce(String pathToField, Pool<S> pool)
	{
		return new RandomizerEliminate<>(pathToField, pool, null, null, null);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createPoolFromStreamWithEnforce(String pathToField, EnforceActions<V> enforce, PoolEnforceActions poolEnforce)
	{
		return new RandomizerEliminate<>(pathToField, null, null, enforce, poolEnforce);
	}
	
	public static <V, S> RandomizerEliminate<V, S> createWithPoolAndEnforce(String pathToField, Pool<S> pool, EnforceActions<V> enforce, PoolEnforceActions poolEnforce)
	{
		return new RandomizerEliminate<>(pathToField, pool, null, enforce, poolEnforce);
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

	@Override
	protected void resetPool() 
	{
		if (lastPeekedIndex >= 0)
		{
			for (Pool<P> pool : workingPools)
			{
				pool.reset();
			}
			lastPeekedIndex = -1;
		}
	}

	@Override
	protected P peekNext(Random rand) 
	{
		if (lastPeekedIndex >= 0 && lastPeekedIndex < workingPools.size())
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
			workingPools.add(pool.copy());
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
