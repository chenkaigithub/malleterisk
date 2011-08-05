package tests.fs.methods;



public class TFIDFTest {
//	private InstanceList instances;
//	
//	@Before
//	public void setUp() {
//		/*	manually generated term occurrences:
//			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/10.
//			duke: 8.0
//			citru: 6.0
//			
//			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/18.
//			glatzer: 1.0
//			
//			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/34.
//
//			schedul: 6.0
//			
//			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/ecogas/10.
//
//			montauk: 9.0
//			settlement: 7.0
//			
//			/Work/msc/code/seamce/../../data/enron_flat/sanders-r/ecogas/18.
//			ecoga: 4.0
//			legal: 5.0
//			expens: 3.0
//			8: 4.0
//			17: 3.0
//			00: 3.0
//		 */
//		instances = InstanceList.load(new File("instances+0+0+tests"));
//	}
//	
//	public static final InstanceList tfidf(InstanceList instances) {
//		Alphabet alphabet = instances.getDataAlphabet();
//		InstanceList newInstances = new InstanceList(alphabet, instances.getTargetAlphabet());
//		
//		// cache the idfs
//		TreeMap<Integer, Double> idfs = new TreeMap<Integer, Double>();
//		
//		// number of documents
//		double n = instances.size();
//		
//		for (Instance instance : instances) {
//			// get features of current instance
//			FeatureVector fv = (FeatureVector) instance.getData();
//			int[] indices = fv.getIndices();
//			
//			double[] tfidfValues = new double[indices.length];
//			int i = 0;
//			for(int idx : indices) {
//				final double tf = fv.value(idx);
//				Double idf; // check the cache for the value
//				if((idf=idfs.get(idx))==null) {
//					idf = Math.log10(n / (1.0+Functions.df(idx, instances)));
//					idfs.put(idx, idf);
//				}
//				
//				tfidfValues[i++] = tf*idf;
//			}
//			
//			newInstances.add(new Instance(
//				new FeatureVector(instance.getDataAlphabet(), indices, tfidfValues), 
//				instance.getTarget(), 
//				instance.getName(), 
//				instance.getSource()
//			));
//		}
//		
//		return newInstances;
//	}
//	
////	@Test
////	public void test0() {
////		new FeatureWeighting(FeatureWeighting.TF_BOOLEAN, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE).calculate(instances);
////		new FeatureWeighting(FeatureWeighting.TF_LOG, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE).calculate(instances);
////		new FeatureWeighting(FeatureWeighting.TF_MAX_NORM, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE).calculate(instances);
////		new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE).calculate(instances);
////		new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE).calculate(instances);
////		new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_COSINE).calculate(instances);
////	}
//	
//	@Test
//	public void test1() {
//		IWeighter transformer = new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE);
//		InstanceList newInstances1 = transformer.calculate(instances);
//		InstanceList newInstances2 = tfidf(instances);
//		
//		for(int i=0; i<newInstances1.size(); ++i) {
//			Instance i1 = newInstances1.get(i);
//			Instance i2 = newInstances2.get(i);
//			
//			FeatureVector fv1 = (FeatureVector)i1.getData();
//			FeatureVector fv2 = (FeatureVector)i2.getData();
//			
//			int[] indices = fv1.getIndices();
//			for (int idx : indices) {
//				Assert.assertEquals(fv1.value(idx), fv2.value(idx));
//			}
//		}
//		
////		for (Instance instance : newInstances) {
////			FeatureVector fv = (FeatureVector)instance.getData();
////			
////			for (int idx : fv.getIndices()) {
////				String t = alphabet.lookupObject(idx).toString();
////				if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/10.")) {
////					if(t.equals("duke")) Assert.assertEquals(tfidf(8.0), fv.value(idx));
////					else if(t.equals("citru")) Assert.assertEquals(tfidf(6.0), fv.value(idx));
////				}
////				else if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/18.")) {
////					if(t.equals("glatzer")) Assert.assertEquals(tfidf(1.0), fv.value(idx));
////				}
////				else if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/sanders-r/duke/34.")) {
////					if(t.equals("schedul")) Assert.assertEquals(tfidf(6.0), fv.value(idx));
////				}
////				else if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/ecogas/10.")) {
////					if(t.equals("montauk")) Assert.assertEquals(tfidf(9.0), fv.value(idx));
////					else if(t.equals("settlement")) Assert.assertEquals(tfidf(7.0), fv.value(idx));
////				}
////				else if(instance.getName().equals("/Work/msc/code/seamce/../../data/enron_flat/sanders-r/ecogas/18.")) {
////					if(t.equals("ecoga")) Assert.assertEquals(tfidf(4.0), fv.value(idx));
////					else if(t.equals("legal")) Assert.assertEquals(tfidf(5.0), fv.value(idx));
////					else if(t.equals("expens")) Assert.assertEquals(tfidf(3.0), fv.value(idx));
////					else if(t.equals("8")) Assert.assertEquals(tfidf(4.0), fv.value(idx));
////					else if(t.equals("17")) Assert.assertEquals(tfidf(3.0), fv.value(idx));
////					else if(t.equals("00")) Assert.assertEquals(tfidf(3.0), fv.value(idx));
////				}
////			}
////			
////		}
//	}
}
