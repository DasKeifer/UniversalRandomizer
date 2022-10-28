package universal_randomizer;

import java.util.stream.Stream;

import condition.Condition;

public class Select extends IntermediateAction 
{	
	public enum SelectStrategy
	{
		EACH, ALL
	}
	
	Condition varExpr;
	
	public Select(Condition varExpr, StreamAction nextAction)
	{
		super(nextAction);
		
		this.varExpr = varExpr;
	}
	
	public boolean perform(Stream<ReflectionObject> objStream)
	{
		return continueActions(objStream.filter(obj -> varExpr.evaluate(obj)));
	}
}
