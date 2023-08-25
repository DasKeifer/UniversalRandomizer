package universal_randomizer.condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import universal_randomizer.wrappers.ReflectionObject;

public class MethodCondition <T> implements Condition<T>
{
	private String method;
	private Negate negate;

	public static <T2> MethodCondition<T2> create(
			String methodName)
	{
		return create(methodName, Negate.NO);
	}
	
	public static <T2> MethodCondition<T2> create(
			String methodName, Negate negate)
	{
		return new MethodCondition<>(methodName, negate);
	}
	
	protected MethodCondition(String methodName, Negate negate) 
	{
		this.method = methodName;
		this.negate = negate;
	}
	
	@Override
	public boolean evaluate(ReflectionObject<T> obj) 
	{
		// Get the var
		Method methodObj = obj.getBooleanMethod(method);
		if (methodObj != null)
		{
			try 
			{
				boolean result = (boolean) methodObj.invoke(obj.getObject());
				switch (negate)
				{
					case YES: result = !result; break;
					case NO: break;
					default:
						//error
						return false;
				}
				return result;
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				System.err.println("evaluate failed to invoke method " + methodObj.getName() + 
						". This should never happen as we ensure the funciton is found before calling");
			}
		}
		
		return false;
	}

	public String getMethod() {
		return method;
	}

	public Negate getNegate() {
		return negate;
	}

	protected void setMethod(String method) {
		this.method = method;
	}

	protected void setNegate(Negate negate) {
		this.negate = negate;
	}
}
