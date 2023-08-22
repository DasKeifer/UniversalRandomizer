package universal_randomizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import universal_randomizer.user_object_apis.Sum;
import universal_randomizer.user_object_apis.Sumable;
import universal_randomizer.wrappers.ComparableAsComparator;
import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.SumableAsSum;

public class Utils 
{	
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
	
	public static <S, V> Stream<V> narrowToField(String pathToField, Stream<ReflectionObject<S>> objStream)
	{
		return objStream.flatMap(obj -> obj.getFieldStream(pathToField));
	}
	
	public static <S, V> Stream<V> narrowToMapField(String pathToField, Stream<ReflectionObject<S>> objStream, boolean valuesNotKeys)
	{
		return objStream.flatMap(obj -> obj.getMapFieldStream(pathToField, valuesNotKeys));
	}
	
	public static <S, V> Stream<V> narrowToMapKeyField(String pathToField, Stream<ReflectionObject<S>> objStream)
	{
		return narrowToMapField(pathToField, objStream, false);
	}
	
	public static <S, V> Stream<V> narrowToMapValueField(String pathToField, Stream<ReflectionObject<S>> objStream)
	{
		return narrowToMapField(pathToField, objStream, true);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> convertToStream(Object obj)
	{
		if (obj == null)
		{
			return Stream.empty();
		}
		else if (obj instanceof Collection)
		{
			return ((Collection<T>) obj).stream();
		}
		else if (obj.getClass().isArray())
		{
			return convertArrayToStream(obj);
		}
		else if (obj instanceof Map)
		{
			return ((Map<?,T>) obj).values().stream();
		}
		return Stream.of((T) obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> convertArrayToStream(Object array)
	{
		if (array == null)
		{
			return Stream.empty();
		}
		if (array.getClass().getComponentType().isPrimitive())
		{
			return convertPrimativeArrayToStream(array);
		}
		
		// Non primatives can be casted safely
		return Arrays.stream((T[]) array);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> convertPrimativeArrayToStream(Object primativeArray)
	{
		if (primativeArray == null)
		{
			return Stream.empty();
		}
		
		Class<?> primType = primativeArray.getClass().getComponentType();
		if (primType == byte.class)
		{
			byte[] narrowed = (byte[]) primativeArray;
			return IntStream.range(0, narrowed.length)
                    .mapToObj(idx -> (T) Byte.valueOf(narrowed[idx]));
		}
		else if (primType == short.class)
		{
			short[] narrowed = (short[]) primativeArray;
			return IntStream.range(0, narrowed.length)
                    .mapToObj(idx -> (T) Short.valueOf(narrowed[idx]));
		}
		else if (primType == int.class)
		{
			return (Stream<T>) Arrays.stream((int[]) primativeArray).boxed();
		}
		else if (primType == float.class)
		{
			float[] narrowed = (float[]) primativeArray;
			return IntStream.range(0, narrowed.length)
                    .mapToObj(idx -> (T) Float.valueOf(narrowed[idx]));
		}
		else if (primType == double.class)
		{
			return (Stream<T>) Arrays.stream((double[]) primativeArray).boxed();
		}
		else if (primType == boolean.class)
		{
			boolean[] narrowed = (boolean[]) primativeArray;
			return IntStream.range(0, narrowed.length)
                    .mapToObj(idx -> (T) Boolean.valueOf(narrowed[idx]));
		}
		else if (primType == char.class)
		{
			char[] narrowed = (char[]) primativeArray;
			return IntStream.range(0, narrowed.length)
                    .mapToObj(idx -> (T) Character.valueOf(narrowed[idx]));
		}
		return Stream.empty();
	}
	
	public static <M> M safeCast(Class<M> clazz, Object obj)
	{
		if (obj.getClass().equals(clazz))
		{
			return clazz.cast(obj);
		}
		return null;
	}
	
	// TODO: implement?
	public static <T> T deepCopy(T obj)
	{
		return obj;
	}
}
