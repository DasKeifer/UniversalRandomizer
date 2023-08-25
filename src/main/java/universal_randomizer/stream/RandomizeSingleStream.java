package universal_randomizer.stream;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.condition.Condition;
import universal_randomizer.wrappers.ComparatorReflObjWrapper;
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

	@Override
	public RandomizeSingleStream<T> select(Condition<T> varExpr)
	{
		stream = stream.filter(varExpr::evaluate);
		return this;
	}

	@Override
	public RandomizeMultiStream<T> group(String groupingVar)
	{
		Map<Object, List<ReflectionObject<T>>> grouped = stream
				.collect(Collectors.groupingBy(x -> x.getField(groupingVar)));
		
		return new RandomizeMultiStream<>(
				grouped.entrySet().stream()
					.map(entry -> new RandomizeSingleStream<>(entry.getValue().stream())));
	}

	@Override
	public RandomizeSingleStream<T> shuffle(Random rand) 
	{
		stream = stream
				.map(obj -> obj.setSortingValueReturnSelf(rand.nextInt()))
				.sorted(ReflectionObject::sortBySortingValue);
		return this;
	}

	@Override
	public RandomizeStream<T> sort() 
	{
		stream = stream.sorted();
		return this;
	}

	@Override
	public RandomizeStream<T> sort(Comparator<T> sorter)
	{
		stream = stream.sorted(new ComparatorReflObjWrapper<>(sorter));
		return this;
	}

	@Override
	public RandomizeStream<T> sortWrapped(Comparator<ReflectionObject<T>> wrappedSorter)
	{
		stream = stream.sorted(wrappedSorter);
		return this;
	}
	
	@Override
	public Stream<T> toStream()
	{
		return stream.map(ReflectionObject::getObject);
	}

	@Override
	public Stream<ReflectionObject<T>> toWrappedStream()
	{
		return stream;
	}

	@Override
	public <A, R> R collect(Collector<? super T, A, R> collector)
	{
		return stream.map(ReflectionObject::getObject).collect(collector);
	}
	
	@Override
	public <A, R> R collectWrapped(Collector<? super ReflectionObject<T>, A, R> collector)
	{
		return stream.collect(collector);
	}
}
