package universal_randomizer.stream;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;

public interface RandomizeStream<T>
{
	public static <S> RandomizeStream<S> createRandomizeStream(Collection<ReflectionObject<S>> source)
	{
		return new RandomizeSingleStream<>(source);
	}
	
	public static <S> RandomizeStream<S> createRandomizeStream(Stream<ReflectionObject<S>> source)
	{
		return new RandomizeSingleStream<>(source);
	}
	
	public RandomizeStream<T> select(Condition<T> varExpr);
	
	public RandomizeMultiStream<T> group(String groupingVar);

	public default RandomizeStream<T> shuffle()
	{
		return shuffle(new Random());
	}
	
	public default RandomizeStream<T> shuffle(long seed)
	{
		return shuffle(new Random(seed));
	}
	
	public RandomizeStream<T> shuffle(Random rand);
	
	public RandomizeStream<T> sort();
	
	public RandomizeStream<T> sort(Comparator<T> sorter);
	
	public RandomizeStream<T> sortWrapped(Comparator<ReflectionObject<T>> wrappedSorter);

	public Stream<T> toStream();
	
	public Stream<ReflectionObject<T>> toWrappedStream();
	
	public default List<T> toList()
	{
		return collect(Collectors.toList());
	}
	
	public default List<ReflectionObject<T>> toWrappedList()
	{
		return collectWrapped(Collectors.toList());
	}

	public <A, R> R collect(Collector<? super T, A, R> collector);
	
	public <A, R> R collectWrapped(Collector<? super ReflectionObject<T>, A, R> collector);
}
