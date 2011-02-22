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
	
	private double tfidf(double v) {
		return (v) * Math.log(5/2);
	}
	
	@Test
	public void test1() {
		LinkedList<IFeatureTransformer> featureSelectors = new LinkedList<IFeatureTransformer>();
		featureSelectors.add(new TFIDF());
		InstanceList newInstances = new FeatureTransformationPipeline(featureSelectors).runThruPipeline(instances);
		Alphabet alphabet = newInstances.getDataAlphabet();
		
		for (Instance instance : newInstances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			
			for (int idx : fv.getIndices()) {
				String t = alphabet.lookupObject(idx).toString();
				if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/10.")) {
					if(t.equals("duke")) Assert.assertEquals(tfidf(8), fv.value(idx));
					else if(t.equals("citru")) Assert.assertEquals(tfidf(6), fv.value(idx));
				}
				else if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/18.")) {
					if(t.equals("glatzer")) Assert.assertEquals(tfidf(1), fv.value(idx));
				}
				else if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/34.")) {
					if(t.equals("schedul")) Assert.assertEquals(tfidf(6), fv.value(idx));
				}
				else if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/ecogas/10.")) {
					if(t.equals("montauk")) Assert.assertEquals(tfidf(9), fv.value(idx));
					else if(t.equals("settlement")) Assert.assertEquals(tfidf(7), fv.value(idx));
				}
				else if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/sanders-r/ecogas/18.")) {
					if(t.equals("ecoga")) Assert.assertEquals(tfidf(4), fv.value(idx));
					else if(t.equals("legal")) Assert.assertEquals(tfidf(5), fv.value(idx));
					else if(t.equals("expens")) Assert.assertEquals(tfidf(3), fv.value(idx));
					else if(t.equals("8")) Assert.assertEquals(tfidf(4), fv.value(idx));
					else if(t.equals("17")) Assert.assertEquals(tfidf(3), fv.value(idx));
					else if(t.equals("00")) Assert.assertEquals(tfidf(3), fv.value(idx));
				}
			}
			
		}
	}
}
