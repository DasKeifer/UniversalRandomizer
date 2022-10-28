package condition;

import java.util.LinkedList;
import java.util.List;

import universal_randomizer.ReflectionObject;

public class CompoundCondition extends Condition
{
	List<ConditionOperatorPair> conditions;
	
	public CompoundCondition(List<ConditionOperatorPair> conditions)
	{
		this.conditions = new LinkedList<>();
		this.conditions.addAll(conditions);
	}
	
	@Override
	public boolean evaluate(ReflectionObject obj) 
	{
		boolean result = false;
		for(ConditionOperatorPair condOp : conditions)
		{
			switch (condOp.op)
			{
				case START: result = condOp.cond.evaluate(obj); break;
				case AND: result = result && condOp.cond.evaluate(obj); break;
				case OR: result = result || condOp.cond.evaluate(obj); break;
				default:
					//Error
					result = false;
			}
		}
		return result;
	}
}
