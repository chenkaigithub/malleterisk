package unitary.fs.methods;

import java.io.File;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import fs.FeatureTransformationPipeline;
import fs.IFeatureTransformer;
import fs.functions.Functions;
import fs.methods.FilterByRankedDF;

public class FilterByRankedDFUnitTest {
//	private InstanceList instances;
//	private int threshold;
//	
//	@Before
//	public void setUp() {
//		this.instances = InstanceList.load(new File(""));
//		this.threshold = 5;
//		
//		Assert.assertEquals(instances.size(), 0);
//		Assert.assertEquals(instances.getAlphabet().size(), 0);
//	}
//	
//	@Test
//	public void testDF() {
//		RankedFeatureVector rfv = Functions.df(instances);
//		
//		Assert.assertEquals(rfv.getAlphabet().size(), 0);
//	}
//	
//	@Test
//	public void testPruneByDF() {
//		LinkedList<IFeatureTransformer> fts = new LinkedList<IFeatureTransformer>();
//		fts.add(new FilterByRankedDF(threshold));
//		InstanceList newInstances = new FeatureTransformationPipeline(fts).runThruPipeline(instances);
//		
//		Assert.assertEquals(newInstances.getAlphabet().size(), 0);
//	}
}
