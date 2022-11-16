package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
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
import universal_randomizer.Pool;
import universal_randomizer.Select;
import universal_randomizer.Shuffle;
import universal_randomizer.Sort;
import universal_randomizer.action.ReflObjStreamAction;
import universal_randomizer.randomize.Randomize;
import universal_randomizer.wrappers.ReflectionObject;

public class Test {
	
	static List<ReflectionObject<SimpleObject>> soList = new ArrayList<>();
	static List<ReflectionObject<NestedObject>> noList = new ArrayList<>();
	static List<ReflectionObject<CollectionsObject>> coList = new ArrayList<>();
	
	static SimpleCondition<SimpleObject, Integer> sointLte4 = new SimpleCondition<>("intVal", Negate.YES, Compare.GREATER_THAN, 4);
	static SimpleCondition<NestedObject, Integer> nointLte4 = new SimpleCondition<>("intVal", Negate.YES, Compare.GREATER_THAN, 4);
	static SimpleCondition<NestedObject, Integer> nosoIntLte4 = new SimpleCondition<>("so.intVal", Negate.YES, Compare.GREATER_THAN, 4);
	static SimpleCondition<SimpleObject, Integer> intGt1 = new SimpleCondition<>("intVal", Compare.GREATER_THAN, 1);
	static SimpleCondition<SimpleObject, String> nameIs8 = new SimpleCondition<>("name", Compare.EQUAL, "8");
	
	public static void main(String[] args)
	{		
		setupLists();
		
		reflectionObjectTests();
		simpleConditionTests();
		methodConditionTests();
		compoundConditionTests();
		
		groupTests();
		sortTests();
        shuffleTests();
        poolTests();
        randomizeTests();
	}

	static void setupLists()
	{
		soList.add(new ReflectionObject<>(new SimpleObject("1", 4)));
		soList.add(new ReflectionObject<>(new SimpleObject("2", 1)));
		soList.add(new ReflectionObject<>(new SimpleObject("3", 2)));
		soList.add(new ReflectionObject<>(new SimpleObject("4", 4)));
		soList.add(new ReflectionObject<>(new SimpleObject("5", 1)));	
		soList.add(new ReflectionObject<>(new SimpleObject("6", 7)));	
		soList.add(new ReflectionObject<>(new SimpleObject("7", 5)));	
		soList.add(new ReflectionObject<>(new SimpleObject("8", 9)));
		soList.add(new ReflectionObject<>(new SimpleObject("9", 4)));		
		
		noList.add(new ReflectionObject<>(new NestedObject("no1", 4, soList.get(8).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no2", 3, soList.get(7).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no3", 3, soList.get(6).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no4", 1, soList.get(5).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no5", 4, soList.get(4).getObject())));
		noList.add(new ReflectionObject<>(new NestedObject("no6", 6, soList.get(3).getObject())));
		
		CollectionsObject co1 = new CollectionsObject("1", 1);
		co1.doubleWrapperArray = new Double[] { 1.0, 2.0, 3.0 };
		co1.charRawArray = new char[] {'z','y','x'};
		co1.charCollection.add('A');
		co1.charCollection.add('B');
		co1.charCollection.add('C');
		co1.charCollection.add('D');
		co1.floatMap.put(1, 1.2345f);
		co1.floatMap.put(5, 2.1111f);
		co1.floatMap.put(10, 0.54f);
		coList.add(new ReflectionObject<CollectionsObject>(co1));

		CollectionsObject co2 = new CollectionsObject("2", 2);
		co2.doubleWrapperArray = new Double[] { 4.0, 5.0, 6.0, 7.0 };
		co2.charRawArray = new char[] {'w','v'};
		co2.charCollection.add('E');
		co2.charCollection.add('F');
		co2.floatMap.put(3, 3.333f);
		co2.floatMap.put(4, -4.04f);
		co2.floatMap.put(-5, 0.01f);
		coList.add(new ReflectionObject<CollectionsObject>(co2));
	}
	
	static void reflectionObjectTests()
	{
		System.out.println("----------- Reflection Object -------------------");

		getAndPrintField(coList.get(0), "intVal");
		getAndPrintField(coList.get(0), "charRawArray");
		getAndPrintField(coList.get(0), "doubleWrapperArray");
		getAndPrintField(coList.get(0), "charCollection");
		getAndPrintField(coList.get(0), "floatMap");
		getAndPrintMapField(coList.get(0), "floatMap", true);
		getAndPrintMapField(coList.get(0), "floatMap", false);
	}

	static void simpleConditionTests()
	{
		System.out.println("----------- Simple Condition -------------------");
		executeAndPrintCondition(soList, sointLte4);
		executeAndPrintCondition(soList, intGt1);
		executeAndPrintCondition(soList, nameIs8);
	}
	
