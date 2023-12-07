package universal_randomizer.randomize;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.utils.StreamUtils;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class DependentRandomizer<T extends Collection<O>, O, P, G> extends Randomizer<T, O, O, P, P>
{		
	private Comparator<? super P> valSorter;
	private Getter<O, G> grouper;
	private Comparator<? super G> groupSorter;
	
	protected DependentRandomizer(MultiSetter<O, P> setter, Getter<T, Integer> countGetter, Comparator<? super P> valSorter, 
			Getter<O, G> grouper, Comparator<? super G> groupSorter, EnforceParams<T> enforce)
	{
		super(setter, countGetter, enforce);
		this.valSorter = valSorter;
		this.grouper = grouper;
		this.groupSorter = groupSorter;
	}

	// Create a multi setter with count from the list
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	create(MultiSetter<O2, P2> setter, Getter<T2, Integer> countGetter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter, EnforceParams<T2> enforce)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new DependentRandomizer<>(setter, countGetter, valSorter, grouper, groupSorter, enforce);
	}

	// Create a multi setter with fixed count
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	create(MultiSetter<O2, P2> setter, int count, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter, EnforceParams<T2> enforce)
	{
		return create(setter, o -> count, valSorter, grouper, groupSorter, enforce);
	}
	
	// Create a single setter
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	create(MultiSetter<O2, P2> setter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter, EnforceParams<T2> enforce)
	{
		return create(setter, 1, valSorter, grouper, groupSorter, enforce);
	}
	
	// Create a single setter where we set the whole collection at once
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	create(Setter<O2, P2> setter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter, EnforceParams<T2> enforce)
	{
		if (setter == null)
		{
			return null;
		}
		return create(Setter.asMultiSetter(setter), 1, valSorter, grouper, groupSorter, enforce);
	}

	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	createNoEnforce(MultiSetter<O2, P2> setter, Getter<T2, Integer> countGetter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter)
	{
		return create(setter, countGetter, valSorter, grouper, groupSorter, null);
	}
	
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	createNoEnforce(MultiSetter<O2, P2> setter, int count, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter)
	{
		return create(setter, o -> count, valSorter, grouper, groupSorter, null);
	}
	
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	createNoEnforce(MultiSetter<O2, P2> setter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter)
	{
		return create(setter, valSorter, grouper, groupSorter, null);
	}
	
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	createNoEnforce(Setter<O2, P2> setter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter)
	{
		return create(setter, valSorter, grouper, groupSorter, null);
	}
	
	@Override
	protected boolean attemptAssignValue(T obj, int count)
	{
		boolean success = false;		
		
		while (!success) // Loop on pool depth (if pool supports it)
		{
			for (int retry = 0; retry <= getEnforceActions().getMaxRetries() && !success; retry++)
			{
				success = attemptAssignValuePass(obj, count);
			}
			
			if (!success)
			{
				getPool().peekNewBatch();
				if (!getPool().useNextPool())
				{
					break;
				}
			}
		}
		
		// Select the peeked items
		if (success)
		{
			getPool().selectPeeked();
		}
		
		return success;
	}
	
	protected boolean attemptAssignValuePass(T obj, int count)
	{
		boolean success = true;
		
		// Get the values for each object
		List<P> vals = new ArrayList<>(obj.size());
		for (O item : obj)
		{
			// Set the mutlipool (if we are using one)
			if (getMultiPool() != null)
			{
				getMultiPool().setPool(item, count);
			}
			P val = getPool().peekBatch(getRandom());
			if (val == null)
			{
				return false;
			}
			vals.add(val);
		}
		
		// Sort the values as appropriate
		vals.sort(valSorter);
		
		// Group the object being randomized (in sorted order)
		SortedMap<G, List<O>> groupedObjs = StreamUtils.sortedGroup(obj.stream(), grouper, groupSorter);
		
		// break val into groups by index/count
		int startIdx = 0;
		for (List<O> objs : groupedObjs.values())
		{
			List<P> groupVals = vals.subList(startIdx, startIdx + objs.size());
			// try set each group
			success = assignAndCheckEnforce(objs, groupVals, count);
			startIdx += objs.size();
		}
		
		// Check the enforce condition (if there is one)
		if (success)
		{
			success = getEnforceActions().evaluateEnforce(obj);
		}
		
		return success;
	}

	protected boolean assignAndCheckEnforce(List<O> objs, List<P> poolValue, int count)
	{
		boolean success = true;
		Iterator<O> objItr = objs.iterator();
		Iterator<P> valItr = poolValue.iterator();
		while (objItr.hasNext() && success)
		{
			O obj = objItr.next();
			success = getSetter().setReturn(obj, valItr.next(), count);
		}
		return success;
	}
}