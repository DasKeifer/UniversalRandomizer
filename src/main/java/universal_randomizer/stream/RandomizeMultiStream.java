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
		if (varExpr == null)
		{
			return null;
		}
		streams = streams.map(stream -> stream.select(varExpr));
		return this;
	}

	@Override
	public <R> RandomizeMultiStream<T> group(Getter<T, R> groupingFn)
	{
		if (groupingFn == null)
		{
			return null;
		}
		streams = streams.map(stream -> stream.group(groupingFn));
		return this;
	}

	@Override
	public RandomizeMultiStream<T> shuffle()
	{
		return shuffle(null);
	}

	@Override
	public RandomizeMultiStream<T> shuffle(long seed)
	{
		return shuffle(new Random(seed));
	}

	@Override
	public RandomizeMultiStream<T> shuffle(Random rand)
	{
		streams = streams.map(stream -> stream.shuffle(rand));
		return this;
	}

	@Override
	public RandomizeMultiStream<T> sort()
	{
		return sort(null);
	}

	@Override
	public RandomizeMultiStream<T> sort(Comparator<T> sorter)
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
	public <R> RandomizeMultiStream<R> convertToField(Getter<T, R> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToField(getter)));
	}

	@Override
	public <R> RandomizeMultiStream<R> convertToFieldArray(Getter<T, R[]> getter)
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldArray(getter)));
	}

	@Override
	public <C extends Collection<R>, R> RandomizeMultiStream<R> convertToFieldCollection(Getter<T, C> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldCollection(getter)));
	}

	@Override
	public <S extends Stream<R>, R> RandomizeMultiStream<R> convertToFieldStream(Getter<T, S> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldStream(getter)));
	}

	@Override
	public <M extends Map<R, ?>, R> RandomizeMultiStream<R> convertToFieldMapKeys(Getter<T, M> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldMapKeys(getter)));
	}

	@Override
	public <M extends Map<?, R>, R> RandomizeMultiStream<R> convertToFieldMapValues(Getter<T, M> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeMultiStream<>(streams.map(stream -> stream.convertToFieldMapValues(getter)));
	}
}
