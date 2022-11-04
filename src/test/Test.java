package test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import condition.Condition;
import condition.LogicConditionPair;
import condition.MethodCondition;
import condition.Logic;
import condition.Negate;
import condition.Compare;
import condition.CompoundCondition;
import condition.SimpleCondition;
import universal_randomizer.Group;
import universal_randomizer.Randomize;
import universal_randomizer.Select;
import universal_randomizer.Shuffle;
import universal_randomizer.Sort;
import universal_randomizer.pool.Pool;
import universal_randomizer.wrappers.ReflectionObject;

public class Test {
	
	public static void main(String[] args)
	{
		List<ReflectionObject<SimpleObject>> soList = new ArrayList<>();
		soList.add(new ReflectionObject<>(new SimpleObject("1", 4)));
		soList.add(new ReflectionObject<>(new SimpleObject("2", 1)));
		soList.add(new ReflectionObject<>(new SimpleObject("3", 2)));
		soList.add(new ReflectionObject<>(new SimpleObject("4", 4)));
		soList.add(new ReflectionObject<>(new SimpleObject("5", 1)));	
		soList.add(new ReflectionObject<>(new SimpleObject("6", 7)));	
		soList.add(new ReflectionObject<>(new SimpleObject("7", 5)));	
		soList.add(new ReflectionObject<>(new SimpleObject("8", 9)));
		soList.add(new ReflectionObject<>(new SimpleObject("9", 4)));		
		
		List<ReflectionObject<NestedObject>> noList = new ArrayList<>();
		noList.add(new ReflectionObject<>(new NestedObject("no1", 4, soList.get(8).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no2", 3, soList.get(7).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no3", 3, soList.get(6).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no4", 1, soList.get(5).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no5", 4, soList.get(4).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no6", 6, soList.get(3).getObject())));

		// ------------- Simple Condition testing -------------------
		SimpleCondition<SimpleObject, Integer> sointLte4 = new SimpleCondition<>("intVal", Negate.YES, Compare.GREATER_THAN, 4);
		SimpleCondition<NestedObject, Integer> nointLte4 = new SimpleCondition<>("intVal", Negate.YES, Compare.GREATER_THAN, 4);
//		executeAndPrintCondition(soList, intLte4);
		
		SimpleCondition<NestedObject, Integer> nosoIntLte4 = new SimpleCondition<>("so.intVal", Negate.YES, Compare.GREATER_THAN, 4);
//		executeAndPrintCondition(soList, intLte4);

		SimpleCondition<SimpleObject, Integer> intGt1 = new SimpleCondition<>("intVal", Compare.GREATER_THAN, 1);
//		executeAndPrintCondition(soList, intGt1);

		SimpleCondition<SimpleObject, String> nameIs8 = new SimpleCondition<>("name", Compare.EQUAL, "8");
//		executeAndPrintCondition(soList, nameIs8);

		System.out.println("----------- Select/Simple Condition -------------------");
		
		Select<SimpleObject> select1 = new Select<>(sointLte4, Test::unwrappedPrintSimpleObject);
		select1.perform(soList.stream());
		
		Select<SimpleObject> select2 = new Select<>(intGt1, select1::perform);
		select2.perform(soList.stream());

		// ------------------- Method Condition testing --------------------------
		System.out.println("----------- Method Condition -------------------");
		MethodCondition<SimpleObject> mc1 = new MethodCondition<>("valBetweenn2And5Excl");
		Select<SimpleObject> smc1 = new Select<>(mc1, Test::unwrappedPrintSimpleObject);
		smc1.perform(soList.stream());
		

		// ------------------- Group testing --------------------------
		System.out.println("----------- Group -------------------");
		Select<SimpleObject> sg1 = new Select<>(sointLte4, 
						new Select<>(intGt1, 
								new Group<>("intVal", Test::printSimpleObjectList)));
		sg1.perform(soList.stream());
		
		Select<SimpleObject> sg2 = new Select<>(sointLte4, 
				new Select<>(intGt1, 
						new Group<>("intVal", 
								new Group<>("name", Test::printSimpleObjectList))));
		sg2.perform(soList.stream());

		// ------------- Compound Condition testing -------------------
		System.out.println("----------- Compount Condition -------------------");
		CompoundCondition<SimpleObject> cc1 = new CompoundCondition<>(
				nameIs8,
				new LogicConditionPair<>(Logic.OR, sointLte4));

		Select<SimpleObject> saCc1 = new Select<>(cc1, Test::printSimpleObjectList);
		saCc1.perform(soList.stream());

		CompoundCondition<SimpleObject> cc2 = new CompoundCondition<>(
				cc1,
				new LogicConditionPair<>(Logic.AND, Negate.YES, new SimpleCondition<>("intVal", Compare.GREATER_THAN, 1)));

		Select<SimpleObject> saCc2 = new Select<>(cc2, Test::printSimpleObjectList);
		saCc2.perform(soList.stream());
		
		// ------------------- Nested Object Selects --------------------
		System.out.println("----------- Nested Selects -------------------");
		Select<NestedObject> nos1 = new Select<>(nointLte4,
				new Select<>(nosoIntLte4, Test::printNestedObjectList));
		nos1.perform(noList.stream());
        
        // --------------------- Sort testing ---------------------------
		System.out.println("----------- Sort -------------------");
        Sort<SimpleObject> sort1 = Sort.createComparator(SimpleObject::reverseSort, Test::printSimpleObjectList);
        sort1.perform(soList.stream());
        
        Sort<SimpleObject> sort2 = Sort.createComparable(Test::printSimpleObjectList);
        sort2.perform(soList.stream());
        

        // --------------------- Shuffle testing ---------------------------
		System.out.println("----------- Shuffle -------------------");
        Shuffle<SimpleObject> shuffle2 = Shuffle.createSeeded(Test::printSimpleObjectList, 1);
        shuffle2.perform(soList.stream());

        // --------------------- Randomize testing ---------------------------
		System.out.println("----------- Randomize -------------------");
		Pool<Integer> rp1 = Pool.createRange(21, 31, 2);
		Randomize<SimpleObject> r1 = Randomize.createSeeded("intVal", rp1, 1);
		r1.perform(soList.stream());
		printSimpleObjectList(soList.stream());
	}
	
	static void executeAndPrintCondition(List<ReflectionObject<SimpleObject>> list, Condition<SimpleObject> cond)
	{
		System.out.println("executeAndPrintCondition:");
		for (ReflectionObject<SimpleObject> obj : list)
		{
			System.out.println(cond.evaluate(obj) + " - " + obj.getObject().name + "," + obj.getObject().intVal);
		}
	}

	static boolean unwrappedPrintSimpleObject(SimpleObject obj)
	{
		System.out.println(obj.name + "," + obj.intVal);
		return true;
	}
	
	static boolean printSimpleObjectList(Stream<ReflectionObject<SimpleObject>> stream)
	{
		System.out.println("printSimpleObjectList:");
		stream.forEach(obj -> {
			System.out.println(obj.getObject().name + "," + obj.getObject().intVal);
		});
		return true;
	}
	
	static boolean printNestedObjectList(Stream<ReflectionObject<NestedObject>> stream)
	{
		System.out.println("printNestedObjectList:");
		stream.forEach(obj -> {
			System.out.println(obj.getObject().name + "," + obj.getObject().intVal + " - SO " + obj.getObject().so.name + "," + obj.getObject().so.intVal);
		});
		return true;
	}
}
