package universal_randomizer.randomize;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.pool.RandomizerPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;

public abstract class ListRandomizer<T, C extends Collection<T>, K, P, V extends Collection<P>>
{		
	private Setter<T, P> setter;
	private Getter<C, K> keyGetter;
	private Map<K, RandomizerPool<V>> poolMap;
	private Random rand;
	private EnforceParams<T> enforceActions;
	
	protected ListRandomizer(Setter<T, P> setter, EnforceParams<T> enforce)
	{
	}
	
	public boolean perform(Stream<C> objStream, Map<K, RandomizerPool<V>> pool) 
	{
		return perform(objStream, pool, null);
	}
	
	public boolean perform(Stream<C> objStream, Map<K, RandomizerPool<V>> pool, Random rand) 
	{
		this.poolMap = pool;
		if (rand == null && this.rand == null)
		{
			this.rand = new Random();
		}
		
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<C> streamAsList = objStream.toList();
		return attemptRandomization(streamAsList);
	}
	
	// Handles RESET
	protected boolean attemptRandomization(List<C> streamAsList)
	{		
		// Attempt to assign randomized values for each item in the stream
		List<C> failed = randomize(streamAsList.stream());
		
		// While we have failed and have resets left
		for (int reset = 0; reset < enforceActions.getMaxResets(); reset++)
		{
			if (failed.isEmpty())
			{
				break;
			}
			
			for (RandomizerPool<V> pool : poolMap.values())
			{
				pool.reset();
			}
			failed = randomize(streamAsList.stream());
		}
		
		return failed.isEmpty();
	}

	protected List<C> randomize(Stream<C> objStream)
	{
		return objStream.filter(this::assignValueNegated).toList();
	}
	
	protected boolean assignValueNegated(C obj)
	{
		return !assignValue(obj);
	}
	
	protected boolean assignValue(C objs)
	{
		RandomizerPool<V> pool = poolMap.get(keyGetter.get(objs));
		boolean success = false;
		do 
		{
			if (attemptAssignValue(objs, pool))
			{
				success = true;
				break;
			}
		} while (pool.useNextPool());		
		
		return success;
	}

	protected boolean attemptAssignValue(C objs, RandomizerPool<V> pool)
	{
		V selectedVal = pool.peek(rand);
		
		// While its a good index and fails the enforce check, retry if
		// we have attempts left
		boolean success = assignAndCheckEnforce(objs, selectedVal);
		for (int retry = 0; retry < enforceActions.getMaxRetries(); retry++)
		{
			if (success || selectedVal == null)
			{
				break;
			}
			selectedVal = pool.peek(rand);
			success = assignAndCheckEnforce(objs, selectedVal);
		}
		
		if (success)
		{
			pool.selectPeeked();
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
			success = success && setter.setReturn(obj, valItr.next()) && enforceActions.evaluateEnforce(obj);
		}
		return success;
	}
}