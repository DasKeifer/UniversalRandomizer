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
		return stream.flatMap(o -> convertArrayToStream(getter.get(o)));
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
		if (array != null)
		{
			// Primitives have to be handled specially so we can convert
			// them to their boxed types
			if (array.getClass().getComponentType().isPrimitive())
			{
				return convertPrimativeArrayToStream(array);
			}
			return Arrays.stream(array);
		}
		return null;
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
