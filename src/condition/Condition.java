package condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import universal_randomizer.ReflectionObject;

public abstract class Condition 
{
	public abstract Condition copy();
	public abstract boolean evaluate(ReflectionObject obj);
	
	protected <T extends Object> boolean compareTo(T wrapped, Negate negate, Compare comparator, Object val)
	{
		boolean result = false;
		if (wrapped != null)
		{
			Class<?> clazz = wrapped.getClass();
			if (wrapped.getClass().isInstance(val))
			{
				result = invokeCompareTo(clazz, wrapped, comparator, val);
				if (negate == Negate.YES)
				{
					result = !result;
				}
			}
		}
		return result;
	}
	
	private <T extends Object> boolean invokeCompareTo(Class<?> clazz, T wrapped, Compare comparator, Object val)
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
					case LESS_THAN_OR_EQUAL: return 0 >= compareResult;
					case GREATER_THAN_OR_EQUAL: return 0 <= compareResult;
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
}
