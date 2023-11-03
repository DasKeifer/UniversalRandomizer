package universal_randomizer.randomize;


import java.util.Collection;
import java.util.Iterator;

import universal_randomizer.user_object_apis.MultiSetter;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times, once
/// for each item in the collection and only verifies the object after all setting is
/// done
public class MultiRandomizer<T, P extends Collection<S>, S> extends Randomizer<T, T, P, S>
{	
	private static int return1(Object o)
	{
		// We have this function so we don't have to keep creating lambda
		// functions. Ignore sonar lint saying to use a constant since
		// we need the function signature for this to work
		return 1; // NOSONAR
	}
	
	protected MultiRandomizer(MultiSetter<T, S> setter, EnforceParams<T> enforce)
	{
		super(setter, MultiRandomizer::return1, enforce);
	}
	
	// Create a single setter - where P is a collection of S
	// Create a single setter - S must match P
	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	create(MultiSetter<T2, S2> setter, EnforceParams<T2> enforce)
	{
		if (setter == null)
		{
			return null;
		}
		return new MultiRandomizer<>(setter, enforce);
	}
	
	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	createNoEnforce(MultiSetter<T2, S2> setter)
	{
		return create(setter, null);
	}

	@Override	
	protected boolean assignAndCheckEnforce(T obj, P poolValue, int count)
	{
		boolean success = true;
		Iterator<S> valItr = poolValue.iterator();
		int counter = 0;
		while (valItr.hasNext() && success)
		{
			success = getSetter().setReturn(obj, valItr.next(), counter++);
		}
		
		if (success)
		{
			success = getEnforceActions().evaluateEnforce(obj);
		}
		return success;
	}
}