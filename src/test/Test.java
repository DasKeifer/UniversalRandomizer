package test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import condition.Condition;
import condition.LogicConditionPair;
import condition.Logic;
import condition.Negate;
import condition.Compare;
import condition.CompoundCondition;
import condition.SimpleCondition;
import universal_randomizer.Group;
import universal_randomizer.ReflectionComparator;
import universal_randomizer.ReflectionObject;
import universal_randomizer.Select;
import universal_randomizer.Sort;

public class Test {
	
	public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException
	{
		List<ReflectionObject> soList = new ArrayList<>();
		soList.add(new ReflectionObject(new SimpleObject("1", 4)));
		soList.add(new ReflectionObject(new SimpleObject("2", 1)));
		soList.add(new ReflectionObject(new SimpleObject("3", 2)));
		soList.add(new ReflectionObject(new SimpleObject("4", 4)));
		soList.add(new ReflectionObject(new SimpleObject("5", 1)));	
		soList.add(new ReflectionObject(new SimpleObject("6", 7)));	
		soList.add(new ReflectionObject(new SimpleObject("7", 5)));	
		soList.add(new ReflectionObject(new SimpleObject("8", 9)));
		soList.add(new ReflectionObject(new SimpleObject("9", 4)));
		
        List<ReflectionObject> noList = new ArrayList<>();
        noList.add(new ReflectionObject(new NestedObject("no1", 4, (SimpleObject) soList.get(8).getObject())));
        noList.add(new ReflectionObject(new NestedObject("no2", 3, (SimpleObject) soList.get(7).getObject())));
        noList.add(new ReflectionObject(new NestedObject("no3", 3, (SimpleObject) soList.get(6).getObject())));
        noList.add(new ReflectionObject(new NestedObject("no4", 1, (SimpleObject) soList.get(5).getObject())));
        noList.add(new ReflectionObject(new NestedObject("no5", 4, (SimpleObject) soList.get(4).getObject())));
        noList.add(new ReflectionObject(new NestedObject("no6", 6, (SimpleObject) soList.get(3).getObject())));


		// ------------- Simple Condition testing -------------------
		SimpleCondition intLte4 = new SimpleCondition("intVal", Negate.YES, Compare.GREATER_THAN, 4);
		executeAndPrintCondition(soList, intLte4);
		
        SimpleCondition soIntLte4 = new SimpleCondition("so.intVal", Negate.YES, Compare.GREATER_THAN, 4);
        //executeAndPrintCondition(noList, soIntLte4);

		SimpleCondition intGt1 = new SimpleCondition("intVal", Compare.GREATER_THAN, 1);
		executeAndPrintCondition(soList, intGt1);

		SimpleCondition nameIs8 = new SimpleCondition("name", Compare.EQUAL, "8");
		executeAndPrintCondition(soList, nameIs8);

		
		Select select1 = new Select(intLte4, Test::printSimpleObjectList);
		select1.perform(soList.stream());
		
		Select select2 = new Select(intGt1, select1::perform);
		select2.perform(soList.stream());

		// ------------------- Group testing --------------------------
		Select sg1 = new Select(intLte4, 
						new Select(intGt1, 
								new Group("intVal", Test::printSimpleObjectList)));
		sg1.perform(soList.stream());
		
		Select sg2 = new Select(intLte4, 
				new Select(intGt1, 
						new Group("intVal", 
								new Group("name", Test::printSimpleObjectList))));
		sg2.perform(soList.stream());

		// ------------- Compound Condition testing -------------------
		CompoundCondition cc1 = new CompoundCondition(
				nameIs8,
				new LogicConditionPair(Logic.OR, intLte4));

		Select saCc1 = new Select(cc1, Test::printSimpleObjectList);
		saCc1.perform(soList.stream());

		CompoundCondition cc2 = new CompoundCondition(
				cc1,
				new LogicConditionPair(Logic.AND, Negate.YES, new SimpleCondition("intVal", Compare.GREATER_THAN, 1)));

		Select saCc2 = new Select(cc2, Test::printSimpleObjectList);
		saCc2.perform(soList.stream());
		
        // ------------------- Nested Object Selects --------------------
        Select nos1 = new Select(intLte4,
                new Select(soIntLte4, Test::printNestedObjectList));
        nos1.perform(noList.stream());
        
		// --------------------- Sort testing ---------------------------
		Sort<SimpleObject> sort1 = new Sort<>(SimpleObject::reverseSort, Test::printSimpleObjectList);
		sort1.perform(soList.stream());
	}
	
	static void executeAndPrintCondition(List<ReflectionObject> list, Condition cond)
	{
		System.out.println("executeAndPrintCondition:");
		for (ReflectionObject obj : list)
		{
			SimpleObject so = (SimpleObject) obj.getObject();
			System.out.println(cond.evaluate(obj) + " - " + so.name + "," + so.intVal);
		}
	}
	
	static boolean printSimpleObjectList(Stream<ReflectionObject> stream)
	{
		System.out.println("printSimpleObjectList:");
		stream.forEach(obj -> {
			SimpleObject so = (SimpleObject) obj.getObject();
			System.out.println(so.name + "," + so.intVal);
		});
		return true;
	}
	
	static boolean printNestedObjectList(Stream<ReflectionObject> stream)
	{
		System.out.println("printNestedObjectList:");
		stream.forEach(obj -> {
			NestedObject no = (NestedObject) obj.getObject();
            System.out.println(no.name + "," + no.intVal + " - SO " + no.so.name + "," + no.so.intVal);
		});
		return true;
	}
}
