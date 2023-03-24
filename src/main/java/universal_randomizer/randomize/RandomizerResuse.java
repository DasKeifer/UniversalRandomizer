package universal_randomizer.randomize;

import java.util.Random;

import universal_randomizer.Pool;

public class RandomizerResuse<T, P> extends Randomizer<T, P> 
{
	private RandomizerResuse(String pathToField, Pool<P> pool, Random rand, EnforceActions<T> enforce)
	{
		super(pathToField, pool, rand, enforce);
	}
	
	public static <V, S> RandomizerResuse<V, S> create(String pathToField, Pool<S> pool, Random rand, EnforceActions<V> enforce)
	{
		return new RandomizerResuse<>(pathToField, pool, rand, enforce);
	}
	
	public static <V, S> RandomizerResuse<V, S> createWithPoolAndEnforce(String pathToField, Pool<S> pool, Random rand)
	{
		return new RandomizerResuse<>(pathToField, pool, rand, null);
	}
	
	public static <V, S> RandomizerResuse<V, S> createPoolFromStream(String pathToField, Random rand, EnforceActions<V> enforce)
	{
		return new RandomizerResuse<>(pathToField, null, rand, enforce);
	}

	public static <V, S> RandomizerResuse<V, S> createPoolFromStreamNoEnforce(String pathToField, Random rand)
	{
		return new RandomizerResuse<>(pathToField, null, rand, null);
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
