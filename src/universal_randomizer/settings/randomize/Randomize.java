package universal_randomizer.settings.randomize;

import java.util.List;

public class Randomize {

	public enum DistroType
	{
		SOURCE, UNIFORM, CUSTOM, EXPRESSION
	}
	
	public enum OnUseType
	{
		REMOVE, KEEP
	}
	

	
	DistroType distroType;
	OnUseType onUseType;
	List<EnforceAction> enforceActions;
}
