package condition;

import java.util.LinkedList;
import java.util.List;

import universal_randomizer.ReflectionObject;

public class CompoundCondition extends Condition
{
	Condition baseCond;
	List<LogicConditionPair> additionalConds;
	
	public CompoundCondition(Condition baseCond, List<LogicConditionPair> additionalConds)
	{
		this.baseCond = baseCond.copy();
		this.additionalConds = new LinkedList<>();
		for (LogicConditionPair pair : additionalConds)
		{
			this.additionalConds.add(new LogicConditionPair(pair));
		}
	}
	
	public CompoundCondition(Condition baseCond, LogicConditionPair... additionalConds)
	{
		this.baseCond = baseCond.copy();
		this.additionalConds = new LinkedList<>();
		for (LogicConditionPair pair : additionalConds)
		{
			this.additionalConds.add(new LogicConditionPair(pair));
		}
	}
	
	public CompoundCondition(CompoundCondition toCopy)
	{
		this(toCopy.baseCond, toCopy.additionalConds);
	}

	@Override
	public CompoundCondition copy() 
	{
		return new CompoundCondition(this);
	}
	
	@Override
	public boolean evaluate(ReflectionObject obj) 
	{
		boolean result = baseCond.evaluate(obj);
		
		for (LogicConditionPair condOp : additionalConds)
		{
			switch (condOp.op)
			{
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
