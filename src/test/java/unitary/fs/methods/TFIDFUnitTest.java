package unitary.fs.methods;

import java.io.File;
import java.util.LinkedList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.FeatureTransformationPipeline;
import fs.IFeatureTransformer;
import fs.methods.TFIDF;

public class TFIDFUnitTest {
	public static void main(String[] args) {
		InstanceList instances = InstanceList.load(new File("instances_0-0_tests"));
		Alphabet alphabet = instances.getDataAlphabet();
		for (Instance instance : instances) {
			System.out.println(instance.getName());
			FeatureVector fv = (FeatureVector) instance.getData();
			for(int idx : fv.getIndices()) {
				System.out.println(alphabet.lookupObject(idx));
				System.out.println(fv.value(idx));
			}
			System.out.println();
		}
	}
	
	private InstanceList instances;
	
	@Before
	public void setUp() {
		/*	manually generated term occurrences:
			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/10.
			duke: 8.0
			citru: 6.0
			
			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/18.
			glatzer: 1.0
			
			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/34.

			schedul: 6.0
			
			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/ecogas/10.

			montauk: 9.0
			settlement: 7.0
			
			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/ecogas/18.
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
		featureSelectors.add(new TFIDF());
		InstanceList newInstances = new FeatureTransformationPipeline(featureSelectors).runThruPipeline(instances);
		
		for (Instance instance : newInstances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			
			for (int idx : fv.getIndices()) {
				Assert.assertEquals(1.0, fv.value(idx));
			}
		}
	}
}
