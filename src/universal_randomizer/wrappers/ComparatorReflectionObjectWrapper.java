package universal_randomizer.wrappers;

import java.util.Comparator;

public class ComparatorReflectionObjectWrapper<T> implements Comparator<ReflectionObject<T>>
{
	Comparator<T> unwrapped;
	
	public ComparatorReflectionObjectWrapper(Comparator<T> unwrapped)
	{
		this.unwrapped = unwrapped;
	}

	@Override
	public int compare(ReflectionObject<T> o1, ReflectionObject<T> o2) {
		return unwrapped.compare(o1.getObject(), o2.getObject());
	}

}
