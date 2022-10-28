package condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import universal_randomizer.ReflectionObject;

public abstract class Condition 
{
	public abstract boolean evaluate(ReflectionObject obj);
	
	protected <T extends Object> boolean compareTo(T wrapped, boolean not, Comparator comparator, Object val)
	{
		boolean result = false;
		if (wrapped != null)
		{
			Class<?> clazz = wrapped.getClass();
			if (wrapped.getClass().isInstance(val))
			{
				result = invokeCompareTo(clazz, wrapped, comparator, val);
				if (not)
				{
					result = !result;
				}
			}
		}
		return result;
	}
	
	private <T extends Object> boolean invokeCompareTo(Class<?> clazz, T wrapped, Comparator comparator, Object val)
	{
		try 
		{
			Method compareTo = clazz.getMethod("compareTo", clazz);
			Integer compareResult = (Integer) compareTo.invoke(wrapped, val);
			if (compareResult != null)
			{
				switch (comparator)
				{
					case EQUAL: return 0 == compareResult;
					case LESS_THAN: return 0 > compareResult;
					case GREATER_THAN: return 0 < compareResult;
					default: System.out.println("compareWrappedPrimative - unknown Comparator value: " + comparator);
				}
			}
		} 				
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
			// TODO Bad invoke
			e.printStackTrace();
		}
		catch (NoSuchMethodException | SecurityException e1) 
		{
			// TODO Doesn't implement compareTo
			e1.printStackTrace();
		}
		
		return false;
	}

	private <T extends Object> T safeCast(Object obj, Class<T> clazz)
	{
		if (clazz.isInstance(obj))
		{
			return clazz.cast(obj);
		}
		
		return null;
	}
}
