package universal_randomizer.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.Getter;

public class RandomizeMultiStream<T> implements RandomizeStream<T>
{
	// Don't store as a stream so we can repeat actions on it instead of
	// continually needing to create new ones
	Stream<RandomizeStream<T>> streams;
	// Grouping var (replace with map?)
	
	protected RandomizeMultiStream(Stream<RandomizeStream<T>> streams) 
	{
		this.streams = streams;
	}

	public static <T2> RandomizeMultiStream<T2> create(Stream<RandomizeStream<T2>> source)
	{
		return new RandomizeMultiStream<>(source);
	}
	
	public static <T2> RandomizeMultiStream<T2> create(Collection<RandomizeStream<T2>> source)
	{
		if (source == null)
		{
			return null;
		}
		return create(source.stream());
	}
	
	public static <T2> RandomizeMultiStream<T2> create(RandomizeStream<T2> item)
	{
		return create(Stream.of(item));
	}

	@Override
	public boolean isMultiStream()
	{
		return true;
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
	public RandomizeMultiStream<T> duplicate() 
	{
		List<RandomizeStream<T>> lists = streams.toList();
		streams = lists.stream();
		
		List<RandomizeStream<T>> copied = new ArrayList<>(lists.size());
		for (RandomizeStream<T> stream : lists)
		{
			copied.add(stream.duplicate());
		}
		return RandomizeMultiStream.create(copied.stream());
	}

	@Override
	public List<RandomizeStream<T>> duplicate(int numCopies) 
	{
		List<RandomizeStream<T>> lists = streams.toList();
		streams = lists.stream();
		
		// Tempting to use the single duplicate function but
		// each time it will convert to array and has to re-stream
		// the original and the copy so its significantly less 
		// efficient
		List<List<RandomizeStream<T>>> dups = new ArrayList<>(numCopies);
		for (RandomizeStream<T> stream : lists)
		{
			dups.add(stream.duplicate(numCopies));
		}
		
		List<RandomizeStream<T>> duplicated = new ArrayList<>(numCopies);
		for (int i = 0; i < numCopies; i++)
		{
			duplicated.add(RandomizeMultiStream.create(dups.get(i).stream()));
		}
		return duplicated;
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
	public void forEach(Consumer<? super T> action) 
	{
		streams.forEach(s -> s.forEach(action));
	}
	
	@Override
	public void forEachStream(Consumer<? super RandomizeSingleStream<T>> action) 
	{
		streams.forEach(s -> s.forEachStream(action));
	}

	@Override
	public <R> RandomizeMultiStream<R> map(Function<? super T, ? extends R> mapper) 
	{
		return RandomizeMultiStream.create(streams.map(s -> s.map(mapper)));
	}

	@Override
	public <R> RandomizeMultiStream<R> mapStreams(Function<? super RandomizeSingleStream<T>, ? extends R> mapper)
	{
		return RandomizeMultiStream.create(streams.map(s -> s.mapStreams(mapper)));
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
		return RandomizeMultiStream.create(streams.map(stream -> stream.convertToField(getter)));
	}

	@Override
	public <R> RandomizeMultiStream<R> convertToFieldArray(Getter<T, R[]> getter)
	{
		if (getter == null)
		{
			return null;
		}
		return RandomizeMultiStream.create(streams.map(stream -> stream.convertToFieldArray(getter)));
	}

	@Override
	public <C extends Collection<R>, R> RandomizeMultiStream<R> convertToFieldCollection(Getter<T, C> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return RandomizeMultiStream.create(streams.map(stream -> stream.convertToFieldCollection(getter)));
	}

	@Override
	public <S extends Stream<R>, R> RandomizeMultiStream<R> convertToFieldStream(Getter<T, S> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return RandomizeMultiStream.create(streams.map(stream -> stream.convertToFieldStream(getter)));
	}

	@Override
	public <M extends Map<R, ?>, R> RandomizeMultiStream<R> convertToFieldMapKeys(Getter<T, M> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return RandomizeMultiStream.create(streams.map(stream -> stream.convertToFieldMapKeys(getter)));
	}

	@Override
	public <M extends Map<?, R>, R> RandomizeMultiStream<R> convertToFieldMapValues(Getter<T, M> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return RandomizeMultiStream.create(streams.map(stream -> stream.convertToFieldMapValues(getter)));
	}
}
