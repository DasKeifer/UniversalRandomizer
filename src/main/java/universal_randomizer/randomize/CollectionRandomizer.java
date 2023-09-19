package universal_randomizer.randomize;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import universal_randomizer.pool.RandomizerPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;

public class CollectionRandomizer<T, C extends Collection<T>, K, P, V extends Collection<P>> 
	extends Randomizer<C, Map<K, RandomizerPool<V>>>
{		
	private Setter<T, P> setter;
	private Getter<C, K> keyGetter;
	
	protected CollectionRandomizer(Setter<T, P> setter, Getter<C, K> keyGetter, EnforceParams<C> enforce)
	{
		super(enforce);
		
		this.setter = setter;
		this.keyGetter = keyGetter;
	}

	public static <T2, C2 extends Collection<T2>, K2, P2, V2 extends Collection<P2>> 
		CollectionRandomizer<T2, C2, K2, P2, V2> create(Setter<T2, P2> setter, Getter<C2, K2> keyGetter, EnforceParams<C2> enforce)
	{
		if (setter == null || keyGetter == null)
		{
			return null;
		}
		return new CollectionRandomizer<>(setter, keyGetter, enforce);
	}

	public static <T2, C2 extends Collection<T2>, K2, P2, V2 extends Collection<P2>> 
		CollectionRandomizer<T2, C2, K2, P2, V2>  createNoEnforce(Setter<T2, P2> setter, Getter<C2, K2> keyGetter)
	{
		return create(setter, keyGetter, null);
	}
	
	@Override
	protected void resetPool() 
	{
		for (RandomizerPool<V> pool : getPool().values())
		{
			pool.reset();
		}
	}
	
	@Override
	protected boolean assignValue(C objs)
	{
		RandomizerPool<V> pool = getPool().get(keyGetter.get(objs));
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
		V selectedVal = pool.peek(getRandom());
		
		// While its a good index and fails the enforce check, retry if
		// we have attempts left
		boolean success = assignAndCheckEnforce(objs, selectedVal);
		for (int retry = 0; retry < getEnforceActions().getMaxRetries(); retry++)
		{
			if (success || selectedVal == null)
			{
				break;
			}
			selectedVal = pool.peek(getRandom());
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
			success = success && setter.setReturn(obj, valItr.next());
		}
		
		if (success)
		{
			success = getEnforceActions().evaluateEnforce(objs);
		}
		return success;
	}
}