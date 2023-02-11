package universal_randomizer.wrappers;

import java.util.Comparator;

public class ComparableAsComparator<T extends Comparable<T>> implements Comparator<T>{

	@Override
	public int compare(T o1, T o2) 
	{
		if (o1 == null)
		{
			return 1;
		}
		else if (o2 == null)
		{
			return -1;
		}
		return o1.compareTo(o2);
	}

}
