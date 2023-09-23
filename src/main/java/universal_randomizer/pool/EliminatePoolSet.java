package universal_randomizer.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EliminatePoolSet<T> implements RandomizerBasicPool<T> 
{	
	private int maxDepth;
	
	// Internal tracking
	private List<PeekPool<T>> workingPools;
	private int currentPool;
	
	protected EliminatePoolSet(PeekPool<T> sourcePool, int maxDepth)
	{		
		this.maxDepth = maxDepth;		
		workingPools = new ArrayList<>(maxDepth);
		workingPools.add(sourcePool.copy());
		workingPools.get(0).reset();
		currentPool = 0;
	}
	
	public static <T2> EliminatePoolSet<T2> create(PeekPool<T2> sourcePool, int maxDepth)
	{
		if (sourcePool == null)
		{
			return null;
		}
		return new EliminatePoolSet<>(sourcePool, maxDepth);
	}
	
	public static <T2> EliminatePoolSet<T2> createNoAdditionalPools(PeekPool<T2> sourcePool)
	{
		return create(sourcePool, 1);
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
		if (currentPool >= maxDepth - 1)
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
