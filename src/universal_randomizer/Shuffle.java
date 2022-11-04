package universal_randomizer;

import java.util.Random;
import java.util.stream.Stream;

import universal_randomizer.wrappers.ReflectionObject;

public class Shuffle<T> extends IntermediateAction<T>
{
	Random rand;
	
	private Shuffle(StreamAction<T> nextAction, Random rand)
	{
		super(nextAction);
		if (rand == null)
		{
			rand = new Random();
		}
		else
		{
			this.rand = rand;
		}
	}
	
	public static <T> Shuffle<T> createRandom(StreamAction<T> nextAction)
	{
		return new Shuffle<>(nextAction, null);
	}
			
	public static <T> Shuffle<T> createSeeded(StreamAction<T> nextAction, long seed)
	{
		return new Shuffle<>(nextAction, new Random(seed));
	}

	@Override
	public boolean perform(Stream<ReflectionObject<T>> objStream) 
	{
		return continueActions(objStream
				.map(this::assignRandValue)
				.sorted(Shuffle::sortByRandValue));
	}

	private ReflectionObject<T> assignRandValue(ReflectionObject<T> obj)
	{
		obj.setRandomValue(rand.nextInt());
		return obj;
	}
	
	private static <T> int sortByRandValue(ReflectionObject<T> lhs, ReflectionObject<T> rhs)
    {
        return Integer.compare(lhs.getRandomValue(), rhs.getRandomValue());
    }
}
