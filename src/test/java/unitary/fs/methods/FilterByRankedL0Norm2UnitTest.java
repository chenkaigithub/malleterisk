package unitary.fs.methods;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.InstanceList;
import fs.methods.FilterByRankedL0Norm2;

public class FilterByRankedL0Norm2UnitTest {
	private static final InstanceList originalInstances = InstanceList.load(new File("ilBody.txt"));
	
	private int numFeaturesToKeep;
	
	@Before
	public void setUp() {
		numFeaturesToKeep = 100;
	}
	
	@Test
	public void testL0Rank() {
		FilterByRankedL0Norm2 rl0n = new FilterByRankedL0Norm2(numFeaturesToKeep);
		InstanceList newInstances = rl0n.transform(originalInstances);
		
		Assert.assertEquals(100, newInstances.getAlphabet().size());
	}
}
