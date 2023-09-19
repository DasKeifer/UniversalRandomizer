package universal_randomizer.randomize;


import universal_randomizer.pool.RandomizerPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.wrappers.SetterAsMultiSetter;

public class BasicRandomizer<T, P>  extends Randomizer<T, RandomizerPool<P>>
{	
	private MultiSetter<T, P> setter;
	private Getter<T, Integer> countGetter;
	
	protected BasicRandomizer(MultiSetter<T, P> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce)
	{
		super(enforce);
		
		this.setter = setter;
		this.countGetter = countGetter;
	}

	public static <T2, P2> BasicRandomizer<T2, P2> create(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new BasicRandomizer<>(setter, countGetter, enforce);
	}
	
	public static <T2, P2> BasicRandomizer<T2, P2> create(MultiSetter<T2, P2> setter, int count, EnforceParams<T2> enforce)
	{
		return create(setter, o -> count, enforce);
	}
	
	public static <T2, P2> BasicRandomizer<T2, P2> create(Setter<T2, P2> setter, EnforceParams<T2> enforce)
	{
		return create(new SetterAsMultiSetter<>(setter), 1, enforce);
	}

	public static <T2, P2> BasicRandomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter)
	{
		return create(setter, countGetter, null);
	}
	
	public static <T2, P2> BasicRandomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter, int count)
	{
		return create(setter, o -> count, null);
	}
	
	public static <T2, P2> BasicRandomizer<T2, P2> createNoEnforce(Setter<T2, P2> setter)
	{
		return create(setter, null);
	}

	@Override
	protected void resetPool() 
	{
		getPool().reset();
	}

	@Override
	protected boolean assignValue(T obj)
	{
		boolean success = true;
		do 
		{
			success = true;
			int setCount = countGetter.get(obj);
			for (int count = 0; count < setCount; count++)
			{
				success = success && attemptAssignValue(obj, count);
			}
			if (success)
			{
				break;
			}
		} while (getPool().useNextPool());		
		
		return success;
	}

	protected boolean attemptAssignValue(T obj, int count)
	{
		P selectedVal = getPool().peek(getRandom());
		
		// While its a good index and fails the enforce check, retry if
		// we have attempts left
		boolean success = assignAndCheckEnforce(obj, selectedVal, count);
		for (int retry = 0; retry < getEnforceActions().getMaxRetries(); retry++)
		{
			if (success || selectedVal == null)
			{
				break;
			}
			selectedVal = getPool().peek(getRandom());
			success = assignAndCheckEnforce(obj, selectedVal, count);
		}
		
		if (success)
		{
			getPool().selectPeeked();
		}
		
		return success;
	}
	
	protected boolean assignAndCheckEnforce(T obj, P value, int count)
	{
		return setter.setReturn(obj, value, count) && getEnforceActions().evaluateEnforce(obj);
	}

	protected MultiSetter<T, P> getSetter() 
	{
		return setter;
	}
}