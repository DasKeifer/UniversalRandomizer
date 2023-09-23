package universal_randomizer.pool;

import java.util.Map;
import java.util.Random;

import universal_randomizer.user_object_apis.MultiGetter;

public class MultiPool<O, K, T> implements RandomizerMultiPool<O, T> 
{	
	private Map<K, RandomizerPool<T>> poolMap;
	private MultiGetter<O, K> keyGetter;
	private RandomizerPool<T> activePool;
	
	public MultiPool(Map<K, RandomizerPool<T>> poolMap, MultiGetter<O, K> keyGetter)
	{
		this.poolMap = poolMap;
		this.keyGetter = keyGetter;
	}
	
	@Override
	public void setPool(O obj, int count)
	{
		K key = keyGetter.get(obj, count);
		activePool = poolMap.get(key);
	}

	@Override
	public void reset() 
	{
		activePool.reset();
	}

	@Override
	public T peek(Random rand) 
	{
		return activePool.peek(rand);
	}

	@Override
	public T selectPeeked() 
	{
		return activePool.selectPeeked();
	}
	
	@Override
	public boolean useNextPool()
	{
		return activePool.useNextPool();
	}

	@Override
	public void resetPeeked() 
	{
		activePool.resetPeeked();
	}
}
