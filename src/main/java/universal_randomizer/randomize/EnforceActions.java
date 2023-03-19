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
		if (enforce != null)
		{
			this.enforce = enforce.copy();
			this.maxRetries = maxRetries;
			this.maxResets = maxResets;
			this.failAction = failAction;
		}
		else
		{
			this.enforce = null;
			this.maxRetries = 0;
			this.maxResets = 0;
			this.failAction = OnFail.IGNORE;
		}
	}
	
	public EnforceActions(EnforceActions<T> toCopy)
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
		this.failAction = toCopy.failAction;
	}
	
	public static <U> EnforceActions<U> createNone() 
	{
		return new EnforceActions<U>(null, 0, 0, OnFail.IGNORE);
	}

	public EnforceActions<T> copy()
	{
		return new EnforceActions<>(this);
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
