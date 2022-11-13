package universal_randomizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils 
{
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
	
	// TODO: implement
	public static <T> T deepCopy(T obj)
	{
		return obj;
	}
}
