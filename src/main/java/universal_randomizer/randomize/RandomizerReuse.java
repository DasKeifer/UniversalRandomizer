package universal_randomizer.randomize;

import java.util.Random;

import universal_randomizer.Pool;

public class RandomizerReuse<T, P> extends Randomizer<T, P> 
{
	protected RandomizerReuse(String pathToField, Pool<P> pool, EnforceParams<T> enforce)
	{
		super(pathToField, pool, enforce);
	}
	
	public static <V, S> RandomizerReuse<V, S> create(String pathToField, Pool<S> pool, EnforceParams<V> enforce)
	{
		return new RandomizerReuse<>(pathToField, pool, enforce);
	}
	
	public static <V, S> RandomizerReuse<V, S> createWithPoolNoEnforce(String pathToField, Pool<S> pool)
	{
		return new RandomizerReuse<>(pathToField, pool, null);
	}
	
	public static <V, S> RandomizerReuse<V, S> createPoolFromStream(String pathToField, EnforceParams<V> enforce)
	{
		return new RandomizerReuse<>(pathToField, null, enforce);
	}

	public static <V, S> RandomizerReuse<V, S> createPoolFromStreamNoEnforce(String pathToField)
	{
		return new RandomizerReuse<>(pathToField, null, null);
	}

	@Override
	protected void resetPool() 
	{
		getPool().reset();
	}

	@Override
	protected P peekNext(Random rand) 
	{
		 return getPool().peek(rand);
	}

	@Override
	protected void selectPeeked() 
	{
		getPool().selectPeeked();
	}
}
