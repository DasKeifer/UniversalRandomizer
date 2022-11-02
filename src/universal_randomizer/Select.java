package universal_randomizer;

import java.util.stream.Stream;

import condition.Condition;
import universal_randomizer.wrappers.ReflectionObject;
import universal_randomizer.wrappers.UnwrappedStreamAction;
import universal_randomizer.wrappers.WrappedStreamAction;

public class Select<T extends Object> extends IntermediateAction<T>
{	
	public enum SelectStrategy
	{
		EACH, ALL
	}
	
	Condition varExpr;
	
	// TODO: Refactor to factory instead of constructor
	
	public Select(Condition varExpr, StreamAction<T> nextAction)
	{
		super(nextAction);
		
		this.varExpr = varExpr;
	}
	
	public Select(Condition varExpr, UnwrappedStreamAction<T> nextAction)
	{
		super(new WrappedStreamAction<>(nextAction));
		
		this.varExpr = varExpr;
	}
	
	public boolean perform(Stream<ReflectionObject<T>> objStream)
	{
		return continueActions(objStream.filter(obj -> varExpr.evaluate(obj)));
	}
}
