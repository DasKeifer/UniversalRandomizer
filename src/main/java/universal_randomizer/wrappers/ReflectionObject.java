package universal_randomizer.wrappers;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import universal_randomizer.Utils;

public class ReflectionObject <T> {
	T obj;
	int randValue;

	// TODO: Refactor to factory instead of constructor and to prevent null values?
	
	public ReflectionObject(T obj)
	{
		this.obj = obj;
		randValue = 0;
	}
	
	public Method getBooleanMethod(String pathToMethod, Class<?>... paramTypes)
	{
		Object owningObj = getPenultimateObject(obj, pathToMethod);
		try
		{
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
	
	public Object getField(String pathToField)
	{
		Object owningObj = getPenultimateObject(obj, pathToField);
		try 
		{
			return owningObj.getClass().getField(getLastNameOfPath(pathToField)).get(owningObj);
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public <M> Stream<M> getFieldStream(String pathToField)
	{
		Object owningObj = getPenultimateObject(obj, pathToField);
		try 
		{
			return Utils.convertToStream(
					owningObj.getClass().getField(getLastNameOfPath(pathToField)).get(owningObj));
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Stream.empty();
	}
	
	public <M> Stream<M> getMapFieldValuesStream(String pathToMapField)
	{
		return getMapFieldStream(pathToMapField, true);
	}
	
	public <M> Stream<M> getMapFieldKeysStream(String pathToMapField)
	{
		return getMapFieldStream(pathToMapField, false);
	}
	
	public <M> Stream<M> getMapFieldStream(String pathToField, boolean valuesNotKeys)
	{
		Object owningObj = getPenultimateObject(obj, pathToField);
		try 
		{
			Object fieldVal = owningObj.getClass().getField(getLastNameOfPath(pathToField)).get(owningObj);
			if (fieldVal instanceof Map)
			{
				if (valuesNotKeys)
				{
					return Utils.convertToStream(((Map<?, ?>) fieldVal).values());
				}
				else
				{
					return Utils.convertToStream(((Map<?, ?>) fieldVal).keySet());
				}
			}
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Stream.empty();
	}

	// TODO: Need to handle setters and also "raw" vars
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

	public ReflectionObject<T> setRandomValueReturnSelf(int val)
	{
		setRandomValue(val);
		return this;
	}
	
	public static <T> int sortByRandomValue(ReflectionObject<T> lhs, ReflectionObject<T> rhs)
    {
		// TODO: need a way to avoid duplicate values?
        return Integer.compare(lhs.getRandomValue(), rhs.getRandomValue());
    }
}
