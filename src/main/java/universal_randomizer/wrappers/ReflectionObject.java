package universal_randomizer.wrappers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import universal_randomizer.Utils;

public class ReflectionObject <T> 
{
	private T obj;
	private int sortingValue;
	private boolean tryUnboxPrimitiveWrappers;

	protected ReflectionObject(T obj)
	{
		this.obj = obj;
		tryUnboxPrimitiveWrappers = true;
		sortingValue = 0;
	}
	
	public static <T2> ReflectionObject<T2> create(T2 obj)
	{
		if (obj == null)
		{
			return null;
		}
		return new ReflectionObject<>(obj);
	}
	
	// TODO: Make more generic	
	public Method getBooleanMethod(String pathToMethod, Class<?>... paramTypes)
	{
		Method method = null;
		Object owningObj = getPenultimateObject(obj, pathToMethod);
		if (owningObj != null)
		{
			try
			{
				method = owningObj.getClass().getMethod(getLastNameOfPath(pathToMethod), paramTypes);
				if (method.getReturnType() != Boolean.class && method.getReturnType() != boolean.class)
				{
					System.err.println("getBooleanMethod found Method " + method.getName() + " at path " + 
							pathToMethod + " with params (" +  Utils.classArrayToString(paramTypes) + 
							") but it does not return a boolean!");
					method = null;
				}
			}
			catch (NoSuchMethodException | SecurityException e) 
			{
				System.err.println("getBooleanMethod couldn't find or access Method " + pathToMethod + 
						" with specified parameters: " + Utils.classArrayToString(paramTypes) );
			}
		}
		return method;
	}
	
	// TODO: how to distinguish between null value and error?
	public Object getField(String pathToGetterOrField)
	{
		return getFromMethodOrField(getPenultimateObject(obj, pathToGetterOrField), getLastNameOfPath(pathToGetterOrField));
	}
	
	public <O> O getField(String pathToGetterOrField, Class<O> oClass) //What about lists with their own types?
	{
		return getFromMethodOrField(getPenultimateObject(obj, pathToGetterOrField), getLastNameOfPath(pathToGetterOrField), oClass);
	}

	// TODO not typesafe
	public Stream<Object> getFieldStream(String pathToGetterOrField)
	{
		Object ret = getFromMethodOrField(getPenultimateObject(obj, pathToGetterOrField), getLastNameOfPath(pathToGetterOrField));
		if (ret != null)
		{
			return Utils.convertToStream(ret);
		}
		return Stream.empty();
	}
	
	public <O> Stream<O> getFieldStream(String pathToGetterOrField, Class<O> oClass)
	{
		Object ret = getFromMethodOrField(getPenultimateObject(obj, pathToGetterOrField), getLastNameOfPath(pathToGetterOrField));
		if (ret != null)
		{
			return Utils.convertToStream(ret, oClass);
		}
		return Stream.empty();
	}

	public Stream<Object> getMapFieldValuesStream(String pathToGetterOrField)
	{
		return getMapFieldStream(pathToGetterOrField, true);
	}

	public <O> Stream<O> getMapFieldValuesStream(String pathToGetterOrField, Class<O> oClass)
	{
		return getMapFieldStream(pathToGetterOrField, oClass, true);
	}

	public Stream<Object> getMapFieldKeysStream(String pathToGetterOrField)
	{
		return getMapFieldStream(pathToGetterOrField, false);
	}

	public <O> Stream<O> getMapFieldKeysStream(String pathToGetterOrField, Class<O> oClass)
	{
		return getMapFieldStream(pathToGetterOrField, oClass, false);
	}

	public Stream<Object> getMapFieldStream(String pathToGetterOrField, boolean valuesNotKeys)
	{
		Object ret = getFromMethodOrField(getPenultimateObject(obj, pathToGetterOrField), getLastNameOfPath(pathToGetterOrField));
		if (ret instanceof Map)
		{
			if (valuesNotKeys)
			{
				return Utils.convertToStream(((Map<?, ?>) ret).values());
			}
			else
			{
				return Utils.convertToStream(((Map<?, ?>) ret).keySet());
			}
		}
		return Stream.empty();
	}
	
	public <O> Stream<O> getMapFieldStream(String pathToGetterOrField, Class<O> oClass, boolean valuesNotKeys)
	{
		Object ret = getFromMethodOrField(getPenultimateObject(obj, pathToGetterOrField), getLastNameOfPath(pathToGetterOrField));
		if (ret instanceof Map)
		{
			// Size and type safety are handled by the util already
			if (valuesNotKeys)
			{
				return Utils.convertToStream(((Map<?, ?>) ret).values(), oClass);
			}
			else
			{
				return Utils.convertToStream(((Map<?, ?>) ret).keySet(), oClass);
			}
		}
		return Stream.empty();
	}

