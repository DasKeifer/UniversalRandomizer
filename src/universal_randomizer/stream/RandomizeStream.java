package universal_randomizer.stream;

import java.util.List;

import condition.Condition;

public interface RandomizeStream<T>
{	
	public RandomizeStream<T> select(Condition<T> varExpr);
	
	public RandomizeMultiStream<T> group(String groupingVar);
	
	public List<T> collect();
}
