package condition;


import universal_randomizer.ReflectionObject;

public class SimpleCondition extends Condition
{
	public String variable;
	public boolean negate;
	public Comparator comparator;
	public Object val;
	
	@Override
	public boolean evaluate(ReflectionObject obj)
	{
		// Get the var
		Object var = obj.getVariableValue(variable);
		return compareTo(var, negate, comparator, val);
	}
}
