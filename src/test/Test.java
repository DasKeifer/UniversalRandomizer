package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import condition.Condition;
import condition.ConditionOperatorPair;
import condition.LogicOperator;
import condition.Comparator;
import condition.CompoundCondition;
import condition.SimpleCondition;
import universal_randomizer.ReflectionObject;
import universal_randomizer.select.SelectAll;
import universal_randomizer.select.SelectEach;

public class Test {
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
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
		SimpleCondition intLte4 = new SimpleCondition();
		intLte4.variable = "intVal";
		intLte4.negate = true;
		intLte4.comparator = Comparator.GREATER_THAN;
		intLte4.val = 4;
		executeAndPrintCondition(testList, intLte4);

		SimpleCondition intGt1 = new SimpleCondition();
		intGt1.variable = "intVal";
		intGt1.negate = false;
		intGt1.comparator = Comparator.GREATER_THAN;
		intGt1.val = 1;
		executeAndPrintCondition(testList, intGt1);

		SimpleCondition nameIs8 = new SimpleCondition();
		nameIs8.variable = "name";
		nameIs8.negate = false;
		nameIs8.comparator = Comparator.EQUAL;
		nameIs8.val = "8";
		executeAndPrintCondition(testList, nameIs8);

		
		SelectAll sa1 = new SelectAll(intLte4);
		sa1.nextAction = Test::printSimpleObjectList;
		sa1.perform(testList);
		
		SelectEach se1 = new SelectEach(intLte4);
		se1.nextAction = Test::printSimpleObjectList;
		se1.perform(testList);
		
		SelectAll sa2 = new SelectAll(intGt1);
		sa2.nextAction = sa1::perform;
		sa2.perform(testList);

		// ------------- Compound Condition testing -------------------
		List<ConditionOperatorPair> cc1Conds = new LinkedList<>();
		cc1Conds.add(new ConditionOperatorPair(LogicOperator.START, nameIs8));
		cc1Conds.add(new ConditionOperatorPair(LogicOperator.OR, intLte4));
		CompoundCondition cc1 = new CompoundCondition(cc1Conds);

		SelectAll saCc1 = new SelectAll(cc1);
		saCc1.nextAction = Test::printSimpleObjectList;
		saCc1.perform(testList);

		List<ConditionOperatorPair> cc2Conds = new LinkedList<>();
		cc2Conds.add(new ConditionOperatorPair(LogicOperator.START, cc1));
		cc2Conds.add(new ConditionOperatorPair(LogicOperator.AND, intGt1));
		CompoundCondition cc2 = new CompoundCondition(cc2Conds);

		SelectAll saCc2 = new SelectAll(cc2);
		saCc2.nextAction = Test::printSimpleObjectList;
		saCc2.perform(testList);
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
	
	static boolean printSimpleObjectList(Collection<ReflectionObject> list)
	{
		System.out.println("printSimpleObjectList:");
		for (ReflectionObject obj : list)
		{
			SimpleObject so = (SimpleObject) obj.getObject();
			System.out.println(so.name + "," + so.intVal);
		}
		return true;
	}
}
