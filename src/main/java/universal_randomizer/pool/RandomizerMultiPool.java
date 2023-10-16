package universal_randomizer.pool;


public interface RandomizerMultiPool<K, T> extends RandomizerPool<T>
{	
	public boolean setPool(K obj, int count);
}