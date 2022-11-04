package universal_randomizer.wrappers;

import universal_randomizer.interfaces.Sum;
import universal_randomizer.interfaces.Sumable;

public class SumableAsSum<T extends Sumable<T>> implements Sum<T>{

	@Override
	public T sum(T o1, T o2) 
	{
		if (o1 == null)
		{
			return null;
		}
		return o1.sum(o2);
	}
}
