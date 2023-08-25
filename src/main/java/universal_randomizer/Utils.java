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
	
	public static <S, V> Stream<V> narrowToField(String pathToField, Stream<ReflectionObject<S>> objStream)
	{
		// TODO: Type safety
		return (Stream<V>) objStream.flatMap(obj -> obj.getFieldStream(pathToField));
	}
	
	public static <S, V> Stream<V> narrowToMapField(String pathToField, Stream<ReflectionObject<S>> objStream, boolean valuesNotKeys)
	{
		// TODO: Type safety
		return (Stream<V>) objStream.flatMap(obj -> obj.getMapFieldStream(pathToField, valuesNotKeys));
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
	public static Stream<Object> convertToStream(Object obj)
	{
		if (obj == null)
		{
			return Stream.empty();
		}
		else if (obj instanceof Collection)
		{
			return ((Collection<Object>) obj).stream();
		}
		else if (obj.getClass().isArray())
		{
			return convertArrayToStream(obj);
		}
		else if (obj instanceof Map)
		{
			return ((Map<?,Object>) obj).values().stream();
		}
		return Stream.of(obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> convertToStream(Object obj, Class<T> tClass)
	{
		if (obj == null || tClass == null)
		{
			return Stream.empty();
		}
		else if (obj instanceof Collection)
		{
			Collection<?> asCol = (Collection<?>) obj;
			if (asCol.isEmpty() || !tClass.isInstance(asCol.iterator().next()))
			{
				return Stream.empty();
			}
			return ((Collection<T>) obj).stream();
		}
		else if (obj.getClass().isArray())
		{
			return convertArrayToStream(obj);
		}
		else if (obj instanceof Map)
		{
			Map<?,?> asCol = (Map<?,?>) obj;
			if (asCol.isEmpty() || !tClass.isInstance(asCol.values().iterator().next()))
			{
				return Stream.empty();
			}
			return ((Map<?,T>) obj).values().stream();
		}
		return Stream.of((T) obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> convertArrayToStream(Object array)
	{
		if (array != null)
		{
			if (array.getClass().getComponentType().isPrimitive())
			{
				return convertPrimativeArrayToStream(array);
			}
			
			// Non primatives can be casted safely
			// TODO: Can I legally do this or will I need to pass in
			// the type?
			T[] casted = (T[]) array;
			if (array.getClass().getComponentType().isAssignableFrom(casted.getClass().getComponentType()))
			{
				return Arrays.stream(casted);
			}
		}
		return Stream.empty();
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
	
	public static String classArrayToString(Class<?>... classes)
	{
		if (classes != null && classes.length > 0)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(classes[0] != null ? classes[0].getName() : "null");
			for (int classIdx = 1; classIdx < classes.length; classIdx++)
			{
				sb.append(", ");
				sb.append(classes[classIdx] != null ? classes[classIdx].getName() : "null");
			}
			return sb.toString();
		}
		else
		{
			return "None";
		}
	}

	public static Class<?> tryUnboxToPrimitive(Class<?> classToTry) 
	{
		return tryUnboxToPrimitive(classToTry, true);
	}

	public static Class<?> tryUnboxToPrimitive(Class<?> classToTry, boolean returnIfAlreadyPrimitive) 
	{
		if (classToTry != null)
		{
			if (classToTry.isPrimitive())
			{
				if (returnIfAlreadyPrimitive)
				{
					return classToTry;
				}
			}
			else if (classToTry == Byte.class)
			{
				return Byte.TYPE;
			}
			else if (classToTry == Short.class)
			{
				return Short.TYPE;
			}
			else if (classToTry == Integer.class)
			{
				return Integer.TYPE;
			}
			else if (classToTry == Float.class)
			{
				return Float.TYPE;
			}
			else if (classToTry == Double.class)
			{
				return Double.TYPE;
			}
			else if (classToTry == Boolean.class)
			{
				return Boolean.TYPE;
			}
			else if (classToTry == Character.class)
			{
				return Character.TYPE;
			}
			else if (classToTry == Void.class)
			{
				return Void.TYPE;
			}
		}
		return null;
	}
}
