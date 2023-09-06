package universal_randomizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import universal_randomizer.user_object_apis.Getter;
import universal_randomizer.user_object_apis.Sum;
import universal_randomizer.user_object_apis.Sumable;
import universal_randomizer.wrappers.ComparableAsComparator;
import universal_randomizer.wrappers.SumableAsSum;

public class Utils 
{	
	private Utils() 
	{
	    throw new IllegalStateException("Utility class");
	}
	
	public static <N extends Comparable<N> & Sumable<N>> Collection<N> createRange(N min, N max, N stepSize)
	{
		return createRange(min, max, stepSize, new SumableAsSum<>());
	}
	
	public static <N extends Comparable<N>> Collection<N> createRange(N min, N max, N stepSize, Sum<N> sumFn)
	{
		return createRange(min, max, stepSize, new ComparableAsComparator<>(), sumFn);
	}
	
	public static <N extends Sumable<N>> Collection<N> createRange(N min, N max, N stepSize, Comparator<N> comparator)
	{
		return createRange(min, max, stepSize, comparator, new SumableAsSum<>());
	}
	
	public static <N> Collection<N> createRange(N min, N max, N stepSize, Comparator<N> comparator, Sum<N> sumtor)
	{
		List<N> vals = new LinkedList<>();
		N nextVal = min;
		while (comparator.compare(nextVal, max) <= 0)
		{
			vals.add(nextVal);
			nextVal = sumtor.sum(nextVal, stepSize);
		}
		return vals;
	}

	public static <T, R> Stream<R> convertToField(Getter<T, R> getter, Stream<T> stream)
	{
		if (getter == null || stream == null)
		{
			return null;
		}
		return stream.map(getter::get);
	}

	public static <T, R> Stream<R> convertToFieldArray(Getter<T, R[]> getter, Stream<T> stream)
	{
		if (getter == null || stream == null)
		{
			return null;
		}
		return stream.flatMap(o -> Arrays.stream(getter.get(o)));
	}

	public static <T, C extends Collection<R>, R> Stream<R> convertToFieldCollection(Getter<T, C> getter, Stream<T> stream)
	{
		if (getter == null || stream == null)
		{
			return null;
		}
		return stream.flatMap(obj -> getter.get(obj).stream());
	}

	public static <T, S extends Stream<R>, R> Stream<R> convertToFieldStream(Getter<T, S> getter, Stream<T> stream)
	{
		if (getter == null || stream == null)
		{
			return null;
		}
		return stream.flatMap(getter::get);
	}

	public static <T, M extends Map<R, ?>, R> Stream<R> convertToFieldMapKeys(Getter<T, M> getter, Stream<T> stream)
	{
		if (getter == null || stream == null)
		{
			return null;
		}
		return stream.flatMap(obj -> getter.get(obj).keySet().stream());
	}

	public static <T, M extends Map<?, R>, R> Stream<R> convertToFieldMapValues(Getter<T, M> getter, Stream<T> stream)
	{
		if (getter == null || stream == null)
		{
			return null;
		}
		return stream.flatMap(obj -> getter.get(obj).values().stream());
	}
	
	public static <T> Stream<T> convertArrayToStream(T[] array)
	{
		return Arrays.stream(array);
	}
	
	public static Stream<Byte> convertPrimitiveArrayToStream(byte[] primitiveArray)
	{
		if (primitiveArray != null)
		{	
			return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
		}
		return null;
	}
	
	public static Stream<Short> convertPrimitiveArrayToStream(short[] primitiveArray)
	{
		if (primitiveArray != null)
		{	
			return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
		}
		return null;
	}
	
	public static Stream<Integer> convertPrimitiveArrayToStream(int[] primitiveArray)
	{
		if (primitiveArray != null)
		{	
			return Arrays.stream(primitiveArray).boxed();
		}
		return null;
	}
	
	public static Stream<Float> convertPrimitiveArrayToStream(float[] primitiveArray)
	{
		if (primitiveArray != null)
		{	
			return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
		}
		return null;
	}
	
	public static Stream<Double> convertPrimitiveArrayToStream(double[] primitiveArray)
	{
		if (primitiveArray != null)
		{	
			return Arrays.stream(primitiveArray).boxed();
		}
		return null;
	}
	
	public static Stream<Boolean> convertPrimitiveArrayToStream(boolean[] primitiveArray)
	{
		if (primitiveArray != null)
		{	
			return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
		}
		return null;
	}
	
	public static Stream<Character> convertPrimitiveArrayToStream(char[] primitiveArray)
	{
		if (primitiveArray != null)
		{	
			return IntStream.range(0, primitiveArray.length)
                    .mapToObj(idx -> primitiveArray[idx]);
		}
		return null;
	}
}
