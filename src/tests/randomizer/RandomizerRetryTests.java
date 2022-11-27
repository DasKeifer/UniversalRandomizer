package tests.randomizer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import tests.support.SimpleObject;
import tests.support.TestData;
import universal_randomizer.condition.Compare;
import universal_randomizer.condition.Negate;
import universal_randomizer.condition.SimpleCondition;
import universal_randomizer.randomize.OnFailAction;
import universal_randomizer.randomize.OnFailActionAttempts;
import universal_randomizer.randomize.Randomizer;
import universal_randomizer.randomize.RandomizerResuse;
import universal_randomizer.wrappers.ReflectionObject;

class RandomizerRetryTests 
{
	@Test
	void Randomizer_Reuse_BasicRetryTests() 
	{
		System.out.println("----------- Randomizer_Reuse_BasicRetryTests ------------");
		List<ReflectionObject<SimpleObject>> soList = TestData.getCopyOfSoList();
		Randomizer<SimpleObject, String> rand = RandomizerResuse.createSeededPoolFromStream("name", 1);
		
		List<OnFailAction> failActions = new LinkedList<>();
		failActions.add(OnFailActionAttempts.createRetryAction(5));
		rand.setOnFailActions(failActions);
		rand.setEnforce(new SimpleCondition<SimpleObject, String>("name", Negate.YES, Compare.EQUAL, "7"));
		assertTrue(rand.perform(soList.stream()));
		
		List<String> foundNames = soList.stream().map(ro -> ro.getObject().name).collect(Collectors.toList());
		List<String> expectedNames = Arrays.asList("7", "2", "2", "7", "9", "5", "6", "2", "2");
		assertIterableEquals(expectedNames, foundNames, "expected: " + expectedNames + ", found: " + foundNames);
	}
	
	@Test
	void Randomizer_Reuse_FailRetryTests() 
	{
		System.out.println("----------- Randomizer_Reuse_FailRetryTests ------------");
		List<ReflectionObject<SimpleObject>> soList = TestData.getCopyOfSoList();
		Randomizer<SimpleObject, String> rand = RandomizerResuse.createSeededPoolFromStream("name", 1);
		
		List<OnFailAction> failActions = new LinkedList<>();
		failActions.add(OnFailActionAttempts.createRetryAction(5));
		rand.setOnFailActions(failActions);
		rand.setEnforce(new SimpleCondition<SimpleObject, String>("name", Compare.EQUAL, "an unused name"));
		assertFalse(rand.perform(soList.stream()));
		
		// Note there is no guarantee that the state was not changed in the case of failure
		// so we don't check it
	}
}
