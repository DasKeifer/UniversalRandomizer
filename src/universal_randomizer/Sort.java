package universal_randomizer;

import java.util.Comparator;
import java.util.stream.Stream;

public class Sort<T> extends IntermediateAction
{
    Comparator<T> sorter;
    
    public Sort(Comparator<T> sorter, StreamAction nextAction)
    {
        super(nextAction);
        this.sorter = sorter;
    }
    
    public Sort(StreamAction nextAction)
    {
        super(nextAction);
        this.sorter = null;
    }

    @Override
    public boolean perform(Stream<ReflectionObject> objStream) 
    {
        if (sorter != null)
        {
            return continueActions(objStream.sorted(
            		(lhs, rhs) -> sorter.compare((T)lhs.obj, (T)rhs.obj)));
        }
        return continueActions(objStream.sorted());
    }
}