	public <O> boolean setField(String pathToSetterOrField, O value, Class<O> oClass)
	{
		return setFromMethodOrField(getPenultimateObject(obj, pathToSetterOrField), getLastNameOfPath(pathToSetterOrField), value, oClass);
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
			Object nextObj = getFromMethodOrField(baseObj, paths[0]);
			for (int pathIndex = 1; pathIndex < paths.length - 1 && nextObj != null; pathIndex++)
			{
				nextObj = getFromMethodOrField(nextObj, paths[pathIndex]);
			}
			return nextObj;
		}
		return baseObj;
	}
	
	// TODO: Add support for tryUnboxPrimitiveWrappers
	private Object getFromMethodOrField(Object owningObj, String methodOrField)
	{
		if (owningObj != null)
		{
			try
			{
				return owningObj.getClass().getMethod(methodOrField).invoke(owningObj);
			} 
			catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e)
			{
				// Try next thing
			}
			
			try 
			{
				return owningObj.getClass().getField(methodOrField).get(owningObj);
			} 
			catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
			{
				// Fall through to catch all paths with error below
			}
			System.err.println("getFromMethodOrField couldn't find or access Getter/Field " + methodOrField + 
					" for object " + owningObj.getClass().getName());
		}
		else
		{
			System.err.println("getFromMethodOrField passed null object for Getter/Field " + methodOrField);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <O> O getFromMethodOrField(Object owningObj, String methodOrField, Class<O> oClass)
	{
		if (owningObj != null && oClass != null)
		{
			if (oClass.getTypeParameters().length > 0)
			{
				System.err.println("getFromMethodOrField passed object class " + oClass.getName() +
					"has templated parameters. Type safety can only be enforced at the top level" +
					"so this conversion will not be fully type safe");
			}
			try
			{
				Method method = owningObj.getClass().getMethod(methodOrField);
				if (oClass.isAssignableFrom(method.getReturnType()))
				{
					// Use this type of casting so that it will work for primitives
					// using oClass.cast will give a conversion error if you try to
					// to convert Integer to int
					return (O) method.invoke(owningObj);
				}
			} 
			catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e)
			{
				// Try next thing
			}
			
			try 
			{
				Field field = owningObj.getClass().getField(methodOrField);
				if (oClass.isAssignableFrom(field.getType()))
				{
					// Same comment as above
					return (O) field.get(owningObj);
				}
			} 
			catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
			{
				// Fall through to catch all paths with error below
			}
			System.err.println("getFromMethodOrField couldn't find or access Getter/Field " + methodOrField + 
					" for object " + owningObj.getClass().getName() + " or it did not return " + oClass.getName());
		}
		else
		{
			System.err.println("getFromMethodOrField passed null class or object for Getter/Field " + methodOrField);
		}
		return null;
	}

	// TODO have a non type safe version? Would likely need to use getMethods and loop through manually checking
	/*
			
			Class<?> asPrimitive = Utils.tryConvertBoxedToPrimitive(oClass.getClass(), false);
			if (asPrimitive != null)
			{
				System.err.println("getFromMethodOrField couldn't find or access Getter/Field " + methodOrField + 
						" for object " + owningObj.getClass().getName() + " with param " + value.getClass().getName() +
						" but the class is an boxed primitive. Trying the primitive class now");
				return setFromMethodOrField(owningObj, methodOrField, value, asPrimitive);
			}*/
	// TODO will this handle null inputs? not right now. Can maybe use null.class to help
	// TODO: How to handle primitives?	
	private boolean setFromMethodOrField(Object owningObj, String methodOrField, Object value, Class<?> oClass)
	{		
		// Make sure we have an owning object, a class and if its 
		if (owningObj != null && oClass != null && (!oClass.isPrimitive() || value != null))
		{
			// Non Primitive setter
			try
			{
				Method method = owningObj.getClass().getMethod(methodOrField, oClass);
				Object ret = method.invoke(owningObj, value);
				if (method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class)
				{
					return ret != null && (boolean) ret;
				}
				return true;
			} 
			catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) 
			{
				// Try next thing
			}
			
			Class<?> asPrimitive = null;
			if (tryUnboxPrimitiveWrappers && value != null)
			{
				// Try to unbox. If its already a primitive, it will return null
				asPrimitive = Utils.tryUnboxToPrimitive(oClass, false);
				if (asPrimitive != null)
				{
					try
					{
						Method method = owningObj.getClass().getMethod(methodOrField, asPrimitive);
						Object ret = method.invoke(owningObj, value);
						if (method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class)
						{
							return ret != null && (boolean) ret;
						}
						return true;
					} 
					catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) 
					{
						// Try next thing
					}
				}
			}
			
			// field
			Field foundField = null;
			try
			{
				foundField = owningObj.getClass().getField(getLastNameOfPath(methodOrField));
				if (foundField.getType().isAssignableFrom(oClass) ||
						// Check if its a primitive version of the field
						// we already did safety checking so all we need to
						// check now is if asPrimitive is set
						(asPrimitive != null && foundField.getType().isAssignableFrom(asPrimitive)))
				{
					foundField.set(owningObj, value);
					return true;
				}
			} 
			catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) 
			{
				// Handle outside so fall through will catch it too
			}
			System.err.println("setFromMethodOrField couldn't find or access Setter/Field " + methodOrField + 
					" for object " + owningObj.getClass().getName() + " with param " + oClass.getName());
		}
		else
		{
			System.err.println("setFromMethodOrField passed null owning object for Setter/Field " + methodOrField);
		}
		return false;
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
	
	public boolean getTryUnboxWrappersOfPrimitives()
	{
		return tryUnboxPrimitiveWrappers;
	}
	
	public void setTryUnboxWrappersOfPrimitives(boolean willUnbox)
	{
		tryUnboxPrimitiveWrappers = willUnbox;
	}
	
	public int getSortingValue()
	{
		return sortingValue;
	}
	
	public void setSortingValue(int val)
	{
		sortingValue = val;
	}

	public ReflectionObject<T> setSortingValueReturnSelf(int val)
	{
		setSortingValue(val);
		return this;
	}
	
	public static <T> int sortBySortingValue(ReflectionObject<T> lhs, ReflectionObject<T> rhs)
    {
		// TODO: need a way to avoid duplicate values? Maybe secondary that is set when there is a conflict?
        return Integer.compare(lhs.getSortingValue(), rhs.getSortingValue());
    }
}
