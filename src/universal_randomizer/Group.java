package universal_randomizer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;

public class Group<M, T extends Object> extends IntermediateAction<T>
{
	String var;
	Class<M> clazz;

	// TODO: Refactor to factory instead of constructor?
	
	public Group(Class<M> clazz, String groupingVar, StreamAction<T> nextAction)
	{
		super(nextAction);
		this.clazz = clazz;
		var = groupingVar;
	}
	
	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream)
	{
		Map<M, List<ReflectionObject<T>>> grouped = 
				objStream.collect(Collectors.groupingBy(
					x -> x.getVariableValue(var, clazz)));
		
		boolean okay = true;
		for (List<ReflectionObject<T>> group : grouped.values())
		{
			okay = continueActions(group.stream());
		}
		return okay;
	}
}
