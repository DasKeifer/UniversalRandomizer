package universal_randomizer.randomize;


import universal_randomizer.condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;

public class EnforceParams<T>
{
	private Condition<T> enforce;
	private int maxRetries;
	private int maxResets;

	public EnforceParams(Condition<T> enforce, int maxRetries, int maxResets)
	{
		if (enforce != null)
		{
			this.enforce = enforce.copy();
			this.maxRetries = maxRetries;
			this.maxResets = maxResets;
		}
		else
		{
			this.enforce = null;
			this.maxRetries = 0;
			this.maxResets = 0;
		}
	}
	
	public EnforceParams(EnforceParams<T> toCopy)
	{
		if (toCopy.enforce != null)
		{
			this.enforce = toCopy.enforce.copy();
		}
		else
		{
			this.enforce = null;
		}
		this.maxRetries = toCopy.maxRetries;
		this.maxResets = toCopy.maxResets;
	}
	
	public static <U> EnforceParams<U> createNoEnforce() 
	{
		return new EnforceParams<U>(null, 0, 0);
	}

	public EnforceParams<T> copy()
	{
		return new EnforceParams<>(this);
	}

	public boolean evaluateEnforce(ReflectionObject<T> obj) 
	{
		if (enforce == null)
		{
			// We return true because if there is no enforce,
			// then it must "pass"
			return true;
		}
		return enforce.evaluate(obj);
	}
	
	public int getMaxRetries()
	{
		return maxRetries;
	}
	
	public int getMaxResets()
	{
		return maxResets;
	}
}
