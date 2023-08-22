package universal_randomizer.condition;



import java.util.Comparator;

import universal_randomizer.wrappers.ComparableAsComparator;
import universal_randomizer.wrappers.ReflectionObject;


public class SimpleCondition <T, M, MComparator extends Comparator<M>> implements Condition<T>
{
	String variable;
	Negate negate;
	Comparison comparison;
	M val;
	MComparator comparator;

	public static <TF, MF extends Comparable<MF>> 
	SimpleCondition<TF, MF, ComparableAsComparator<MF>> create(
			String variable, Comparison comparison, MF val)
	{
		return create(variable, Negate.NO, comparison, val);
	}
	
	public static <TF, MF extends Comparable<MF>> 
	SimpleCondition<TF, MF, ComparableAsComparator<MF>> create(
			String variable, Negate negate, Comparison comparison, MF val)
	{
		return new SimpleCondition<TF, MF, ComparableAsComparator<MF>>(variable, negate, comparison, val, new ComparableAsComparator<>());
	}
	
	public static <TF, MF, MComparatorF extends Comparator<MF>> 
	SimpleCondition<TF, MF, MComparatorF> create(
			String variable, Comparison comparison, MF val, MComparatorF comparator)
	{
		return create(variable, Negate.NO, comparison, val, comparator);
	}

	public static <TF, MF, MComparatorF extends Comparator<MF>> 
	SimpleCondition<TF, MF, MComparatorF> create(
			String variable, Negate negate, Comparison comparison, MF val, MComparatorF comparator)
	{
		return new SimpleCondition<TF, MF, MComparatorF>(variable, negate, comparison, val, comparator);
	}
	
	protected SimpleCondition(String variable, Negate negate, Comparison comparison, M val, MComparator comparator) 
	{
		this.variable = variable;
		this.negate = negate;
		this.comparison = comparison;
		this.val = val;
		this.comparator = comparator;
	}
	
	@Override
	public boolean evaluate(ReflectionObject<T> obj)
	{
		boolean result = false;
		
		// Get the var
		Object var = obj.getField(variable);
		if (var != null && val.getClass().isInstance(var)) // todo error or alert somehow
		{
			// Checked on the if above
			@SuppressWarnings("unchecked")
			M casted = (M) var;
			result = invokeCompareTo(casted, comparison, val);
			if (negate == Negate.YES)
			{
				result = !result;
			}
		}
		return result;
	}
	
	private boolean invokeCompareTo(M var, Comparison comparison, M val)
	{
		int compareResult = comparator.compare(var, val);
		switch (comparison)
		{
			case EQUAL: return 0 == compareResult;
			case LESS_THAN: return 0 > compareResult;
			case GREATER_THAN: return 0 < compareResult;
			case LESS_THAN_OR_EQUAL: return 0 >= compareResult;
			case GREATER_THAN_OR_EQUAL: return 0 <= compareResult;
			default: 
				System.out.println("compareWrappedPrimative - unknown Comparator value: " + comparator);
				return false;
		}
	}
}
