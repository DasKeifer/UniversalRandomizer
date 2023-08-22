package universal_randomizer.stream;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Stream;

import universal_randomizer.condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;

public class RandomizeMultiStream<T> implements RandomizeStream<T>
{
	// Don't store as a stream so we can repeat actions on it instead of
	// continually needing to create new ones
	Stream<RandomizeStream<T>> streams;
	
	protected RandomizeMultiStream(Stream<RandomizeStream<T>> streams) 
	{
		this.streams = streams;
	}

	@Override
	public RandomizeMultiStream<T> select(Condition<T> varExpr)
	{
		streams = streams.map(stream -> stream.select(varExpr));
		return this;
	}

	@Override
	public RandomizeMultiStream<T> group(String groupingVar) 
	{
		streams = streams.map(stream -> stream.group(groupingVar));
		return this;
	}

	@Override
	public RandomizeStream<T> shuffle(Random rand)
	{
		streams = streams.map(stream -> stream.shuffle(rand));
		return this;
	}

	@Override
	public RandomizeStream<T> sort()
	{
		streams = streams.map(RandomizeStream::sort);
		return this;
	}

	@Override
	public RandomizeStream<T> sort(Comparator<T> sorter)
	{
		streams = streams.map(stream -> stream.sort(sorter));
		return this;
	}

	@Override
	public RandomizeStream<T> sortWrapped(Comparator<ReflectionObject<T>> wrappedSorter)
	{
		streams = streams.map(stream -> stream.sortWrapped(wrappedSorter));
		return this;
	}

	@Override
	public Stream<T> toStream()
	{
		return streams.flatMap(RandomizeStream::toStream);
	}

	@Override
	public Stream<ReflectionObject<T>> toWrappedStream()
	{
		return streams.flatMap(RandomizeStream::toWrappedStream);
	}
	
	@Override
	public <A, R> R collect(Collector<? super T, A, R> collector)
	{
		return toStream().collect(collector);
	}

	@Override
	public <A, R> R collectWrapped(Collector<? super ReflectionObject<T>, A, R> collector)
	{
		return toWrappedStream().collect(collector);
	}
}
