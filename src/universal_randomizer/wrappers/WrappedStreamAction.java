package universal_randomizer.wrappers;

import java.util.stream.Stream;

import universal_randomizer.StreamAction;

public class WrappedStreamAction<T extends Object> implements StreamAction<T> 
{
	UnwrappedStreamAction<T> action;
	
	public WrappedStreamAction(UnwrappedStreamAction<T> action)
	{
		this.action = action;
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		// TODO: Gather returns
		objStream.forEach(obj -> action.perform(obj.getObject()));
		return true;
	}
}
