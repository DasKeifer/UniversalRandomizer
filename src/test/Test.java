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
import universal_randomizer.ReflectionObject;
import universal_randomizer.Select;

public class Test {
	
	public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException
	{
		List<ReflectionObject> testList = new ArrayList<>();
		testList.add(new ReflectionObject(new SimpleObject("1", 4)));
		testList.add(new ReflectionObject(new SimpleObject("2", 1)));
		testList.add(new ReflectionObject(new SimpleObject("3", 2)));
		testList.add(new ReflectionObject(new SimpleObject("4", 4)));
		testList.add(new ReflectionObject(new SimpleObject("5", 1)));	
		testList.add(new ReflectionObject(new SimpleObject("6", 7)));	
		testList.add(new ReflectionObject(new SimpleObject("7", 5)));	
		testList.add(new ReflectionObject(new SimpleObject("8", 9)));
		testList.add(new ReflectionObject(new SimpleObject("9", 4)));			

		// ------------- Simple Condition testing -------------------
		SimpleCondition intLte4 = new SimpleCondition("intVal", Negate.YES, Compare.GREATER_THAN, 4);
		executeAndPrintCondition(testList, intLte4);

		SimpleCondition intGt1 = new SimpleCondition("intVal", Compare.GREATER_THAN, 1);
		executeAndPrintCondition(testList, intGt1);

		SimpleCondition nameIs8 = new SimpleCondition("name", Compare.EQUAL, "8");
		executeAndPrintCondition(testList, nameIs8);

		
		Select select1 = new Select(intLte4, Test::printSimpleObjectList);
		select1.perform(testList.stream());
		
		Select select2 = new Select(intGt1, select1::perform);
		select2.perform(testList.stream());

		// ------------------- Group testing --------------------------
		Select sg1 = new Select(intLte4, 
						new Select(intGt1, 
								new Group("intVal", Test::printSimpleObjectList)));
		sg1.perform(testList.stream());
		
		Select sg2 = new Select(intLte4, 
				new Select(intGt1, 
						new Group("intVal", 
								new Group("name", Test::printSimpleObjectList))));
		sg2.perform(testList.stream());

		// ------------- Compound Condition testing -------------------
		CompoundCondition cc1 = new CompoundCondition(
				nameIs8,
				new LogicConditionPair(Logic.OR, intLte4));

		Select saCc1 = new Select(cc1, Test::printSimpleObjectList);
		saCc1.perform(testList.stream());

		CompoundCondition cc2 = new CompoundCondition(
				cc1,
				new LogicConditionPair(Logic.AND, Negate.YES, new SimpleCondition("intVal", Compare.GREATER_THAN, 1)));

		Select saCc2 = new Select(cc2, Test::printSimpleObjectList);
		saCc2.perform(testList.stream());
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
}
