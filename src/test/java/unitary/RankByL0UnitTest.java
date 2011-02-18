package unitary;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.InstanceList;
import fs.methods.RankByL0Norm;

public class RankByL0UnitTest {
	private static final InstanceList originalInstances = InstanceList.load(new File("ilBody.txt"));
	
	private int numFeaturesToKeep;
	
	@Before
	public void setUp() {
		numFeaturesToKeep = 100;
	}
	
	@Test
	public void testL0Rank() {
		RankByL0Norm rl0n = new RankByL0Norm(numFeaturesToKeep);
		InstanceList newInstances = rl0n.transform(originalInstances);
		
		Assert.assertEquals(100, newInstances.getAlphabet().size());
	}
}
