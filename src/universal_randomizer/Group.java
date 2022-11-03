package universal_randomizer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;

public class Group<T extends Object> extends IntermediateAction<T>
{
	String var;

	// TODO: Refactor to factory instead of constructor?
	
	public Group(String groupingVar, StreamAction<T> nextAction)
	{
		super(nextAction);
		var = groupingVar;
	}
	
	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream)
	{
		Map<Object, List<ReflectionObject<T>>> grouped = 
				objStream.collect(Collectors.groupingBy(
					x -> x.getVariableValue(var)));
		
		boolean okay = true;
		for (List<ReflectionObject<T>> group : grouped.values())
		{
			okay = continueActions(group.stream());
		}
		return okay;
	}
}
