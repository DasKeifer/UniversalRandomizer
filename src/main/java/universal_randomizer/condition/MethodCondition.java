package universal_randomizer.condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import universal_randomizer.wrappers.ReflectionObject;

public class MethodCondition <T> implements Condition<T>
{
	String method;
	Negate negate;

	public static <TF> MethodCondition<TF> create(
			String methodName)
	{
		return create(methodName, Negate.NO);
	}
	
	public static <TF> MethodCondition<TF> create(
			String methodName, Negate negate)
	{
		return create(methodName, negate);
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
		Method methObj = obj.getBooleanMethod(method);
		try 
		{
			boolean result = (boolean) methObj.invoke(obj.getObject());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
