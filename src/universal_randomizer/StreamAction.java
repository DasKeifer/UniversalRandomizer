package universal_randomizer;

import java.util.stream.Stream;

public interface StreamAction<T extends Object> 
{
	public boolean perform(Stream<ReflectionObject<T>> objStream);
}
