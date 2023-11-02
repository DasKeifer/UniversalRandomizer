package universal_randomizer.randomize;


import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;
import universal_randomizer.user_object_apis.Setter;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class SingleRandomizer<T, P> extends Randomizer<T, T, P, P>
{		
	protected SingleRandomizer(MultiSetter<T, P> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce)
	{
		super(setter, countGetter, enforce);
	}

	// Create a multi setter with count from object
	public static <T2, P2> SingleRandomizer<T2, P2> create(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new SingleRandomizer<>(setter, countGetter, enforce);
	}

	// Create a multi setter with fixed count
	public static <T2, P2> SingleRandomizer<T2, P2> create(MultiSetter<T2, P2> setter, int count, EnforceParams<T2> enforce)
	{
		return create(setter, o -> count, enforce);
	}

	// Create a single setter
	public static <T2, P2> SingleRandomizer<T2, P2> create(MultiSetter<T2, P2> setter, EnforceParams<T2> enforce)
	{
		return create(setter, 1, enforce);
	}
	
	// Create a single setter
	public static <T2, P2> SingleRandomizer<T2, P2> create(Setter<T2, P2> setter, EnforceParams<T2> enforce)
	{
		if (setter == null)
		{
			return null;
		}
		return create(Setter.asMultiSetter(setter), 1, enforce);
	}

	public static <T2, P2> SingleRandomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter)
	{
		return create(setter, countGetter, null);
	}
	
	public static <T2, P2> SingleRandomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter, int count)
	{
		return create(setter, o -> count, null);
	}
	
	public static <T2, P2> SingleRandomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter)
	{
		return create(setter, null);
	}
	
	public static <T2, P2> SingleRandomizer<T2, P2> createNoEnforce(Setter<T2, P2> setter)
	{
		return create(setter, null);
	}

	@Override	
	protected boolean assignAndCheckEnforce(T obj, P poolValue, int count)
	{
		return getSetter().setReturn(obj, poolValue, count) && getEnforceActions().evaluateEnforce(obj);
	}
}