	static void methodConditionTests()
	{
		System.out.println("----------- Method Condition -------------------");
		MethodCondition<SimpleObject> mc1 = new MethodCondition<>("valBetween2And5Excl");
		Select<SimpleObject> smc1 = new Select<>(mc1, ReflObjStreamAction.create(Test::printSimpleObjectList));
		smc1.perform(soList.stream());
	}
	
	static void compoundConditionTests()
	{
		System.out.println("----------- Compound Condition -------------------");
		CompoundCondition<SimpleObject> cc1 = new CompoundCondition<>(
				nameIs8,
				new LogicConditionPair<>(Logic.OR, sointLte4));

		Select<SimpleObject> saCc1 = new Select<>(cc1, Test::printWrappedSimpleObjectList);
		saCc1.perform(soList.stream());

		CompoundCondition<SimpleObject> cc2 = new CompoundCondition<>(
				cc1,
				new LogicConditionPair<>(Logic.AND, Negate.YES, new SimpleCondition<>("intVal", Compare.GREATER_THAN, 1)));

		Select<SimpleObject> saCc2 = new Select<>(cc2, Test::printWrappedSimpleObjectList);
		saCc2.perform(soList.stream());
	}
	
	static void selectTests()
	{
		System.out.println("----------- Selects -------------------");
		
		Select<SimpleObject> select1 = new Select<>(sointLte4, ReflObjStreamAction.create(Test::printSimpleObjectList));
		select1.perform(soList.stream());
		
		Select<SimpleObject> select2 = new Select<>(intGt1, select1::perform);
		select2.perform(soList.stream());
		
		System.out.println("----------- Nested Selects -------------------");
		Select<NestedObject> nos1 = new Select<>(nointLte4,
				new Select<>(nosoIntLte4, Test::printNestedObjectList));
		nos1.perform(noList.stream());
	}
	
	static void groupTests()
	{
		System.out.println("----------- Group -------------------");
		Select<SimpleObject> sg1 = new Select<>(sointLte4, 
						new Select<>(intGt1, 
								new Group<>("intVal", Test::printWrappedSimpleObjectList)));
		sg1.perform(soList.stream());
		
		Select<SimpleObject> sg2 = new Select<>(sointLte4, 
				new Select<>(intGt1, 
						new Group<>("intVal", 
								new Group<>("name", Test::printWrappedSimpleObjectList))));
		sg2.perform(soList.stream());
	}
	
	static void sortTests()
	{
		System.out.println("----------- Sort -------------------");
        Sort<SimpleObject> sort1 = Sort.createComparator(SimpleObject::reverseSort, Test::printWrappedSimpleObjectList);
        sort1.perform(soList.stream());
        
        Sort<SimpleObject> sort2 = Sort.createComparable(Test::printWrappedSimpleObjectList);
        sort2.perform(soList.stream());
	}
	
	static void shuffleTests()
	{
		System.out.println("----------- Shuffle -------------------");
        Shuffle<SimpleObject> shuffle2 = Shuffle.createSeeded(Test::printWrappedSimpleObjectList, 1);
        shuffle2.perform(soList.stream());
	}

	static void poolTests()
	{
		poolCreationTests();
		poolGetPopTests();
	}
	
	static void poolCreationTests()
	{
		System.out.println("----------- Pool -------------------");		
		Pool<Integer> intArrayPool = Pool.createFromArray(1, -4, 5, 99);
		printPool(intArrayPool);
		
		Pool<Float> floatPool = Pool.createRange(-3.14f, 1.88f, 0.7152f, Float::sum);
		printPool(floatPool);

		SimpleObject so1 = new SimpleObject("1", 2);
		SimpleObject so2 = new SimpleObject("2", 12);
		SimpleObject soStep = new SimpleObject("3", 2);
		Pool<SimpleObject> soPool = Pool.createRange(so1, so2, soStep, Test::sumSO);
		printSoPool(soPool);
		
		Pool<Integer> fromSo = Pool.createFromStream("intVal", soList.stream());
		printPool(fromSo);

		// From Stream with complex types
		Pool<Integer> fromCoIv = Pool.createFromStream("intVal", soList.stream());
		printPool(fromCoIv);
		Pool<Integer> fromCoDwa = Pool.createFromStream("doubleWrapperArray", coList.stream());
		printPool(fromCoDwa);
		Pool<Integer> fromCoCra = Pool.createFromStream("charRawArray", coList.stream());
		printPool(fromCoCra);
		Pool<Integer> fromCoCc = Pool.createFromStream("charCollection", coList.stream());
		printPool(fromCoCc);
		Pool<Integer> fromCoFm = Pool.createFromStream("floatMap", coList.stream());
		printPool(fromCoFm);
		Pool<Integer> fromCoFmv = Pool.createFromMapValuesStream("floatMap", coList.stream());
		printPool(fromCoFmv);
		Pool<Integer> fromCoFmk = Pool.createFromMapKeysStream("floatMap", coList.stream());
		printPool(fromCoFmk);
	}
	
