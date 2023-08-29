package universal_randomizer.stream;

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
	Stream<T> stream;

	public RandomizeSingleStream(Collection<T> source)
	{
		this.stream = source.stream();
	}
	
	public RandomizeSingleStream(Stream<T> source)
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
	public <R> RandomizeMultiStream<T> group(Getter<T, R> groupingFn)
	{
		Map<Object, List<T>> grouped = stream
				.collect(Collectors.groupingBy(groupingFn::get));
		
		return new RandomizeMultiStream<>(
				grouped.entrySet().stream()
					.map(entry -> new RandomizeSingleStream<>(entry.getValue().stream())));
	}

	@Override
	public RandomizeSingleStream<T> shuffle(Random rand) 
	{
		stream = stream
				.map(o -> RandomOrdering.create(o, rand.nextLong()))
				.sorted(RandomOrdering::sortBySortingValue)
				.map(RandomOrdering::getObject);
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
		stream = stream.sorted(sorter);
		return this;
	}
	
	@Override
	public Stream<T> toStream()
	{
		return stream;
	}

	@Override
	public <R> RandomizeStream<R> convertToField(Getter<T, R> getter)
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeSingleStream<>(stream.map(getter::get));
	}

	@Override
	public <R> RandomizeSingleStream<R> convertToFieldArray(Getter<T, R[]> getter)
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeSingleStream<>(stream.flatMap(o -> Utils.convertArrayToStream(getter.get(o))));
	}

	@Override
	public <C extends Collection<R>, R> RandomizeStream<R> convertToFieldCollection(Getter<T, C> getter)
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeSingleStream<>(stream.flatMap(obj -> getter.get(obj).stream()));
	}

	@Override
	public <S extends Stream<R>, R> RandomizeStream<R> convertToFieldStream(Getter<T, S> getter) 
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeSingleStream<>(stream.flatMap(getter::get));
	}

	@Override
	public <M extends Map<R, ?>, R> RandomizeStream<R> convertToFieldMapKeys(Getter<T, M> getter)
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeSingleStream<>(stream.flatMap(obj -> getter.get(obj).keySet().stream()));
	}

	@Override
	public <M extends Map<?, R>, R> RandomizeStream<R> convertToFieldMapValues(Getter<T, M> getter)
	{
		if (getter == null)
		{
			return null;
		}
		return new RandomizeSingleStream<>(stream.flatMap(obj -> getter.get(obj).values().stream()));
	}
}
