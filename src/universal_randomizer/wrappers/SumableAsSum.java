package universal_randomizer.wrappers;

import universal_randomizer.user_object_apis.Sum;
import universal_randomizer.user_object_apis.Sumable;

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
