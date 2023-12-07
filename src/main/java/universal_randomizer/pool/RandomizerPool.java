package universal_randomizer.pool;

import java.util.Random;

public interface RandomizerPool<T>
{
	public T peek(Random rand);
	public T peekBatch(Random rand);
	public void peekNewBatch();
	public void selectPeeked();
	public void reset();
	public void resetPeeked();
	public boolean useNextPool();
}