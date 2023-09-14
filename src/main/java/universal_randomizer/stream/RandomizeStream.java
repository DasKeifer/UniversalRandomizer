package universal_randomizer.stream;

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


public interface RandomizeStream<T>
{
	public static <T2> RandomizeStream<T2> create(Stream<T2> source)
	{
		return RandomizeSingleStream.create(source);
	}
	
	public static <T2> RandomizeStream<T2> create(Collection<T2> source)
	{
		return RandomizeSingleStream.create(source);
	}
	
	public static <T2> RandomizeStream<T2> create(T2[] source)
	{
		return RandomizeSingleStream.create(source);
	}
	
	public static <T2> RandomizeStream<T2> create(T2 item)
	{
		return RandomizeSingleStream.create(item);
	}
	
	public boolean isMultiStream();
	
	public RandomizeStream<T> select(Condition<T> varExpr);
	
	// Consumes underlying stream and recreates it
	public RandomizeStream<T> duplicate();

	// Consumes underlying stream and recreates it
	public List<RandomizeStream<T>> duplicate(int numCopies);

	// Consumes underlying stream and recreates it
	public <R> RandomizeMultiStream<T> group(Getter<T, R> groupingFn);

	public RandomizeStream<T> shuffle();
	
	public RandomizeStream<T> shuffle(long seed);
	
	public RandomizeStream<T> shuffle(Random rand);
	
	public RandomizeStream<T> sort();
	
	public RandomizeStream<T> sort(Comparator<T> sorter);

	// Consumes this object
	public void forEach(Consumer<? super T> action);

	// Consumes this object
	public void forEachStream(Consumer<? super RandomizeSingleStream<T>> action);

	public <R> RandomizeStream<R> map(Function<? super T, ? extends R> mapper);
	
	public <R> RandomizeStream<R> mapStreams(Function<? super RandomizeSingleStream<T>, ? extends R> mapper);
	
	// Consumes this object
	public Stream<T> toStream();
	
	// public List<RandomizeStream<T>> split();
	
	// TODO
//	public Map<?, RandomizeStream<T>> splitMap();
	
	public <R> RandomizeStream<R> convertToField(Getter<T, R> getter);
	
	public <R> RandomizeStream<R> convertToFieldArray(Getter<T, R[]> getter);

	public <S extends Stream<R>, R> RandomizeStream<R> convertToFieldStream(Getter<T, S> getter);
	
	public <C extends Collection<R>, R> RandomizeStream<R> convertToFieldCollection(Getter<T, C> getter);
	
	public <M extends Map<R, ?>, R> RandomizeStream<R> convertToFieldMapKeys(Getter<T, M> getter);
	
	public <M extends Map<?, R>, R> RandomizeStream<R> convertToFieldMapValues(Getter<T, M> getter);
}
