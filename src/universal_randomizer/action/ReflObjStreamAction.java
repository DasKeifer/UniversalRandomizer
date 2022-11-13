package universal_randomizer.action;

import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.StreamActionReflObjWrapper;

@FunctionalInterface
public interface ReflObjStreamAction<T> 
{
	public boolean perform(Stream<ReflectionObject<T>> objStream);
	
	public static <M> ReflObjStreamAction<M> create(StreamAction<M> action)
	{
		return new StreamActionReflObjWrapper<>(action);
	}
}
