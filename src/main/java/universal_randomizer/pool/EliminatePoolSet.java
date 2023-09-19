package universal_randomizer.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import universal_randomizer.randomize.EliminateParams;

public class EliminatePoolSet<T> implements RandomizerPool<T> 
{	
	private EliminateParams poolEnforceActions;
	
	// Internal tracking
	private PeekPool<T> sourcePool;
	private List<PeekPool<T>> workingPools;
	private int currentPool;
	
	protected EliminatePoolSet(EliminateParams poolEnforce)
	{		
		if (poolEnforce != null)
		{			
			this.poolEnforceActions = poolEnforce;
		}
		else
		{
			this.poolEnforceActions = EliminateParams.createNoAdditionalPools();
		}
		
		workingPools = new ArrayList<>(poolEnforceActions.getMaxDepth());
		workingPools.add(sourcePool.copy());
		workingPools.get(0).reset();
		currentPool = 0;
	}

	@Override
	public void reset() 
	{
		for (PeekPool<T> pool : workingPools)
		{
			pool.reset();
		}
		currentPool = 0;
	}

	@Override
	public T peek(Random rand) 
	{
		if (currentPool < workingPools.size())
		{
			// Get an item from the next pool
			return workingPools.get(currentPool).peek(rand);
		}
		return null;
	}

	@Override
	public T selectPeeked() 
	{
		T popped = null;
		if (currentPool < workingPools.size())
		{
			popped = workingPools.get(currentPool).popPeeked();
		}
		resetPeeked();
		return popped;
	}
	
	@Override
	public boolean useNextPool()
	{
		if (currentPool >= poolEnforceActions.getMaxDepth() - 1)
		{
			return false;
		}
		currentPool++;
		
		// If we ran out of pools, add a new one
		if (currentPool >= workingPools.size())
		{
			workingPools.add(workingPools.get(currentPool - 1));
			workingPools.get(currentPool).reset();
		}
		return true;
	}

	@Override
	public void resetPeeked() 
	{
		for (int peeked = 0; peeked <= currentPool; peeked++)
		{
			workingPools.get(peeked).resetPeeked();
		}
		currentPool = 0;
	}
}
