package universal_randomizer.randomize;


import java.util.Collection;
import java.util.Iterator;

import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class MultiRandomizer<T, P extends Collection<S>, S> extends Randomizer<T, T, P, S>
{	
	protected MultiRandomizer(MultiSetter<T, S> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce)
	{
		super(setter, countGetter, enforce);
	}

	// Create a multi setter with count from object
	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	create(MultiSetter<T2, S2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new MultiRandomizer<>(setter, countGetter, enforce);
	}

	// Create a multi setter with fixed count
	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	create(MultiSetter<T2, S2> setter, int count, EnforceParams<T2> enforce)
	{
		return create(setter, o -> count, enforce);
	}
	
	// Create a single setter - where P is a collection of S
	// Create a single setter - S must match P
	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	create(MultiSetter<T2, S2> setter, EnforceParams<T2> enforce)
	{
		return create(setter, 1, enforce);
	}

	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	createNoEnforce(MultiSetter<T2, S2> setter, Getter<T2, Integer> countGetter)
	{
		return create(setter, countGetter, null);
	}
	
	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	createNoEnforce(MultiSetter<T2, S2> setter, int count)
	{
		return create(setter, o -> count, null);
	}
	
	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	createNoEnforce(MultiSetter<T2, S2> setter)
	{
		return create(setter, 1, null);
	}

	@Override	
	protected boolean assignAndCheckEnforce(T obj, P poolValue, int count)
	{
		boolean success = true;
		Iterator<S> valItr = poolValue.iterator();
		int counter = 0;
		while (valItr.hasNext())
		{
			success = success && getSetter().setReturn(obj, valItr.next(), counter++);
		}
		
		if (success)
		{
			success = getEnforceActions().evaluateEnforce(obj);
		}
		return success;
	}
}