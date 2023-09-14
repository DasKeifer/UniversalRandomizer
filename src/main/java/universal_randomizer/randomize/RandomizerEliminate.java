package universal_randomizer.randomize;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.user_object_apis.SetterNoReturn;

public class RandomizerEliminate<T, P> extends Randomizer<T, P> 
{	
	private EliminateParams poolEnforceActions;
	
	// Internal tracking
	private List<Pool<P>> workingPools;
	private int lastPeekedIndex;
	
	protected RandomizerEliminate(Setter<T, P> setter, Pool<P> pool, Getter<T, P> poolGetter, EnforceParams<T> enforce, EliminateParams poolEnforce)
	{
		super(setter, pool, poolGetter, enforce);
		
		if (poolEnforce != null)
		{			
			this.poolEnforceActions = poolEnforce;
		}
		else
		{
			this.poolEnforceActions = EliminateParams.createNoAdditionalPools();
		}
		
		workingPools = new ArrayList<>();
		lastPeekedIndex = -1;
	}
	
	public static <T2, P2> RandomizerEliminate<T2, P2> create(Setter<T2, P2> setter, Pool<P2> pool, EnforceParams<T2> enforce, EliminateParams poolEnforce)
	{
		if (setter == null || pool == null)
		{
			return null;
		}
		return new RandomizerEliminate<>(setter, pool, null, enforce, poolEnforce);
	}
	
	public static <T2, P2> RandomizerEliminate<T2, P2> create(SetterNoReturn<T2, P2> setter, Pool<P2> pool, EnforceParams<T2> enforce, EliminateParams poolEnforce)
	{
		return create((Setter<T2, P2>)setter, pool, enforce, poolEnforce);
	}
	
	public static <T2, P2> RandomizerEliminate<T2, P2> createWithPoolNoEnforce(Setter<T2, P2> setter, Pool<P2> pool)
	{
		return create(setter, pool, null, null);
	}
	
	public static <T2, P2> RandomizerEliminate<T2, P2> createWithPoolNoEnforce(SetterNoReturn<T2, P2> setter, Pool<P2> pool)
	{
		return createWithPoolNoEnforce((Setter<T2, P2>)setter, pool);
	}
	
	public static <T2, P2> RandomizerEliminate<T2, P2> createPoolFromStream(Setter<T2, P2> setter, Getter<T2, P2> poolGetter, EnforceParams<T2> enforce, EliminateParams poolEnforce)
	{
		if (setter == null || poolGetter == null)
		{
			return null;
		}
		return new RandomizerEliminate<>(setter, null, poolGetter, enforce, poolEnforce);
	}
	
	public static <T2, P2> RandomizerEliminate<T2, P2> createPoolFromStream(SetterNoReturn<T2, P2> setter, Getter<T2, P2> poolGetter, EnforceParams<T2> enforce, EliminateParams poolEnforce)
	{
		return createPoolFromStream((Setter<T2, P2>)setter, poolGetter, enforce, poolEnforce);
	}
	
	public static <T2, P2> RandomizerEliminate<T2, P2> createPoolFromStreamNoEnforce(Setter<T2, P2> setter, Getter<T2, P2> poolGetter)
	{
		return createPoolFromStream(setter, poolGetter, null, null);
	}
	
	public static <T2, P2> RandomizerEliminate<T2, P2> createPoolFromStreamNoEnforce(SetterNoReturn<T2, P2> setter, Getter<T2, P2> poolGetter)
	{
		return createPoolFromStreamNoEnforce((Setter<T2, P2>)setter, poolGetter);
	}
	
	@Override
	protected boolean attemptAssignValue(T obj)
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
		if (getPool() == null || lastPeekedIndex >= poolEnforceActions.getMaxDepth() - 1)
		{
			return false;
		}
		lastPeekedIndex++;
		
		// If we ran out of pools, add a new one
		if (lastPeekedIndex >= workingPools.size())
		{
			if (workingPools.isEmpty())
			{
				workingPools.add(getPool());
			}
			else
			{
				workingPools.add(getPool().copy());
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
