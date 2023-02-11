package universal_randomizer.condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import universal_randomizer.wrappers.ReflectionObject;

public class MethodCondition <T> implements Condition<T>
{
	String method;

	public MethodCondition(String methodName) 
	{
		this.method = methodName;
	}
	
	public MethodCondition(MethodCondition<T> toCopy) 
	{
		this.method = toCopy.method;
	}
	
	@Override
	public Condition<T> copy() 
	{
		return new MethodCondition<>(this);
	}

	@Override
	public boolean evaluate(ReflectionObject<T> obj) 
	{
		// Get the var
		Method methObj = obj.getBooleanMethod(method);
		try 
		{
			return (Boolean) methObj.invoke(obj.getObject());
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
