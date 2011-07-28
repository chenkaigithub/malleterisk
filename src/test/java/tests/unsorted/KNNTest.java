package tests.unsorted;


import java.io.File;

import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import classifiers.KNN;

public class KNNTest {
	private int k;
	private InstanceList instances;
		
	@Before
	public void setup() {
		k = 3;
		this.instances = InstanceList.load(new File("instances+0+0+tests"));
	}
	
	@Test
	public void test1() {
		for (Instance instance : instances) {
			Instance[] knn = KNN.knn(k, instance, instances);
			for (Instance inst : knn)
				System.out.println(inst.getData().toString());
			System.out.println();
		}
	}
}
