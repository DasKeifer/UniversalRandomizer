package universal_randomizer.randomize;

import java.util.Random;

import universal_randomizer.Pool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.user_object_apis.SetterNoReturn;

public class RandomizerReuse<T, P> extends Randomizer<T, P> 
{
	protected RandomizerReuse(Setter<T, P> setter, Pool<P> pool, Getter<T, P> poolGetter, EnforceParams<T> enforce)
	{
		super(setter, pool, poolGetter, enforce);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> create(Setter<T2, P2> setter, Pool<P2> pool, EnforceParams<T2> enforce)
	{
		if (setter == null || pool == null)
		{
			return null;
		}
		return new RandomizerReuse<>(setter, pool, null, enforce);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> create(SetterNoReturn<T2, P2> setter, Pool<P2> pool, EnforceParams<T2> enforce)
	{
		return create((Setter<T2, P2>)setter, pool, enforce);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> createWithPoolNoEnforce(Setter<T2, P2> setter, Pool<P2> pool)
	{
		return create(setter, pool, null);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> createWithPoolNoEnforce(SetterNoReturn<T2, P2> setter, Pool<P2> pool)
	{
		return createWithPoolNoEnforce((Setter<T2, P2>)setter, pool);
	}

	public static <T2, P2> RandomizerReuse<T2, P2> createPoolFromStream(Setter<T2, P2> setter, Getter<T2, P2> poolGetter, EnforceParams<T2> enforce)
	{
		if (setter == null || poolGetter == null)
		{
			return null;
		}
		return new RandomizerReuse<>(setter, null, poolGetter, enforce);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> createPoolFromStream(SetterNoReturn<T2, P2> setter, Getter<T2, P2> poolGetter, EnforceParams<T2> enforce)
	{
		return createPoolFromStream((Setter<T2, P2>)setter, poolGetter, enforce);
	}

	public static <T2, P2> RandomizerReuse<T2, P2> createPoolFromStreamNoEnforce(Setter<T2, P2> setter, Getter<T2, P2> poolGetter)
	{
		return createPoolFromStream(setter, poolGetter, null);
	}

	public static <T2, P2> RandomizerReuse<T2, P2> createPoolFromStreamNoEnforce(SetterNoReturn<T2, P2> setter, Getter<T2, P2> poolGetter)
	{
		return createPoolFromStreamNoEnforce((Setter<T2, P2>)setter, poolGetter);
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
