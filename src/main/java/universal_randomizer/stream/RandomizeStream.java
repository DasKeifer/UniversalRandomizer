package universal_randomizer.stream;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.user_object_apis.Condition;
import universal_randomizer.user_object_apis.Getter;


public interface RandomizeStream<T>
{
	public static <S> RandomizeStream<S> createRandomizeStream(Collection<S> source)
	{
		return new RandomizeSingleStream<>(source);
	}
	
	public static <S> RandomizeStream<S> createRandomizeStream(Stream<S> source)
	{
		return new RandomizeSingleStream<>(source);
	}
	
	public RandomizeStream<T> select(Condition<T> varExpr);
	
	public <R> RandomizeMultiStream<T> group(Getter<T, R> groupingFn);

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

	public Stream<T> toStream();
	
	public <R> RandomizeStream<R> convertToField(Getter<T, R> getter);
	
	public <R> RandomizeStream<R> convertToFieldArray(Getter<T, R[]> getter);

	public <S extends Stream<R>, R> RandomizeStream<R> convertToFieldStream(Getter<T, S> getter);
	
	public <C extends Collection<R>, R> RandomizeStream<R> convertToFieldCollection(Getter<T, C> getter);
	
	public <M extends Map<R, ?>, R> RandomizeStream<R> convertToFieldMapKeys(Getter<T, M> getter);
	
	public <M extends Map<?, R>, R> RandomizeStream<R> convertToFieldMapValues(Getter<T, M> getter);
}
