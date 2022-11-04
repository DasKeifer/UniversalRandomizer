package universal_randomizer;

import java.util.Comparator;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.ComaprableReflectionObjectWrapper;
import universal_randomizer.wrappers.ComparatorReflectionObjectWrapper;
import universal_randomizer.wrappers.ReflectionObjectStreamAction;

public class Sort<T> extends IntermediateAction<T>
{
	Comparator<ReflectionObject<T>> sorter;

	private Sort(Comparator<ReflectionObject<T>> sorter, ReflectionObjectStreamAction<T> nextAction)
	{
		super(nextAction);
		this.sorter = sorter;
	}
	
	public static <M extends Comparable<M>> Sort<M> createComparable(ReflectionObjectStreamAction<M> nextAction)
	{
		return new Sort<>(new ComaprableReflectionObjectWrapper<>(), nextAction);
	}
	
	public static <M> Sort<M> createComparator(Comparator<M> sorter, ReflectionObjectStreamAction<M> nextAction)
	{
		return new Sort<>(new ComparatorReflectionObjectWrapper<>(sorter), nextAction);
	}
	
	public static <T> Sort<T> createWrappedComparator(Comparator<ReflectionObject<T>> sorter, ReflectionObjectStreamAction<T> nextAction)
	{
		return new Sort<>(sorter, nextAction);
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		return continueActions(objStream.sorted(sorter));
	}
}
