package universal_randomizer.select;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import condition.Condition;
import universal_randomizer.ReflectionObject;
public class SelectAll extends Select{
	
	public SelectAll(Condition varExpr)
	{
		super(varExpr);
	}

	@Override
	protected boolean continueWork(Stream<ReflectionObject> objStream)
	{
		return nextThingummy(objStream.collect(Collectors.toList()));
	}

}
