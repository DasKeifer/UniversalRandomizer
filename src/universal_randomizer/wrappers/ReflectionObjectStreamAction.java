package universal_randomizer.wrappers;

import java.util.stream.Stream;

public interface ReflectionObjectStreamAction<T> 
{
	public boolean perform(Stream<ReflectionObject<T>> objStream);
}
