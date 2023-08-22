package Support;

import java.util.ArrayList;
import java.util.List;

import universal_randomizer.wrappers.ReflectionObject;

public class SimpleObjectUtils 
{
	public static List<Integer> toIntFieldList(List<ReflectionObject<SimpleObject>> soList)
	{
		List<Integer> list = new ArrayList<>();
		for (ReflectionObject<SimpleObject> ro : soList)
		{
			list.add(ro.getObject().getIntField());
		}
		return list;
	}
}
