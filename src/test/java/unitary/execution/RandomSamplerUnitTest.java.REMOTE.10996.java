package unitary.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import main.SEAMCE;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import execution.RandomSampler;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedIG;
import ft.transformation.ITransformer;
import ft.transformation.methods.FeatureWeighting;

public class RandomSamplerUnitTest {
	
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		String runName = "instances+1+1+subjects";
		InstanceList instances = InstanceList.load(new File(runName));

		// random sampling
		runName += "+rs-t5-n10";
		RandomSampler rs = new RandomSampler(instances, 5);
		instances = rs.x(10);
		
		ArrayList<ITransformer> transformers = new ArrayList<ITransformer>();
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_LOG, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE));

		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedIG());
		
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		classifiers.add(new NaiveBayesTrainer());
		
		int step = 10;
		int folds = 10;
		
		SEAMCE.y(runName, instances, transformers, filters, classifiers, step, folds);
	}
	
//	private InstanceList ilist;
//	private RandomSampler rs;
//	
//	@Before
//	public void setUp() {
//		this.ilist = InstanceList.load(new File("instances+1+6+subjects"));
//		this.rs = new RandomSampler(ilist, 1);
//	}
//	
//	@Test
//	public void test() {
//		InstanceList newInstances = rs.x(10);
//		
//		LabeledInstancesList newLil = new LabeledInstancesList(newInstances);
//		InstanceList[] ilists = newLil.getLabeledInstances();
//		for (InstanceList il : ilists) {
//			if(il!=null) {
//				System.out.println(il.getTargetAlphabet().toString());
//				Assert.assertEquals(il.size(), 1);
//			}
//		}
//	}
}
