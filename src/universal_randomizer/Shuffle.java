package universal_randomizer;

import java.util.stream.Stream;

public class Shuffle<T extends Object> extends IntermediateAction<T>
{
	protected Shuffle(StreamAction<T> nextAction)
	{
		super(nextAction);
	}

	@Override
	public boolean perform(Stream<T> objStream) 
	{
		return false;
	}
}
