package universal_randomizer;

import java.util.stream.Stream;

public interface StreamAction 
{
	public boolean perform(Stream<ReflectionObject> objStream);
}
