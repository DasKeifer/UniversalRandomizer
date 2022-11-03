package universal_randomizer;

import java.util.Comparator;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.WrappedComparable;
import universal_randomizer.wrappers.WrappedComparator;

public class Sort<T extends Object> extends IntermediateAction<T>
{
	Comparator<ReflectionObject<T>> sorter;

	// TODO: Refactor to use inheritance instead of factory and then use constructor?
	
	private Sort(Comparator<ReflectionObject<T>> sorter, StreamAction<T> nextAction)
	{
		super(nextAction);
		this.sorter = sorter;
	}
	
	public static <T extends Comparable<T>> Sort<T> comparableSort(StreamAction<T> nextAction)
	{
		return new Sort<>(new WrappedComparable<>(), nextAction);
	}
	
	public static <T> Sort<T> comparatorSort(Comparator<T> sorter, StreamAction<T> nextAction)
	{
		return new Sort<>(new WrappedComparator<>(sorter), nextAction);
	}
	
	public static <T> Sort<T> wrappedComparatorSort(Comparator<ReflectionObject<T>> sorter, StreamAction<T> nextAction)
	{
		return new Sort<>(sorter, nextAction);
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		if (sorter != null)
		{
			return continueActions(objStream.sorted(sorter));
		}
		
		System.err.println("No sorter given");
		return false;
	}
}
