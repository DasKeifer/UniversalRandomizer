package universal_randomizer.randomize;


import universal_randomizer.condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;

public class EnforceParams<T>
{
	private Condition<T> enforce;
	private int maxRetries;
	private int maxResets;

	// TODO refactor to factory
	
	public EnforceParams(Condition<T> enforce, int maxRetries, int maxResets)
	{
		if (enforce != null)
		{
			this.enforce = enforce;
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
	
	public static <T2> EnforceParams<T2> createNoEnforce() 
	{
		return new EnforceParams<>(null, 0, 0);
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
