package universal_randomizer.action;

import java.util.stream.Stream;

@FunctionalInterface
public interface StreamAction<T> 
{
	public boolean perform(Stream<T> obj);
}
