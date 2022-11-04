package condition;


public class LogicConditionPair<T>
{
	Logic op;
	Negate negate;
	Condition<T> cond;

	// TODO: Refactor to factory instead of constructor
	
	public LogicConditionPair(Logic op, Negate negate, Condition<T> cond)
	{
		this.op = op;
		this.negate = negate;
		this.cond = cond;
	}
	
	public LogicConditionPair(Logic op, Condition<T> cond)
	{
		this(op, Negate.NO, cond);
	}
	
	public LogicConditionPair(LogicConditionPair<T> toCopy)
	{
		this.op = toCopy.op;
		this.negate = toCopy.negate;
		this.cond = toCopy.cond;
	}
	
	Logic getLogicOperator()
	{
		return op;
	}
	
	Negate getNegateOperator()
	{
		return negate;
	}
	
	Condition<T> getCondition()
	{
		return cond;
	}
}
