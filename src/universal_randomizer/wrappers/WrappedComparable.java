package universal_randomizer.wrappers;

import java.util.Comparator;

public class WrappedComparable<T extends Comparable<T>> implements Comparator<ReflectionObject<T>>
{	
	@Override
	public int compare(ReflectionObject<T> o1, ReflectionObject<T> o2) {
		return o1.getObject().compareTo(o2.getObject());
	}

}