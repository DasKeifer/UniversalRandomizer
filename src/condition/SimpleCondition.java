package condition;


import universal_randomizer.ReflectionObject;

public class SimpleCondition extends Condition
{
	String variable;
	Negate negate;
	Compare comparator;
	Object val;
	
	public SimpleCondition(String variable, Negate negate, Compare comparator, Object val) 
	{
		this.variable = variable;
		this.negate = negate;
		this.comparator = comparator;
		this.val = val; // TODO: How to copy? Enforce copy constructor?
	}

	public SimpleCondition(String variable, Compare comparator, Object val) 
	{
		this(variable, Negate.NO, comparator, val);
	}
	
	public SimpleCondition(SimpleCondition toCopy) 
	{
		this.variable = toCopy.variable;
		this.negate = toCopy.negate;
		this.comparator = toCopy.comparator;
		this.val = toCopy.val; // TODO: How to copy? Enforce copy constructor?
	}
	
	@Override
	public SimpleCondition copy() 
	{
		return new SimpleCondition(this);
	}
	
	@Override
	public boolean evaluate(ReflectionObject obj)
	{
		// Get the var
		Object var = obj.getVariableValue(variable);
		return compareTo(var, negate, comparator, val);
	}
}
