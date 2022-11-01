package universal_randomizer;

import java.util.Comparator;

public class ReflectionComparator<T> implements Comparator<Object>
{
	Comparator<T> comp;
	
	public ReflectionComparator(Comparator<T> comp)
	{
		this.comp = comp; 
	}

	@Override
	public int compare(Object o1, Object o2) 
	{
		return comp.compare((T)o1, (T)o2);
	}
}
