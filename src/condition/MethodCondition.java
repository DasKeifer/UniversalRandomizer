package condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import universal_randomizer.wrappers.ReflectionObject;

public class MethodCondition extends Condition 
{
	String method;

	public MethodCondition(String methodName) 
	{
		this.method = methodName;
	}
	
	public MethodCondition(MethodCondition toCopy) 
	{
		this.method = toCopy.method;
	}
	
	@Override
	public Condition copy() 
	{
		return new MethodCondition(this);
	}

	@Override
	public <T> boolean evaluate(ReflectionObject<T> obj) 
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
