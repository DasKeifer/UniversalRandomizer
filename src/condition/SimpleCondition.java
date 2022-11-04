package condition;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import universal_randomizer.wrappers.ReflectionObject;

public class SimpleCondition <T, M> implements Condition<T>
{
	String variable;
	Negate negate;
	Compare comparator;
	M val;

	// TODO: Refactor to factory instead of constructor?
	
	public SimpleCondition(String variable, Negate negate, Compare comparator, M val) 
	{
		this.variable = variable;
		this.negate = negate;
		this.comparator = comparator;
		this.val = val; // TODO: How to copy? Enforce copy constructor?
	}

	public SimpleCondition(String variable, Compare comparator, M val) 
	{
		this(variable, Negate.NO, comparator, val);
	}
	
	public SimpleCondition(SimpleCondition<T, M> toCopy) 
	{
		this.variable = toCopy.variable;
		this.negate = toCopy.negate;
		this.comparator = toCopy.comparator;
		this.val = toCopy.val; // TODO: How to copy? Enforce copy constructor?
	}
	
	@Override
	public SimpleCondition<T, M> copy() 
	{
		return new SimpleCondition<>(this);
	}
	
	@Override
	public boolean evaluate(ReflectionObject<T> obj)
	{
		boolean result = false;
		
		// Get the var
		M var = obj.getVariableValue(variable);
		if (var != null && var.getClass().isInstance(val))
		{
			result = invokeCompareTo(var, comparator, val);
			if (negate == Negate.YES)
			{
				result = !result;
			}
		}
		return result;
	}
	
	private boolean invokeCompareTo(M var, Compare comparator, M val)
	{
		// TODO: Use comapator/comparable interface instead?
		try 
		{
			Method compareTo = var.getClass().getMethod("compareTo", var.getClass());
			Integer compareResult = (Integer) compareTo.invoke(var, val);
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
