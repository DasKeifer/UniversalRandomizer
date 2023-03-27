package universal_randomizer.randomize;

import java.util.Random;

import universal_randomizer.Pool;

public class RandomizerReuse<T, P> extends Randomizer<T, P> 
{
	private RandomizerReuse(String pathToField, Pool<P> pool, Random rand, EnforceParams<T> enforce)
	{
		super(pathToField, pool, rand, enforce);
	}
	
	public static <V, S> RandomizerReuse<V, S> create(String pathToField, Pool<S> pool, Random rand, EnforceParams<V> enforce)
	{
		return new RandomizerReuse<>(pathToField, pool, rand, enforce);
	}
	
	public static <V, S> RandomizerReuse<V, S> createWithPoolNoEnforce(String pathToField, Pool<S> pool, Random rand)
	{
		return new RandomizerReuse<>(pathToField, pool, rand, null);
	}
	
	public static <V, S> RandomizerReuse<V, S> createPoolFromStream(String pathToField, Random rand, EnforceParams<V> enforce)
	{
		return new RandomizerReuse<>(pathToField, null, rand, enforce);
	}

	public static <V, S> RandomizerReuse<V, S> createPoolFromStreamNoEnforce(String pathToField, Random rand)
	{
		return new RandomizerReuse<>(pathToField, null, rand, null);
	}

	@Override
	protected void resetPool() 
	{
		pool.reset();
	}

	@Override
	protected P peekNext(Random rand) 
	{
		 return pool.peek(rand);
	}

	@Override
	protected void selectPeeked() 
	{
		pool.selectPeeked();
	}
}
