package unitary.execution;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import types.mallet.LabeledInstancesList;
import cc.mallet.types.InstanceList;
import execution.RandomSampler;

public class RandomSamplerUnitTest {
	private InstanceList ilist;
	private RandomSampler rs;
	
	@Before
	public void setUp() {
		this.ilist = InstanceList.load(new File("instances+1+6+subjects"));
		this.rs = new RandomSampler(ilist, 10);
	}
	
	@Test
	public void test() {
		InstanceList newInstances = rs.x(1);
		
		LabeledInstancesList newLil = new LabeledInstancesList(newInstances);
		InstanceList[] ilists = newLil.getLabeledInstances();
		for (InstanceList il : ilists) {
			if(il!=null) {
				System.out.println(il.getTargetAlphabet().toString());
				Assert.assertEquals(il.size(), 1);
			}
		}
		
		
	}
}
