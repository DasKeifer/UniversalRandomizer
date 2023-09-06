package universal_randomizer.stream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.Utils;
import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.wrappers.RandomOrdering;

public class RandomizeSingleStream<T> implements RandomizeStream<T>
{
	private Stream<T> stream;

	protected RandomizeSingleStream(Stream<T> source)
	{
		this.stream = source;
	}

	public static <T2> RandomizeSingleStream<T2> create(Stream<T2> source)
	{
		if (source == null)
		{
			return null;
		}
		return new RandomizeSingleStream<>(source);
	}
	
	public static <T2> RandomizeSingleStream<T2> create(Collection<T2> source)
	{
		if (source == null)
		{
			return null;
		}
		return create(source.stream());
	}
	
	public static <T2> RandomizeSingleStream<T2> create(T2[] source)
	{
		if (source == null)
		{
			return null;
		}
		return create(Utils.convertArrayToStream(source));
	}

	@Override
	public RandomizeSingleStream<T> select(Condition<T> varExpr)
	{
		if (varExpr == null)
		{
			return null;
		}
		stream = stream.filter(varExpr::evaluate);
		return this;
	}

	@Override
	public <R> RandomizeMultiStream<T> group(Getter<T, R> groupingFn)
	{
		if (groupingFn == null)
		{
			return null;
		}
		
		Map<R, List<T>> grouped = stream
				.collect(Collectors.groupingBy(groupingFn::get));
		
		return new RandomizeMultiStream<>(
				grouped.entrySet().stream()
					.map(entry -> new RandomizeSingleStream<>(entry.getValue().stream())));
	}

	@Override
	public RandomizeSingleStream<T> shuffle()
	{
		return shuffle(null);
	}

	@Override
	public RandomizeSingleStream<T> shuffle(long seed)
	{
		return shuffle(new Random(seed));
	}
	
	@Override
	public RandomizeSingleStream<T> shuffle(Random rand) 
	{
		final Random nonNull = rand != null ? rand : new Random();
		stream = stream
				.map(o -> RandomOrdering.create(o, nonNull.nextLong()))
				.sorted(RandomOrdering::sortBySortingValue)
				.map(RandomOrdering::getObject);
		return this;
	}
	
	@Override
	public RandomizeSingleStream<T> sort()
	{
		return sort(null);
	}

	@Override
	public RandomizeSingleStream<T> sort(Comparator<T> sorter)
	{
		stream = sorter != null ? stream.sorted(sorter) : stream.sorted();
		return this;
	}

	@Override
	public <R> RandomizeSingleStream<R> convertToField(Getter<T, R> getter)
	{
		return getter != null ? RandomizeSingleStream.create(stream.map(getter::get)) : null;
	}

	@Override
	public <R> RandomizeSingleStream<R> convertToFieldArray(Getter<T, R[]> getter)
	{
		return getter != null ? RandomizeSingleStream.create(stream.flatMap(o -> Arrays.stream(getter.get(o)))) : null;
	}

	@Override
	public <C extends Collection<R>, R> RandomizeSingleStream<R> convertToFieldCollection(Getter<T, C> getter)
	{
		return getter != null ? RandomizeSingleStream.create(stream.flatMap(obj -> getter.get(obj).stream())) : null;
	}

	@Override
	public <S extends Stream<R>, R> RandomizeSingleStream<R> convertToFieldStream(Getter<T, S> getter) 
	{
		return getter != null ? RandomizeSingleStream.create(stream.flatMap(getter::get)) : null;
	}

	@Override
	public <M extends Map<R, ?>, R> RandomizeSingleStream<R> convertToFieldMapKeys(Getter<T, M> getter)
	{
		return getter != null ? RandomizeSingleStream.create(stream.flatMap(obj -> getter.get(obj).keySet().stream())) : null;
	}

	@Override
	public <M extends Map<?, R>, R> RandomizeSingleStream<R> convertToFieldMapValues(Getter<T, M> getter)
	{
		return getter != null ? RandomizeSingleStream.create(stream.flatMap(obj -> getter.get(obj).values().stream())) : null;
	}
	
	@Override
	public Stream<T> toStream()
	{
		return stream;
	}

	protected boolean setStream(Stream<T> stream) 
	{
		if (stream == null)
		{
			return false;
		}
		this.stream = stream;
		return true;
	}
}
