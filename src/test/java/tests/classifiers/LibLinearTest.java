package tests.classifiers;


public class LibLinearTest {
//	private InstanceList instances;
//	private LibLinearTrainer trainer;
//		
//	@Before
//	public void setup() {
//		this.instances = InstanceList.load(new File("instances+0+0+tests"));
//		this.trainer = new LibLinearTrainer();
//	}
//	
//	@Test
//	public void test1() {
//		CrossValidationIterator cvi = instances.crossValidationIterator(3);
//		InstanceList[] folds = null;
//		Classifier classifier = null;
//		InstanceList trainInstances, testInstances;
//		while(cvi.hasNext()) {
//			folds = cvi.next();
//			
//			trainInstances = folds[0];
//			testInstances = folds[1];
//			
//			classifier = trainer.train(trainInstances);
//			ArrayList<Classification> c = classifier.classify(testInstances);
//			for (Classification classification : c) {
//				System.out.println("-");
//				System.out.println("real class: " + ((Label)(classification.getInstance().getTarget())).getBestLabel());
//				System.out.println("classified as: " + classification.getLabeling().getBestLabel());
//			}		
//		}
//	}
}
