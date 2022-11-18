package universal_randomizer.stream;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;

public class RandomizeMultiStream<T> implements RandomizeStream<T>
{
	// Don't store as a stream so we can repeat actions on it instead of
	// continually needing to create new ones
	Map<Object, RandomizeStream<T>> streams;
	
	public RandomizeMultiStream(Map<Object, List<ReflectionObject<T>>> streams) 
	{
		this.streams = streams.entrySet().stream().collect(
				Collectors.toMap(Entry::getKey, e -> new RandomizeSingleStream<T>(e.getValue())));
	}

	@Override
	public RandomizeMultiStream<T> select(Condition<T> varExpr)
	{
		streams.entrySet().stream().forEach(entry -> entry.getValue().select(varExpr));
		return this;
	}

	@Override
	public RandomizeMultiStream<T> group(String groupingVar) 
	{
		streams.entrySet().stream().forEach(entry -> entry.setValue(entry.getValue().group(groupingVar)));
		return this;
	}

	@Override
	public List<T> collect() 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