	static void poolGetPopTests()
	{		
		Pool<Integer> intPool = Pool.createRange(1, 20, 3, Integer::sum);
		
		Random rand = new Random(1);
		int size = intPool.size();
		System.out.println("rand " + intPool.popRandom(rand));
		if (intPool.size() != size - 1)
		{
			System.err.println("Pop didn't remove - pre " + size + " post " + intPool.size());
		}
		size = intPool.size();
		
		System.out.println("get rand " + intPool.getRandom(rand));
		if (intPool.size() != size)
		{
			System.err.println("get removed - pre " + size + " post " + intPool.size());
		}
		
		// Test getting random indexes excluding some
		SortedSet<Integer> exclIndexes = new TreeSet<>();
		for(int i = 0; i < 100; i++)
		{
			exclIndexes.clear();
			for (int j = 0; j <= intPool.size(); j++)
			{
				int randIndex = intPool.getRandomIndex(rand, exclIndexes);
				if (exclIndexes.contains(randIndex))
				{
					System.err.println("getRandomIndex returned value in exluded list - index: " + randIndex + " excluded: " + exclIndexes);
				}
				if (j == intPool.size() && randIndex != -1)
				{
					System.err.println("getRandomIndex returned non-invalid index: " + randIndex + " when all index are excluded: " + exclIndexes);
				}
				exclIndexes.add(randIndex);
			}
		}
	}

	static void randomizeTests()
	{
		System.out.println("----------- Randomize -------------------");
		Pool<Integer> rp1 = Pool.createRange(1, 20, 2, Integer::sum);
		Randomize<SimpleObject, Integer> r1 = Randomize.createSeededWithPool("intVal", rp1, 1);
		r1.perform(soList.stream());
		printWrappedSimpleObjectList(soList.stream());
	}
	
	static SimpleObject sumSO(SimpleObject lhs, SimpleObject rhs)
	{
		return new SimpleObject(lhs.name, lhs.intVal + rhs.intVal);
	}
	
	static void getAndPrintMapField(ReflectionObject<?> obj, String fieldName, boolean values)
	{
		System.out.print(fieldName + (values ? " values" : " keys") + ": ");
		printStream(obj.getMapFieldStream(fieldName, values));
		System.out.println();
	}
	
	static void getAndPrintField(ReflectionObject<?> obj, String fieldName)
	{
		System.out.print(fieldName + ": ");
		printStream(obj.getFieldStream(fieldName));
		System.out.println();
	}
	
	static <T> void printStream(Stream<T> stream)
	{
		stream.forEach(obj -> {
			System.out.print(obj + ", ");
		});
	}
	
	static void executeAndPrintCondition(List<ReflectionObject<SimpleObject>> list, Condition<SimpleObject> cond)
	{
		System.out.print("executeAndPrintCondition:");
		for (ReflectionObject<SimpleObject> obj : list)
		{
			System.out.print("{" + cond.evaluate(obj) + " - " + obj.getObject().name + "," + obj.getObject().intVal + "}, ");
		}
		System.out.println();
	}

	static boolean printSimpleObjectList(Stream<SimpleObject> stream)
	{
		System.out.println("printSimpleObjectList:");
		stream.forEach(obj -> {
			System.out.print("{" + obj.name + "," + obj.intVal + "}, ");
		});
		System.out.println();
		return true;
	}
	
	static boolean printWrappedSimpleObjectList(Stream<ReflectionObject<SimpleObject>> stream)
	{
		System.out.print("printWrappedSimpleObjectList:");
		stream.forEach(obj -> {
			System.out.print("{" + obj.getObject().name + "," + obj.getObject().intVal + "}, ");
		});
		System.out.println();
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

	static void printPool(Pool<?> pool)
	{
		System.out.print("Pool: ");
		for (int i = 0; i < pool.size(); i++)
		{
			System.out.print(pool.get(i) + ", ");
		}
		System.out.println();
	}
	
	static void printSoPool(Pool<SimpleObject> pool)
	{
		System.out.print("SO Pool: ");
		for (int i = 0; i < pool.size(); i++)
		{
			System.out.print(pool.get(i).intVal + ", ");
		}
		System.out.println();
	}
}
