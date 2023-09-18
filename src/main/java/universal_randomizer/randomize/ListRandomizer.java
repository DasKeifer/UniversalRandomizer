package universal_randomizer.randomize;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.PeekPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;
import universal_randomizer.user_object_apis.Setter;

public abstract class ListRandomizer<T, C extends Collection<T>, K, P, V extends Collection<P>>
{		
	private Getter<C, K> keyGetter;
	private Map<K, PeekPool<V>> poolMap;

	protected abstract void resetPool();
	protected abstract V peekNext(Random rand, PeekPool<V> pool);
	protected abstract void selectPeeked();
	
	protected ListRandomizer(Setter<T, P> setter, EnforceParams<T> enforce)
	{
	}
	
	public boolean perform(Stream<Collection<T>> objStream, Map<K, PeekPool<Collection<P>>> pool) 
	{
		return perform(objStream, pool, null);
	}
	
	public boolean perform(Stream<C> objStream, Map<K, PeekPool<V>> pool, Random rand) 
	{
		this.poolMap = pool;
		if (rand == null && this.rand == null)
		{
			this.rand = new Random();
		}
		
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<Collection<T>> streamAsList = objStream.toList();
		return attemptRandomization(streamAsList);
	}
	
	// Handles RESET
	protected boolean attemptRandomization(List<T> streamAsList)
	{		
		// Attempt to assign randomized values for each item in the stream
		List<T> failed = randomize(streamAsList.stream());
		
		// While we have failed and have resets left
		for (int reset = 0; reset < enforceActions.getMaxResets(); reset++)
		{
			if (failed.isEmpty())
			{
				break;
			}
			resetPool();
			failed = randomize(streamAsList.stream());
		}
		
		return failed.isEmpty();
	}

	protected List<T> randomize(Stream<T> objStream)
	{
		return objStream.filter(this::assignValueNegated).toList();
	}
	
	protected boolean assignValueNegated(T obj)
	{
		return !assignValue(obj);
	}
	
	protected boolean assignValue(C objs)
	{
		PeekPool<V> pool = poolMap.get(keyGetter.get(objs));
		return attemptAssignValue(objs, pool);
	}

	protected boolean attemptAssignValue(C objs, PeekPool<V> pool)
	{
		V selectedVal = peekNext(getRandom(), pool);
		
		// While its a good index and fails the enforce check, retry if
		// we have attempts left
		boolean success = assignAndCheckEnforce(objs, selectedVal);
		for (int retry = 0; retry < getEnforceActions().getMaxRetries(); retry++)
		{
			if (success || selectedVal == null)
			{
				break;
			}
			selectedVal = peekNext(getRandom(), pool);
			success = assignAndCheckEnforce(objs, selectedVal);
		}
		
		if (success)
		{
			selectPeeked();
		}
		
		return success;
	}
	
	protected boolean assignAndCheckEnforce(C objs, V value)
	{
		boolean success = true;
		Iterator<T> objItr = objs.iterator();
		Iterator<P> valItr = value.iterator();
		while (objItr.hasNext())
		{
			T obj = objItr.next();
			success = success && getSetter().setReturn(obj, valItr.next()) && getEnforceActions().evaluateEnforce(obj);
		}
		return success;
	}

	protected Setter<T, P> getSetter() 
	{
		return setter;
	}

	protected Random getRandom() 
	{
		return rand;
	}

	protected Map<K, PeekPool<V>> getPool() 
	{
		return poolMap;
	}

	protected EnforceParams<T> getEnforceActions() 
	{
		return enforceActions;
	}
}