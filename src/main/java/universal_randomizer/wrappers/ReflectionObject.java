package universal_randomizer.wrappers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import universal_randomizer.Utils;


public class ReflectionObject <T> 
{
	private T obj;

	protected ReflectionObject(T obj)
	{
		this.obj = obj;
	}
	
	public static <T2> ReflectionObject<T2> create(T2 obj)
	{
		if (obj == null)
		{
			return null;
		}
		return new ReflectionObject<>(obj);
	}
	
	public Object getField(String pathToGetterOrField) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		return getObjectFromPath(obj, pathToGetterOrField, false);
	}

	public Stream<Object> getFieldStream(String pathToGetterOrField)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		return Utils.convertToStream(getField(pathToGetterOrField));
	}

	public Stream<Object> getMapFieldValuesStream(String pathToGetterOrField)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		return getMapFieldStream(pathToGetterOrField, true);
	}

	public Stream<Object> getMapFieldKeysStream(String pathToGetterOrField)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		return getMapFieldStream(pathToGetterOrField, false);
	}

	@SuppressWarnings("unchecked")
	public Stream<Object> getMapFieldStream(String pathToGetterOrField, boolean valuesNotKeys)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		Object ret = getField(pathToGetterOrField);
		if (valuesNotKeys)
		{
			return Utils.convertToStream(((Map<?, Object>) ret).values());
		}
		else
		{
			return Utils.convertToStream(((Map<Object, ?>) ret).keySet());
		}
	}

	public Object setField(String pathToSetterOrField, Object value) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
			NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		Object owningObj = getObjectFromPath(obj, pathToSetterOrField, true);
		int lastSeparator = pathToSetterOrField.lastIndexOf('.');
		String lastPath = pathToSetterOrField;
		if (lastSeparator >= 0)
		{
			lastPath = lastPath.substring(lastSeparator + 1);
		}

		if (lastPath.endsWith("()"))
		{
			Method method = getMethodByName(owningObj, lastPath.substring(0, lastPath.length() - 2));
			return method.invoke(owningObj, value);
		}
		else
		{
			owningObj.getClass().getField(lastPath).set(owningObj, value);
			return null;
		}
	}
	
	private Method getMethodByName(Object obj, String methodName) throws NoSuchMethodException
	{
		Method[] methods = obj.getClass().getMethods();
		for (Method m : methods)
		{
			if (m.getName().equals(methodName))
			{
				return m;
			}
		}
		throw new NoSuchMethodException();
	}
	
	private Object getObjectFromPath(Object baseObj, String path, boolean penultimate) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
				NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		String[] paths = path.split("\\.");
		Object nextObj = getFromMethodOrField(baseObj, paths[0]);
		int lengths = !penultimate ? paths.length : paths.length - 1;
		for (int pathIndex = 1; pathIndex < lengths; pathIndex++)
		{
			nextObj = getFromMethodOrField(nextObj, paths[pathIndex]);
		}
		return nextObj;
	}
	
	private Object getFromMethodOrField(Object owningObj, String methodOrField) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
				NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		if (methodOrField.endsWith("()"))
		{
			return getMethodByName(owningObj, methodOrField.substring(0, methodOrField.length() - 2)).invoke(owningObj);
		}
		else
		{
			return owningObj.getClass().getField(methodOrField).get(owningObj);
		}
	}
	
	public T getObject()
	{
		return obj;
	}
	
	protected boolean setObject(T obj)
	{
		if (obj != null)
		{
			this.obj = obj;
			return true;
		}
		return false;
	}
}