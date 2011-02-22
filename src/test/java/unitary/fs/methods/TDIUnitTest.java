package unitary.fs.methods;

import java.io.File;
import java.util.LinkedList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.FeatureTransformationPipeline;
import fs.IFeatureTransformer;
import fs.methods.TDI;

public class TDIUnitTest {
	private InstanceList instances;
	
	@Before
	public void setUp() {
		/*	manually generated term occurrences:
			duke: 8.0
			citru: 6.0
			glatzer: 1.0
			schedul: 6.0
			montauk: 9.0
			settlement: 7.0
			ecoga: 4.0
			legal: 5.0
			expens: 3.0
			8: 4.0
			17: 3.0
			00: 3.0
		 */
		instances = InstanceList.load(new File("instances_0-0_tests"));
	}
	
	@Test
	public void test1() {
		LinkedList<IFeatureTransformer> featureSelectors = new LinkedList<IFeatureTransformer>();
		featureSelectors.add(new TDI());
		InstanceList newInstances = new FeatureTransformationPipeline(featureSelectors).runThruPipeline(instances);
		
		for (Instance instance : newInstances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			
			for (int idx : fv.getIndices()) {
				Assert.assertEquals(1.0, fv.value(idx));
			}
		}
	}
}
