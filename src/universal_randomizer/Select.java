package universal_randomizer;

import java.util.stream.Stream;

import condition.Condition;

public class Select<T extends Object> extends IntermediateAction<T>
{	
	public enum SelectStrategy
	{
		EACH, ALL
	}
	
	Condition varExpr;
	
	public Select(Condition varExpr, StreamAction<T> nextAction)
	{
		super(nextAction);
		
		this.varExpr = varExpr;
	}
	
	public boolean perform(Stream<T> objStream)
	{
		return continueActions(objStream.filter(obj -> varExpr.evaluate(obj)));
	}
}
