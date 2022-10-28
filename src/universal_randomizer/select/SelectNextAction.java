package universal_randomizer.select;

import java.util.Collection;

import universal_randomizer.ReflectionObject;

public interface SelectNextAction {
	boolean nextAction(Collection<ReflectionObject> objects);
}
