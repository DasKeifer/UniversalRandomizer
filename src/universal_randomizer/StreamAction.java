package universal_randomizer;

import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;

public interface StreamAction<T extends Object> 
{
	public boolean perform(Stream<ReflectionObject<T>> objStream);
}
