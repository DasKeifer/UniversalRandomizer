package universal_randomizer.condition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import universal_randomizer.wrappers.ReflectionObject;

public class CompoundCondition <T> implements Condition<T>
{
	private Condition<T> baseCond;
	private List<LogicConditionPair<T>> additionalConds;

	public static <T2> CompoundCondition<T2> create(
			Condition<T2> baseCond, List<LogicConditionPair<T2>> additionalConds)
	{
		return new CompoundCondition<>(baseCond, additionalConds);
	}
	
	@SafeVarargs
	public static <T2> CompoundCondition<T2> create(
			Condition<T2> baseCond, LogicConditionPair<T2>... additionalConds)
	{
		return new CompoundCondition<>(baseCond, Arrays.asList(additionalConds));
	}

	protected CompoundCondition(Condition<T> baseCond, List<LogicConditionPair<T>> additionalConds)
	{
		this.baseCond = baseCond;
		this.additionalConds = new LinkedList<>(additionalConds);
	}
	
	@Override
	public boolean evaluate(ReflectionObject<T> obj) 
	{
		boolean result = baseCond.evaluate(obj);
		
		for (LogicConditionPair<T> condOp : additionalConds)
		{
			boolean conditionResult = condOp.cond.evaluate(obj);
			switch (condOp.negate)
			{
				case YES: conditionResult = !conditionResult; break;
				case NO: break;
				default:
					//Error
					return false;
			
			}
			
			switch (condOp.op)
			{
				case AND, NAND: result = result && conditionResult; break;
				case OR, NOR: result = result || conditionResult; break;
				case XOR, XNOR: result = result ^ conditionResult; break;
				default:
					//Error
					return false;
			}
			
			if (condOp.op.isNegationLogic())
			{
				result = !result;
			}
		}
		return result;
	}

	public Condition<T> getBaseCond() {
		return baseCond;
	}

	public List<LogicConditionPair<T>> getAdditionalConds() {
		
		return additionalConds;
	}

	protected void setBaseCond(Condition<T> baseCond) {
		this.baseCond = baseCond;
	}

	protected void setAdditionalConds(List<LogicConditionPair<T>> additionalConds) {
		this.additionalConds = additionalConds;
	}
}
