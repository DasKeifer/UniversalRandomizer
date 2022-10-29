package universal_randomizer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Group<T extends Object> extends IntermediateAction<T>
{
	String var;
	
	public Group(String groupingVar, StreamAction<T> nextAction)
	{
		super(nextAction);
		var = groupingVar;
	}
	
	@Override
	public boolean perform(Stream<T> objStream)
	{
		Map<Object, List<T>> grouped = 
				objStream.collect(Collectors.groupingBy(
					x -> {
		                return ReflectionUtils.getVariableValue(x, var);
		            }));
		
		boolean okay = true;
		for (List<T> group : grouped.values())
		{
			okay = continueActions(group.stream());
		}
		return okay;
	}
}
