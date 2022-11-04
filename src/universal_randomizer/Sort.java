package universal_randomizer;

import java.util.Comparator;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.WrappedComparable;
import universal_randomizer.wrappers.WrappedComparator;

public class Sort<T> extends IntermediateAction<T>
{
	Comparator<ReflectionObject<T>> sorter;

	private Sort(Comparator<ReflectionObject<T>> sorter, StreamAction<T> nextAction)
	{
		super(nextAction);
		this.sorter = sorter;
	}
	
	public static <M extends Comparable<M>> Sort<M> createComparable(StreamAction<M> nextAction)
	{
		return new Sort<>(new WrappedComparable<>(), nextAction);
	}
	
	public static <M> Sort<M> createComparator(Comparator<M> sorter, StreamAction<M> nextAction)
	{
		return new Sort<>(new WrappedComparator<>(sorter), nextAction);
	}
	
	public static <T> Sort<T> createWrappedComparator(Comparator<ReflectionObject<T>> sorter, StreamAction<T> nextAction)
	{
		return new Sort<>(sorter, nextAction);
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		return continueActions(objStream.sorted(sorter));
	}
}
