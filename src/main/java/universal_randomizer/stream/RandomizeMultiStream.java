package universal_randomizer.stream;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.Getter;

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
	public <R> RandomizeMultiStream<T> group(Getter<T, R> groupingFn)
	{
		streams = streams.map(stream -> stream.group(groupingFn));
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
	public Stream<T> toStream()
	{
		return streams.flatMap(RandomizeStream::toStream);
	}

	@Override
	public <R> RandomizeStream<R> convertToField(Getter<T, R> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToField(getter)));
	}

	@Override
	public <R> RandomizeStream<R> convertToFieldArray(Getter<T, R[]> getter)
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldArray(getter)));
	}

	@Override
	public <C extends Collection<R>, R> RandomizeStream<R> convertToFieldCollection(Getter<T, C> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldCollection(getter)));
	}

	@Override
	public <S extends Stream<R>, R> RandomizeStream<R> convertToFieldStream(Getter<T, S> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldStream(getter)));
	}

	@Override
	public <M extends Map<R, ?>, R> RandomizeStream<R> convertToFieldMapKeys(Getter<T, M> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldMapKeys(getter)));
	}

	@Override
	public <M extends Map<?, R>, R> RandomizeStream<R> convertToFieldMapValues(Getter<T, M> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldMapValues(getter)));
	}
}
