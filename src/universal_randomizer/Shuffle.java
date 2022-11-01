package universal_randomizer;

import java.util.stream.Stream;

public class Shuffle extends IntermediateAction
{
    protected Shuffle(StreamAction nextAction)
    {
        super(nextAction);
    }

    @Override
    public boolean perform(Stream<ReflectionObject> objStream) 
    {
        return false;
    }
}