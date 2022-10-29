package universal_randomizer;

import java.util.Comparator;
import java.util.stream.Stream;

public class Sort<T extends Object> extends IntermediateAction<T>
{
	Comparator<T> sorter;
	
	protected Sort(Comparator<T> sorter, StreamAction<T> nextAction)
	{
		super(nextAction);
		this.sorter = sorter;
	}
	
	protected Sort(StreamAction<T> nextAction)
	{
		super(nextAction);
		this.sorter = null;
	}

	@Override
	public boolean perform(Stream<T> objStream) 
	{
		if (sorter != null)
		{
			return continueActions(objStream.sorted(sorter));
		}
		return continueActions(objStream.sorted());
	}
}
