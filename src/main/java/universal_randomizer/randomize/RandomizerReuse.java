package universal_randomizer.randomize;

import java.util.Random;

import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.user_object_apis.SetterNoReturn;

public class RandomizerReuse<T, P> extends Randomizer<T, P> 
{
	protected RandomizerReuse(Setter<T, P> setter, EnforceParams<T> enforce)
	{
		super(setter, enforce);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> create(Setter<T2, P2> setter, EnforceParams<T2> enforce)
	{
		if (setter == null)
		{
			return null;
		}
		return new RandomizerReuse<>(setter, enforce);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> create(SetterNoReturn<T2, P2> setter, EnforceParams<T2> enforce)
	{
		if (setter == null)
		{
			return null;
		}
		return new RandomizerReuse<>(setter, enforce);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> createNoEnforce(Setter<T2, P2> setter)
	{
		return create(setter, null);
	}
	
	public static <T2, P2> RandomizerReuse<T2, P2> createNoEnforce(SetterNoReturn<T2, P2> setter)
	{
		return createNoEnforce((Setter<T2, P2>)setter);
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
