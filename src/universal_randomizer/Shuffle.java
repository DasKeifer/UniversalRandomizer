package universal_randomizer;

import java.util.stream.Stream;

public class Shuffle<T> extends IntermediateAction<T>
{
	protected Shuffle(StreamAction<T> nextAction)
	{
		super(nextAction);
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		return false;
	}
}
