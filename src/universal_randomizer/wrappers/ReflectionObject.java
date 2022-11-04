package universal_randomizer.wrappers;

import java.lang.reflect.Method;

import universal_randomizer.Utils;

public class ReflectionObject <T> {
	T obj;
	int randValue;

	// TODO: Refactor to factory instead of constructor
	
	public ReflectionObject(T obj)
	{
		this.obj = obj;
	}
	
	public Method getBooleanMethod(String pathToMethod, Class<?>... paramTypes)
	{
		Object owningObj = getPenultimateObject(obj, pathToMethod);
		try {
			Method method = owningObj.getClass().getMethod(getLastNameOfPath(pathToMethod), paramTypes);
			if (method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class)
			{
				return method;
			}
			else
			{
				System.err.println("getBooleanMethod found Method " + method.getName() + " at path " + pathToMethod + " but it does not return a boolean ");
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public <M> M getVariableValue(String pathToField)
	{
		Object owningObj = getPenultimateObject(obj, pathToField);
		try 
		{
			return Utils.safeCast(owningObj.getClass().getField(getLastNameOfPath(pathToField)).get(owningObj));
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean setVariableValue(String pathToField, Object value)
	{
		Object owningObj = getPenultimateObject(obj, pathToField);
		try 
		{
			owningObj.getClass().getField(getLastNameOfPath(pathToField)).set(owningObj, value);
			return true;
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private String getLastNameOfPath(String path)
	{
		int lastSeparator = path.lastIndexOf('.');
		if (lastSeparator >= 0)
		{
			return path.substring(lastSeparator + 1);
		}
		return path;
	}
	
	private Object getPenultimateObject(Object baseObj, String path)
	{
		if (path.contains("."))
		{
			String[] paths = path.split("\\.");
			try 
			{
				Object nextObj = baseObj.getClass().getField(paths[0]).get(baseObj);
				for (int pathIndex = 1; pathIndex < paths.length - 1; pathIndex++)
				{
					nextObj = nextObj.getClass().getField(paths[pathIndex]).get(nextObj);
				}
				return nextObj;
			} 
			catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return baseObj;
	}
	
	public T getObject()
	{
		return obj;
	}
	
	public int getRandomValue()
	{
		return randValue;
	}
	
	public void setRandomValue(int val)
	{
		randValue = val;
	}
}
