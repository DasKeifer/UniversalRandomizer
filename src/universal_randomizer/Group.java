package universal_randomizer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Group extends IntermediateAction
{
	String var;
	
	public Group(String groupingVar, StreamAction nextAction)
	{
		super(nextAction);
		var = groupingVar;
	}
	
	@Override
	public boolean perform(Stream<ReflectionObject> objStream)
	{
		Map<Object, List<ReflectionObject>> grouped = 
				objStream.collect(Collectors.groupingBy(
					x -> {
		                return x.getVariableValue(var);
		            }));
		
		boolean okay = true;
		for (List<ReflectionObject> group : grouped.values())
		{
			okay = continueActions(group.stream());
		}
		return okay;
	}
}
