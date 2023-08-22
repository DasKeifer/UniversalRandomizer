package universal_randomizer.condition;

public enum Logic 
{
	AND, OR, XOR, NAND, NOR, XNOR;
	
	public boolean isNegationLogic()
	{
		return this == NAND || this == NOR || this == XNOR;
	}
}
