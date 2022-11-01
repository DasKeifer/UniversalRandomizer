package universal_randomizer;

import java.util.Comparator;
import java.util.stream.Stream;

public class Sort<T extends Object> extends IntermediateAction<T>
{
	Comparator<T> sorter;
	boolean implementsComparable;
	
	public Sort(Comparator<T> sorter, StreamAction<T> nextAction)
	{
		super(nextAction);
		this.sorter = sorter;
		implementsComparable = false;
	}
	
	public Sort(Class<T> tClass, StreamAction<T> nextAction)
	{
		super(nextAction);
		this.sorter = null;
		implementsComparable = Comparable.class.isAssignableFrom(tClass);
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
//		if (sorter != null)
//		{
//			return continueActions(objStream.sorted(sorter));
//		}
//		else if (implementsComparable)
//		{
//			return continueActions(objStream.sorted());
//		}
		
		System.err.println("Doesn't implement comparable");
		return false;
	}
}
