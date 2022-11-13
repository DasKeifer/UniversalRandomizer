package universal_randomizer.wrappers;

import java.util.stream.Stream;

import universal_randomizer.action.ReflObjStreamAction;
import universal_randomizer.action.StreamAction;


public class StreamActionReflObjWrapper<T> implements ReflObjStreamAction<T> 
{
	StreamAction<T> wrapped;
	
	public StreamActionReflObjWrapper(StreamAction<T> action)
	{
		this.wrapped = action;
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		return wrapped.perform(objStream.map(ReflectionObject::getObject));
	}
}
