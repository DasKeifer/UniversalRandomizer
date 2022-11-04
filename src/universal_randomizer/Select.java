package universal_randomizer;

import java.util.stream.Stream;

import condition.Condition;
import universal_randomizer.interfaces.StreamAction;
import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.StreamActionReflectionObjectWrapper;
import universal_randomizer.wrappers.ReflectionObjectStreamAction;

public class Select<T> extends IntermediateAction<T>
{	
	public enum SelectStrategy
	{
		EACH, ALL
	}
	
	Condition<T> varExpr;
	
	// TODO: Refactor to factory instead of constructor
	
	public Select(Condition<T> varExpr, ReflectionObjectStreamAction<T> nextAction)
	{
		super(nextAction);
		
		this.varExpr = varExpr;
	}
	
	public Select(Condition<T> varExpr, StreamAction<T> nextAction)
	{
		super(new StreamActionReflectionObjectWrapper<>(nextAction));
		
		this.varExpr = varExpr;
	}
	
	public boolean perform(Stream<ReflectionObject<T>> objStream)
	{
		return continueActions(objStream.filter(obj -> varExpr.evaluate(obj)));
	}
}
