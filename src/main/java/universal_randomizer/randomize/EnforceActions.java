package universal_randomizer.randomize;


import universal_randomizer.condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;

public class EnforceActions<T>
{
	private Condition<T> enforce;
	private int maxRetries;
	private int maxResets;
	private OnFail failAction;

	public EnforceActions(Condition<T> enforce, int maxRetries, int maxResets, OnFail failAction)
	{
		this.enforce = enforce.copy();
		this.maxRetries = maxRetries;
		this.maxResets = maxResets;
		this.failAction = failAction;
	}
	
	public static <U> EnforceActions<U> createNone() 
	{
		return new EnforceActions<U>(null, 0, 0, OnFail.IGNORE);
	}

	public static <U> EnforceActions<U> copy(EnforceActions<U> toCopy)
	{
		return new EnforceActions<U>(toCopy.enforce, toCopy.maxRetries, toCopy.maxResets, toCopy.failAction);
	}

	public boolean evaluateEnforce(ReflectionObject<T> obj) 
	{
		if (enforce == null)
		{
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
	
	public OnFail getFailAction()
	{
		return failAction;
	}
}
