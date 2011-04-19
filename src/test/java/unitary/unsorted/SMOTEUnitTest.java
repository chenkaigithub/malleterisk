package unitary.unsorted;

import imbalance.SMOTE;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class SMOTEUnitTest {
	private int k;
	private InstanceList instances;
		
	@Before
	public void setup() {
		k = 3;
		this.instances = InstanceList.load(new File("instances+0+0+tests"));
	}
	
	@Test
	public void test1() {
		InstanceList smotedInstances = SMOTE.smote(instances, 2, k);
		
		Assert.assertEquals(2*instances.size(), smotedInstances.size());
		
		for (Instance instance : smotedInstances)
			System.out.println(instance.getData());
	}
}
