package universal_randomizer.select;

import java.util.Collection;
import java.util.stream.Stream;

import condition.Condition;
import universal_randomizer.ReflectionObject;

public abstract class Select {
	
	public enum SelectStrategy
	{
		EACH, ALL
	}
	
	Condition varExpr;
	public SelectNextAction nextAction;
	
	protected Select(Condition varExpr)
	{
		this.varExpr = varExpr;
	}
	
	
	public boolean perform(Collection<ReflectionObject> objects)
	{
		return continueWork(objects.stream().filter(obj -> varExpr.evaluate(obj)));
	}
	
	protected abstract boolean continueWork(Stream<ReflectionObject> objects);
	
	protected boolean nextThingummy(Collection<ReflectionObject> objects)
	{
		if (nextAction != null)
		{
			return nextAction.nextAction(objects);
		}
		return false;
	}
}
