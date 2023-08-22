package universal_randomizer.condition;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import Support.SimpleObject;
import universal_randomizer.wrappers.ReflectionObject;

class CompoundConditionTests 
{	
	@SuppressWarnings("serial")
	final Map<String, ReflectionObject<SimpleObject>> OBJ_LIST = Collections.unmodifiableMap(new LinkedHashMap<String, ReflectionObject<SimpleObject>>() {
	    {
	        put("1 5", new ReflectionObject<>(new SimpleObject("1", 5)));
	        put("1 7", new ReflectionObject<>(new SimpleObject("1", 7)));
	        put("2 5", new ReflectionObject<>(new SimpleObject("2", 5)));
	        put("2 7", new ReflectionObject<>(new SimpleObject("2", 7)));
	    }
	});

	@SuppressWarnings("serial")
	final Map<String, Condition<SimpleObject>> SIMPLE_CONDS = Collections.unmodifiableMap(new LinkedHashMap<String, Condition<SimpleObject>>() {
	    {
	        put("intEq5", SimpleCondition.create("intField", Negate.NO, Comparison.EQUAL, 5));
	        put("strEq2", SimpleCondition.create("stringField", Negate.NO, Comparison.EQUAL, "2"));
	    }
	});
	

	public void assertExpectedResults(String label, CompoundCondition<SimpleObject> cond, Map<String, ReflectionObject<SimpleObject>> toTest, List<Boolean> expectedResults)
	{
		assert(toTest.size() == expectedResults.size());
		
		Iterator<Boolean> itr = expectedResults.iterator();
		for (Entry<String, ReflectionObject<SimpleObject>> pair : toTest.entrySet())
		{
			boolean expected = itr.next();
			assertTrue(cond.evaluate(pair.getValue()) == expected, label + " expected " + expected + " but failed for simple object " + pair.getKey());
		}
	}
	
	@Test
	void create_vargs() 
	{	
		// TODO
	}
	
	@Test
	void create_list() 
	{	
		// TODO
	}
	
	@Test
	void evaluate_many() 
	{	
		// TODO
	}
	
	@Test
	void evaluate_and_nand() 
	{		
		CompoundCondition<SimpleObject> intEq5AndStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.AND, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5AndStrEq2", intEq5AndStrEq2, OBJ_LIST, List.of(false, false, true, false));
		CompoundCondition<SimpleObject> intEq5NandStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.NAND, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5NandStrEq2", intEq5NandStrEq2, OBJ_LIST, List.of(true, true, false, true));

		CompoundCondition<SimpleObject> intEq5AndStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.AND, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5AndStrNeq2", intEq5AndStrNeq2, OBJ_LIST, List.of(true, false, false, false));
		CompoundCondition<SimpleObject> intEq5NandStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.NAND, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5NandStrNeq2", intEq5NandStrNeq2, OBJ_LIST, List.of(false, true, true, true));
		
		CompoundCondition<SimpleObject> strEq2AndIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), new LogicConditionPair<SimpleObject>(
						Logic.AND, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2AndIntNeq5", strEq2AndIntNeq5, OBJ_LIST, List.of(false, false, false, true));
		CompoundCondition<SimpleObject> strEq2NandIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), new LogicConditionPair<SimpleObject>(
						Logic.NAND, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2NandIntNeq5", strEq2NandIntNeq5, OBJ_LIST, List.of(true, true, true, false));
	}
	
	@Test
	void evaluate_or_nor() 
	{
		CompoundCondition<SimpleObject> intEq5OrStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.OR, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5OrStrEq2", intEq5OrStrEq2, OBJ_LIST, List.of(true, false, true, true));
		CompoundCondition<SimpleObject> intEq5NorStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.NOR, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5NorStrEq2", intEq5NorStrEq2, OBJ_LIST, List.of(false, true, false, false));

		CompoundCondition<SimpleObject> intEq5OrStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.OR, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5OrStrNeq2", intEq5OrStrNeq2, OBJ_LIST, List.of(true, true, true, false));
		CompoundCondition<SimpleObject> intEq5NorStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.NOR, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5NorStrNeq2", intEq5NorStrNeq2, OBJ_LIST, List.of(false, false, false, true));
		
		CompoundCondition<SimpleObject> strEq2OrIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), new LogicConditionPair<SimpleObject>(
						Logic.OR, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2OrIntNeq5", strEq2OrIntNeq5, OBJ_LIST, List.of(false, true, true, true));
		CompoundCondition<SimpleObject> strEq2NorIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), new LogicConditionPair<SimpleObject>(
						Logic.NOR, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2NorIntNeq5", strEq2NorIntNeq5, OBJ_LIST, List.of(true, false, false, false));
	}
	
	@Test
	void evaluate_xor_xnor() 
	{
		CompoundCondition<SimpleObject> intEq5XorStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.XOR, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5XorStrEq2", intEq5XorStrEq2, OBJ_LIST, List.of(true, false, false, true));
		CompoundCondition<SimpleObject> intEq5XnorStrEq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.XNOR, Negate.NO, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5XnorStrEq2", intEq5XnorStrEq2, OBJ_LIST, List.of(false, true, true, false));

		CompoundCondition<SimpleObject> intEq5XorStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.XOR, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5XorStrNeq2", intEq5XorStrNeq2, OBJ_LIST, List.of(false, true, true, false));
		CompoundCondition<SimpleObject> intEq5XnorStrNeq2 = CompoundCondition.create(
				SIMPLE_CONDS.get("intEq5"), new LogicConditionPair<SimpleObject>(
						Logic.XNOR, Negate.YES, SIMPLE_CONDS.get("strEq2")));
		assertExpectedResults("intEq5XnorStrNeq2", intEq5XnorStrNeq2, OBJ_LIST, List.of(true, false, false, true));
		
		CompoundCondition<SimpleObject> strEq2XorIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), new LogicConditionPair<SimpleObject>(
						Logic.XOR, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2XorIntNeq5", strEq2XorIntNeq5, OBJ_LIST, List.of(false, true, true, false));
		CompoundCondition<SimpleObject> strEq2XnorIntNeq5 = CompoundCondition.create(
				SIMPLE_CONDS.get("strEq2"), new LogicConditionPair<SimpleObject>(
						Logic.XNOR, Negate.YES, SIMPLE_CONDS.get("intEq5")));
		assertExpectedResults("strEq2XnorIntNeq5", strEq2XnorIntNeq5, OBJ_LIST, List.of(true, false, false, true));
	}
}
