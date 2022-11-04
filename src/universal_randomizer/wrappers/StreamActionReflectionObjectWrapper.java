package universal_randomizer.wrappers;

import java.util.stream.Stream;

import universal_randomizer.interfaces.StreamAction;


public class StreamActionReflectionObjectWrapper<T> implements ReflectionObjectStreamAction<T> 
{
	StreamAction<T> wrapped;
	
	public StreamActionReflectionObjectWrapper(StreamAction<T> action)
	{
		this.wrapped = action;
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		return wrapped.perform(objStream.map(ReflectionObject::getObject));
	}
}
