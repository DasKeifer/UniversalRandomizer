package universal_randomizer.select;

import java.util.stream.Stream;

import condition.SimpleCondition;
import universal_randomizer.ReflectionObject;

public class SelectEach extends Select{

	public SelectEach(SimpleCondition varExpr)
	{
		super(varExpr);
	}
	
	@Override
	protected boolean continueWork(Stream<ReflectionObject> objStream)
	{
//		Map<Object, List<ReflectionObject>> grouped = 
//				objStream.collect(Collectors.groupingBy(
//					x -> {
//		                return x.getVariableValue(varExpr.variable);
//		            }));
//		
//		boolean okay = true;
//		for (List<ReflectionObject> group : grouped.values())
//		{
//			okay = nextThingummy(group);
//		}
//		return okay;
		return false;
	}
}
