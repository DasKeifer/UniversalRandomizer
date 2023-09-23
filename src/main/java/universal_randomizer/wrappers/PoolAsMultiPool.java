package universal_randomizer.wrappers;


import universal_randomizer.pool.RandomizerMultiPool;

public interface PoolAsMultiPool<K, T> extends RandomizerMultiPool<K, T>
{
	public default void setPool(K obj, int count)
	{
		// Do nothing
	}
}