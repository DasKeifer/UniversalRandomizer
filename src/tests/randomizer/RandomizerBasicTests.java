package tests.randomizer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import tests.support.SimpleObject;
import tests.support.TestData;
import universal_randomizer.randomize.Randomizer;
import universal_randomizer.randomize.RandomizerEliminate;
import universal_randomizer.randomize.RandomizerResuse;
import universal_randomizer.wrappers.ReflectionObject;

class RandomizerBasicTests
{
	@Test
	void Randomizer_Reuse_BasicTest() 
	{
		System.out.println("----------- Randomizer_Reuse_BasicTest ------------");
		List<ReflectionObject<SimpleObject>> soList = TestData.getCopyOfSoList();
		Randomizer<SimpleObject, String> rand = RandomizerResuse.createSeededPoolFromStream("name", 1);
		rand.perform(soList.stream());
		
		List<String> foundNames = soList.stream().map(ro -> ro.getObject().name).collect(Collectors.toList());
		List<String> expectedNames = Arrays.asList("7", "2", "2", "7", "9", "5", "6", "2", "2");
		assertIterableEquals(expectedNames, foundNames, "expected: " + expectedNames + ", found: " + foundNames);

		Randomizer<SimpleObject, String> randIntVal = RandomizerResuse.createSeededPoolFromStream("intVal", 1);
		randIntVal.perform(soList.stream());
		
		List<Integer> foundIntVal = soList.stream().map(ro -> ro.getObject().intVal).collect(Collectors.toList());
		List<Integer> expectedIntVals = Arrays.asList(5, 1, 1, 5, 4, 1, 7, 1, 1);
		assertIterableEquals(expectedIntVals, foundIntVal, "expected: " + expectedIntVals + ", found: " + foundIntVal);
	}

	@Test
	void Randomizer_Eliminate_BasicTest() 
	{
		System.out.println("----------- Randomizer_Eliminate_BasicTest ------------");
		List<ReflectionObject<SimpleObject>> soList = TestData.getCopyOfSoList();
		Randomizer<SimpleObject, String> rand = RandomizerEliminate.createSeededPoolFromStream("name", 1);
		rand.perform(soList.stream());
		
		List<String> foundNames = soList.stream().map(ro -> ro.getObject().name).collect(Collectors.toList());
		List<String> expectedNames = Arrays.asList("7", "1", "3", "6", "9", "2", "8", "5", "4");
		assertIterableEquals(expectedNames, foundNames, "expected: " + expectedNames + ", found: " + foundNames);

		Randomizer<SimpleObject, String> randIntVal = RandomizerEliminate.createSeededPoolFromStream("intVal", 1);
		randIntVal.perform(soList.stream());
		
		List<Integer> foundIntVal = soList.stream().map(ro -> ro.getObject().intVal).collect(Collectors.toList());
		List<Integer> expectedIntVals = Arrays.asList(5, 4, 2, 7, 4, 1, 9, 1, 4);
		assertIterableEquals(expectedIntVals, foundIntVal, "expected: " + expectedIntVals + ", found: " + foundIntVal);
	}
}
