package tests.fs.methods;

public class FilterByRankedL0Norm1Test {
//	private static final InstanceList originalInstances = InstanceList.load(new File("ilBody.txt"));
//	
//	private int numFeaturesToKeep;
//	
//	@Before
//	public void setUp() {
//		numFeaturesToKeep = 100;
//	}
//	
//	@Test
//	public void testL0Norm() {
//		// test if  Functions.l0norm is working correctly
//	}
//	
//	@Test
//	public void testL0Pruning() {
//		LinkedList<IFeatureTransformer> fts = new LinkedList<IFeatureTransformer>();
//		fts.add(new FilterByRankedL0Norm1(numFeaturesToKeep));
//		InstanceList newInstances = new FeatureTransformationPipeline(fts).runThruPipeline(originalInstances);
//		
//		Assert.assertEquals(newInstances.getDataAlphabet().size(), numFeaturesToKeep);
//		Assert.assertNotSame(newInstances, originalInstances);
//		Assert.assertNotSame(newInstances.getDataAlphabet(), originalInstances.getDataAlphabet());
//	}
//	
//	@Test
//	public void testL0Values() {
//		// applies the l0 prunning to 
//		LinkedList<IFeatureTransformer> fts = new LinkedList<IFeatureTransformer>();
//		FilterByRankedL0Norm1 l0 = new FilterByRankedL0Norm1(numFeaturesToKeep);
//		fts.add(l0);
//		/*InstanceList newInstances = */new FeatureTransformationPipeline(fts).runThruPipeline(originalInstances);
//		
//		RankedFeatureVector originalRankedFeatureVector = Functions.l0norm(originalInstances);
//		RankedFeatureVector modifiedRankedFeatureVector = l0.getRankedFeatureVector();
//		
//		// this is valid, since I know there are no terms with l0 == 0 or l0 == instances.size()
//		for(int i=0; i<numFeaturesToKeep;++i) {
//			Object obj1 = originalRankedFeatureVector.getObjectAtRank(i);
//			Object obj2 = modifiedRankedFeatureVector.getObjectAtRank(i);
//			
//			System.out.println(
//				obj1.toString() + "\t" + originalRankedFeatureVector.getValueAtRank(i) + "\t\t" + 
//				obj2.toString() + "\t" + modifiedRankedFeatureVector.getValueAtRank(i)
//			);
//			
//			Assert.assertEquals(obj1, obj2);
//		}
//	}
}
