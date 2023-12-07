package universal_randomizer.randomize;

import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;

public abstract class OneToOneRandomizer<T, O, P, S> extends Randomizer<T, T, O, P, S>
{	
	protected OneToOneRandomizer(MultiSetter<O, S> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce) 
	{
		super(setter, countGetter, enforce);
	}

	@Override
	protected boolean attemptAssignValue(T obj, int count)
	{
		// Set the pool by the key
		if (getMultiPool() != null)
		{
			getMultiPool().setPool(obj, count);
		}

		boolean success = false;		
		
		do // Loop on pool depth (if pool supports it)
		{			
			// While its a good index and fails the enforce check, retry if
			// we have attempts left. <= since the first pass doesn't count as
			// a retry
			for (int retry = 0; retry <= getEnforceActions().getMaxRetries() && !success; retry++)
			{
				// Get the next item and try it
				// GR: Loop Here?
				// Group Obj.list by grouper
				// get val x count
				// sort with sorter (or natural order)
				// break val into groups by index/count
				// try set each group
				P selectedVal = getPool().peek(getRandom());
				if (selectedVal == null)
				{
					break;
				}
				success = assignAndCheckEnforce(obj, selectedVal, count);
			}
		} while (!success && getPool().useNextPool());	

		if (success)
		{
			getPool().selectPeeked();
		}		
		
		return success;
	}
	
	protected abstract boolean assignAndCheckEnforce(T obj, P poolValue, int count);
}