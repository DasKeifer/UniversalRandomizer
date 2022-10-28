package condition;


public class LogicConditionPair
{
	Logic op;
	Negate negate;
	Condition cond;
	
	public LogicConditionPair(Logic op, Negate negate, Condition cond)
	{
		this.op = op;
		this.negate = negate;
		this.cond = cond;
	}
	
	public LogicConditionPair(Logic op, Condition cond)
	{
		this(op, Negate.NO, cond);
	}
	
	public LogicConditionPair(LogicConditionPair toCopy)
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
	
	Condition getCondition()
	{
		return cond;
	}
}
