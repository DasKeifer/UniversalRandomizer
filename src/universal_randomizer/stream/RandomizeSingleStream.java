package universal_randomizer.stream;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;

public class RandomizeSingleStream<T> implements RandomizeStream<T>
{
	Stream<ReflectionObject<T>> stream;

	public RandomizeSingleStream(Collection<ReflectionObject<T>> source)
	{
		this.stream = source.stream();
	}
	
	public RandomizeSingleStream(Stream<ReflectionObject<T>> source)
	{
		this.stream = source;
	}
	
	public RandomizeSingleStream<T> select(Condition<T> varExpr)
	{
		stream = stream.filter(varExpr::evaluate);
		return this;
	}
	
	public RandomizeMultiStream<T> group(String groupingVar)
	{
		return new RandomizeMultiStream<>(
				stream.collect(Collectors.groupingBy(
					x -> x.getVariableValue(groupingVar))));
	}
	
	public List<T> collect()
	{
		return stream.map(ReflectionObject::getObject).collect(Collectors.toList());
	}
}
