package universal_randomizer.condition;


public class LogicConditionPair<T>
{
	Logic op;
	Negate negate;
	Condition<T> cond;

	public static <TF> LogicConditionPair<TF> create(
			Logic op, Condition<TF> cond)
	{
		return create(op, Negate.NO, cond);
	}
	
	public static <TF> LogicConditionPair<TF> create(
			Logic op, Negate negate, Condition<TF> cond)
	{
		return new LogicConditionPair<TF>(op, negate, cond);
	}
	
	protected LogicConditionPair(Logic op, Negate negate, Condition<T> cond)
	{
		this.op = op;
		this.negate = negate;
		this.cond = cond;
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
