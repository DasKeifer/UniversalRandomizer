package universal_randomizer.randomize;


import java.util.Collection;
import java.util.Iterator;

import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;
import universal_randomizer.user_object_apis.Setter;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class SetRandomizer<T extends Collection<O>, O, P extends Collection<S>, S> extends Randomizer<T, O, P, S>
{		
	protected SetRandomizer(MultiSetter<O, S> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce)
	{
		super(setter, countGetter, enforce);
	}

	// Create a multi setter with count from object
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	create(MultiSetter<O2, S2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new SetRandomizer<>(setter, countGetter, enforce);
	}

	// Create a multi setter with fixed count
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	create(MultiSetter<O2, S2> setter, int count, EnforceParams<T2> enforce)
	{
		return create(setter, o -> count, enforce);
	}
	
	// Create a single setter where we set the whole collection at once
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	create(MultiSetter<O2, S2> setter, EnforceParams<T2> enforce)
	{
		return create(setter, 1, enforce);
	}
	
	// Create a single setter where we set the whole collection at once
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	create(Setter<O2, S2> setter, EnforceParams<T2> enforce)
	{
		if (setter == null)
		{
			return null;
		}
		return create(Setter.asMultiSetter(setter), 1, enforce);
	}

	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	createNoEnforce(MultiSetter<O2, S2> setter, Getter<T2, Integer> countGetter)
	{
		return create(setter, countGetter, null);
	}
	
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	createNoEnforce(MultiSetter<O2, S2> setter, int count)
	{
		return create(setter, o -> count, null);
	}
	
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	createNoEnforce(MultiSetter<O2, S2> setter)
	{
		return create(setter, null);
	}
	
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	createNoEnforce(Setter<O2, S2> setter)
	{
		return create(setter, null);
	}

	@Override	
	protected boolean assignAndCheckEnforce(T objs, P poolValue, int count)
	{
		boolean success = true;
		Iterator<O> objItr = objs.iterator();
		Iterator<S> valItr = poolValue.iterator();
		while (objItr.hasNext())
		{
			O obj = objItr.next();
			success = success && getSetter().setReturn(obj, valItr.next(), count);
		}
		
		if (success)
		{
			success = getEnforceActions().evaluateEnforce(objs);
		}
		return success;
	}
}