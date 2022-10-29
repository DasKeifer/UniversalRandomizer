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
import universal_randomizer.Select;

public class Test {
	
	public static void main(String[] args)
	{
		List<SimpleObject> soList = new ArrayList<>();
		soList.add(new SimpleObject("1", 4));
		soList.add(new SimpleObject("2", 1));
		soList.add(new SimpleObject("3", 2));
		soList.add(new SimpleObject("4", 4));
		soList.add(new SimpleObject("5", 1));	
		soList.add(new SimpleObject("6", 7));	
		soList.add(new SimpleObject("7", 5));	
		soList.add(new SimpleObject("8", 9));
		soList.add(new SimpleObject("9", 4));		
		
		List<NestedObject> noList = new ArrayList<>();
		noList.add(new NestedObject("no1", 4, soList.get(8)));
		noList.add(new NestedObject("no2", 3, soList.get(7)));
		noList.add(new NestedObject("no3", 3, soList.get(6)));
		noList.add(new NestedObject("no4", 1, soList.get(5)));
		noList.add(new NestedObject("no5", 4, soList.get(4)));
		noList.add(new NestedObject("no6", 6, soList.get(3)));

		// ------------- Simple Condition testing -------------------
		SimpleCondition intLte4 = new SimpleCondition("intVal", Negate.YES, Compare.GREATER_THAN, 4);
		executeAndPrintCondition(soList, intLte4);
		
		SimpleCondition soIntLte4 = new SimpleCondition("so.intVal", Negate.YES, Compare.GREATER_THAN, 4);
		executeAndPrintCondition(soList, intLte4);

		SimpleCondition intGt1 = new SimpleCondition("intVal", Compare.GREATER_THAN, 1);
		executeAndPrintCondition(soList, intGt1);

		SimpleCondition nameIs8 = new SimpleCondition("name", Compare.EQUAL, "8");
		executeAndPrintCondition(soList, nameIs8);

		
		Select<SimpleObject> select1 = new Select<>(intLte4, Test::printSimpleObjectList);
		select1.perform(soList.stream());
		
		Select<SimpleObject> select2 = new Select<>(intGt1, select1::perform);
		select2.perform(soList.stream());

		// ------------------- Group testing --------------------------
		Select<SimpleObject> sg1 = new Select<>(intLte4, 
						new Select<SimpleObject>(intGt1, 
								new Group<SimpleObject>("intVal", Test::printSimpleObjectList)));
		sg1.perform(soList.stream());
		
		Select<SimpleObject> sg2 = new Select<>(intLte4, 
				new Select<SimpleObject>(intGt1, 
						new Group<SimpleObject>("intVal", 
								new Group<SimpleObject>("name", Test::printSimpleObjectList))));
		sg2.perform(soList.stream());

		// ------------- Compound Condition testing -------------------
		CompoundCondition cc1 = new CompoundCondition(
				nameIs8,
				new LogicConditionPair(Logic.OR, intLte4));

		Select<SimpleObject> saCc1 = new Select<>(cc1, Test::printSimpleObjectList);
		saCc1.perform(soList.stream());

		CompoundCondition cc2 = new CompoundCondition(
				cc1,
				new LogicConditionPair(Logic.AND, Negate.YES, new SimpleCondition("intVal", Compare.GREATER_THAN, 1)));

		Select<SimpleObject> saCc2 = new Select<>(cc2, Test::printSimpleObjectList);
		saCc2.perform(soList.stream());
		
		// ------------------- Nested Object Selects --------------------
		Select<NestedObject> nos1 = new Select<>(intLte4,
				new Select<NestedObject>(soIntLte4, Test::printNestedObjectList));
		nos1.perform(noList.stream());
	}
	
	static void executeAndPrintCondition(List<SimpleObject> list, Condition cond)
	{
		System.out.println("executeAndPrintCondition:");
		for (SimpleObject so : list)
		{
			System.out.println(cond.evaluate(so) + " - " + so.name + "," + so.intVal);
		}
	}
	
	static boolean printSimpleObjectList(Stream<SimpleObject> stream)
	{
		System.out.println("printSimpleObjectList:");
		stream.forEach(so -> {
			System.out.println(so.name + "," + so.intVal);
		});
		return true;
	}
	
	static boolean printNestedObjectList(Stream<NestedObject> stream)
	{
		System.out.println("printNestedObjectList:");
		stream.forEach(no -> {
			System.out.println(no.name + "," + no.intVal + " - SO " + no.so.name + "," + no.so.intVal);
		});
		return true;
	}
}
