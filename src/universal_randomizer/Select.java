package universal_randomizer;

import java.util.stream.Stream;

import condition.Condition;
import universal_randomizer.action.IntermediateAction;
import universal_randomizer.action.ReflObjStreamAction;
import universal_randomizer.wrappers.ReflectionObject;

public class Select<T> extends IntermediateAction<T>
{	
	public enum SelectStrategy
	{
		EACH, ALL
	}
	
	Condition<T> varExpr;
	
	// TODO: Refactor to factory instead of constructor
	
	public Select(Condition<T> varExpr, ReflObjStreamAction<T> nextAction)
	{
		super(nextAction);
		
		this.varExpr = varExpr;
	}
	
//	public Select(Condition<T> varExpr, RawStreamAction<T> nextAction)
//	{
//		super(new StreamActionWrapper<>(nextAction));
//		
//		this.varExpr = varExpr;
//	}
	
	public boolean perform(Stream<ReflectionObject<T>> objStream)
	{
		return continueActions(objStream.filter(obj -> varExpr.evaluate(obj)));
	}
}
