package universal_randomizer.interfaces;

import java.util.stream.Stream;

public interface StreamAction<T> 
{
	// TODO: refactor with wrapped to use factory and make the "wrapped" the baseclass
	
	public boolean perform(Stream<T> obj);
}
