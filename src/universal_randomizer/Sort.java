package universal_randomizer;

import java.util.Comparator;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.action.IntermediateAction;
import universal_randomizer.action.ReflObjStreamAction;
import universal_randomizer.wrappers.ComaprableReflObjWrapper;
import universal_randomizer.wrappers.ComparatorReflObjWrapper;

public class Sort<T> extends IntermediateAction<T>
{
	Comparator<ReflectionObject<T>> sorter;

	private Sort(Comparator<ReflectionObject<T>> sorter, ReflObjStreamAction<T> nextAction)
	{
		super(nextAction);
		this.sorter = sorter;
	}
	
	public static <M extends Comparable<M>> Sort<M> createComparable(ReflObjStreamAction<M> nextAction)
	{
		return new Sort<>(new ComaprableReflObjWrapper<>(), nextAction);
	}
	
	public static <M> Sort<M> createComparator(Comparator<M> sorter, ReflObjStreamAction<M> nextAction)
	{
		return new Sort<>(new ComparatorReflObjWrapper<>(sorter), nextAction);
	}
	
	public static <T> Sort<T> createWrappedComparator(Comparator<ReflectionObject<T>> sorter, ReflObjStreamAction<T> nextAction)
	{
		return new Sort<>(sorter, nextAction);
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		return continueActions(objStream.sorted(sorter));
	}
}
