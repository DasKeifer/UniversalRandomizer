package universal_randomizer.randomize;


import universal_randomizer.user_object_apis.Condition;

public class EnforceParams<T>
{
	private Condition<T> enforce;
	private int maxRetries;
	private int maxResets;

	private EnforceParams(Condition<T> enforce, int maxRetries, int maxResets)
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
	
	public static <T2> EnforceParams<T2> create(Condition<T2> enforce, int maxRetries, int maxResets) 
	{
		if (maxRetries < 0 || maxResets < 0)
		{
			return null;
		}
		return new EnforceParams<>(enforce, maxRetries, maxResets);
	}
	
	public static <T2> EnforceParams<T2> createNoEnforce() 
	{
		return new EnforceParams<>(null, 0, 0);
	}

	public boolean evaluateEnforce(T obj) 
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
