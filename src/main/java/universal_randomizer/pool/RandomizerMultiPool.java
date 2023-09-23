package universal_randomizer.pool;


public interface RandomizerMultiPool<K, T> extends RandomizerPool<T>
{	
	public void setPool(K obj, int count);
}