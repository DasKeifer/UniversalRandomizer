package tests.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import universal_randomizer.condition.Compare;
import universal_randomizer.condition.Negate;
import universal_randomizer.condition.SimpleCondition;
import universal_randomizer.wrappers.ReflectionObject;

public class TestData 
{
	private TestData() {};
	 
	public static final List<ReflectionObject<SimpleObject>> soList = Arrays.asList(
			new ReflectionObject<>(new SimpleObject("1", 4)),
			new ReflectionObject<>(new SimpleObject("2", 1)),
			new ReflectionObject<>(new SimpleObject("3", 2)),
			new ReflectionObject<>(new SimpleObject("4", 4)),
			new ReflectionObject<>(new SimpleObject("5", 1)),	
			new ReflectionObject<>(new SimpleObject("6", 7)),	
			new ReflectionObject<>(new SimpleObject("7", 5)),	
			new ReflectionObject<>(new SimpleObject("8", 9)),
			new ReflectionObject<>(new SimpleObject("9", 4)));
	
	public static final List<ReflectionObject<NestedObject>> noList = Arrays.asList(
			new ReflectionObject<>(new NestedObject("no1", 4, soList.get(8).getObject())),
			new ReflectionObject<>(new NestedObject("no2", 3, soList.get(7).getObject())),
			new ReflectionObject<>(new NestedObject("no3", 3, soList.get(6).getObject())),
			new ReflectionObject<>(new NestedObject("no4", 1, soList.get(5).getObject())),
			new ReflectionObject<>(new NestedObject("no5", 4, soList.get(4).getObject())),
			new ReflectionObject<>(new NestedObject("no6", 6, soList.get(3).getObject())));
	
	public static final List<ReflectionObject<CollectionsObject>> coList = Arrays.asList(
			new ReflectionObject<>(new CollectionsObject("co1", 1, 3)), 
			new ReflectionObject<>(new CollectionsObject("co2", 7, 1)), 
			new ReflectionObject<>(new CollectionsObject("co3", 2, 5)));
	
	public static final SimpleCondition<SimpleObject, Integer> sointLte4 = new SimpleCondition<>("intVal", Negate.YES, Compare.GREATER_THAN, 4);
	public static final SimpleCondition<NestedObject, Integer> nointLte4 = new SimpleCondition<>("intVal", Negate.YES, Compare.GREATER_THAN, 4);
	public static final SimpleCondition<NestedObject, Integer> nosoIntLte4 = new SimpleCondition<>("so.intVal", Negate.YES, Compare.GREATER_THAN, 4);
	public static final SimpleCondition<SimpleObject, Integer> intGt1 = new SimpleCondition<>("intVal", Compare.GREATER_THAN, 1);
	public static final SimpleCondition<SimpleObject, String> nameIs8 = new SimpleCondition<>("name", Compare.EQUAL, "8");
	
	public static List<ReflectionObject<SimpleObject>> getCopyOfSoList()
	{
		List<ReflectionObject<SimpleObject>> copy = new ArrayList<>(soList.size());
		for (ReflectionObject<SimpleObject> ro : soList) 
		{
			copy.add(new ReflectionObject<SimpleObject>(new SimpleObject(ro.getObject())));
		}
		return copy;
	}
}
