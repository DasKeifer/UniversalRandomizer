package universal_randomizer.condition;

import java.util.LinkedList;
import java.util.List;

import universal_randomizer.wrappers.ReflectionObject;

public class CompoundCondition <T> implements Condition<T>
{
	Condition<T> baseCond;
	List<LogicConditionPair<T>> additionalConds;

	// TODO: Refactor to factory instead of constructor?
	public CompoundCondition(Condition<T> baseCond, List<LogicConditionPair<T>> additionalConds)
	{
		this.baseCond = baseCond.copy();
		this.additionalConds = new LinkedList<>();
		for (LogicConditionPair<T> pair : additionalConds)
		{
			this.additionalConds.add(new LogicConditionPair<>(pair));
		}
	}
	
	@SafeVarargs
	public CompoundCondition(Condition<T> baseCond, LogicConditionPair<T>... additionalConds)
	{
		this.baseCond = baseCond.copy();
		this.additionalConds = new LinkedList<>();
		for (LogicConditionPair<T> pair : additionalConds)
		{
			this.additionalConds.add(new LogicConditionPair<>(pair));
		}
	}
	
	public CompoundCondition(CompoundCondition<T> toCopy)
	{
		this(toCopy.baseCond, toCopy.additionalConds);
	}

	@Override
	public CompoundCondition<T> copy() 
	{
		return new CompoundCondition<>(this);
	}
	
	@Override
	public boolean evaluate(ReflectionObject<T> obj) 
	{
		boolean result = baseCond.evaluate(obj);
		
		for (LogicConditionPair<T> condOp : additionalConds)
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
