package universal_randomizer.condition;



import java.util.Comparator;

import universal_randomizer.wrappers.ComparableAsComparator;
import universal_randomizer.wrappers.ReflectionObject;


public class SimpleCondition <T, M> implements Condition<T>
{
	private String variable;
	private Negate negate;
	private Comparison comparison;
	private M compareToVal;
	private Comparator<M> comparator;

	public static <T2, M2 extends Comparable<M2>> SimpleCondition<T2, M2> create(
			String variable, Comparison comparison, M2 compareToVal)
	{
		return create(variable, Negate.NO, comparison, compareToVal);
	}
	
	public static <T2, M2 extends Comparable<M2>> SimpleCondition<T2, M2> create(
			String variable, Negate negate, Comparison comparison, M2 compareToVal)
	{
		return new SimpleCondition<>(variable, negate, comparison, compareToVal, new ComparableAsComparator<>());
	}

	public static <T2, M2> SimpleCondition<T2, M2> create(
			String variable, Comparison comparison, M2 compareToVal, Comparator<M2> comparator)
	{
		return create(variable, Negate.NO, comparison, compareToVal, comparator);
	}

	public static <T2, M2> SimpleCondition<T2, M2> create(
			String variable, Negate negate, Comparison comparison, M2 compareToVal, Comparator<M2> comparator)
	{
		return new SimpleCondition<>(variable, negate, comparison, compareToVal, comparator);
	}
	
	protected SimpleCondition(String variable, Negate negate, Comparison comparison, M compareToVal, Comparator<M> comparator) 
	{
		this.variable = variable;
		this.negate = negate;
		this.comparison = comparison;
		this.compareToVal = compareToVal;
		this.comparator = comparator;
	}
	
	@Override
	public boolean evaluate(ReflectionObject<T> obj)
	{
		boolean result = false;
		
		// Get the var
		Object objVal = obj.getField(variable);
		if (objVal != null && compareToVal.getClass().isInstance(objVal)) // todo error or alert somehow
		{
			// Checked on the if above
			@SuppressWarnings("unchecked")
			M valCasted = (M) objVal;
			result = invokeCompareTo(valCasted, comparison, compareToVal);
			if (negate == Negate.YES)
			{
				result = !result;
			}
		}
		return result;
	}
	
	private boolean invokeCompareTo(M objVal, Comparison comparison, M compareToVal)
	{
		int compareResult = comparator.compare(objVal, compareToVal);
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
	
	public String getVariable() {
		return variable;
	}

	public Negate getNegate() {
		return negate;
	}

	public Comparison getComparison() {
		return comparison;
	}

	public M getVal() {
		return compareToVal;
	}

	public Comparator<M> getComparator() {
		return comparator;
	}

	protected void setVariable(String variable) {
		this.variable = variable;
	}

	protected void setNegate(Negate negate) {
		this.negate = negate;
	}

	protected void setComparison(Comparison comparison) {
		this.comparison = comparison;
	}

	protected void setVal(M val) {
		this.compareToVal = val;
	}

	protected void setComparator(Comparator<M> comparator) {
		this.comparator = comparator;
	}
}
