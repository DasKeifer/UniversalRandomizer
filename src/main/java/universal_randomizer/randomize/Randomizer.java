package universal_randomizer.randomize;


import universal_randomizer.pool.RandomizerPool;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.MultiSetter;
import universal_randomizer.user_object_apis.Setter;
import universal_randomizer.wrappers.SetterAsMultiSetter;

public class Randomizer<T, P>  extends RandomizerBase<T, RandomizerPool<P>>
{	
	private MultiSetter<T, P> setter;
	private Getter<T, Integer> countGetter;
	
	protected Randomizer(MultiSetter<T, P> setter, Getter<T, Integer> countGetter, EnforceParams<T> enforce)
	{
		super(enforce);
		
		this.setter = setter;
		this.countGetter = countGetter;
	}

	public <T2, P2> Randomizer<T2, P2> create(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2> enforce)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new Randomizer<>(setter, countGetter, enforce);
	}
	
	public <T2, P2> Randomizer<T2, P2> create(MultiSetter<T2, P2> setter, int count, EnforceParams<T2> enforce)
	{
		return create(setter, o -> count, enforce);
	}
	
	public <T2, P2> Randomizer<T2, P2> create(Setter<T2, P2> setter, EnforceParams<T2> enforce)
	{
		return create(new SetterAsMultiSetter<>(setter), 1, enforce);
	}

	public <T2, P2> Randomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter)
	{
		return create(setter, countGetter, null);
	}
	
	public <T2, P2> Randomizer<T2, P2> createNoEnforce(MultiSetter<T2, P2> setter, int count)
	{
		return create(setter, o -> count, null);
	}
	
	public <T2, P2> Randomizer<T2, P2> createNoEnforce(Setter<T2, P2> setter)
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