package executions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import main.SEAMCE;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedTF;
import ft.transformation.ITransformer;
import ft.transformation.methods.FeatureWeighting;

public class ExecRepresentation {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
//		files.add(new File("instances+0+0+tests"));
		files.add(new File("instances+1+1+bodies"));
		files.add(new File("instances+1+2+bodies"));
		files.add(new File("instances+1+3+bodies"));
		files.add(new File("instances+1+4+bodies"));
		files.add(new File("instances+1+5+bodies"));
		files.add(new File("instances+1+6+bodies"));
		files.add(new File("instances+1+7+bodies"));
		files.add(new File("instances+2+1+bodies"));
		
		ArrayList<ITransformer> transformers = new ArrayList<ITransformer>();
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_BOOLEAN, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE)); // boolean
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE)); // nnn
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_MAX_NORM, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE)); // mnn
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_LOG, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE)); // lnn
		
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE)); // ntn
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_LOG, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_COSINE)); // ltc
		
		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedTF());
		
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		classifiers.add(new NaiveBayesTrainer());
		
		int step = 10;
		int folds = 10;
		
		for (File file : files) {
			System.out.println("+ processing " + file.getName());
			InstanceList instances = InstanceList.load(file);
			for (ITransformer transformer : transformers) {
				System.out.println("- transformer: " + transformer.getDescription());
				for (IFilter filter : filters) {
					System.out.println("- filter: " + filter.getDescription());
					for (ClassifierTrainer<? extends Classifier> trainer : classifiers) {
						SEAMCE.sequentialRun(file.getName(), instances, transformer, filter, trainer, step, folds);
					}
				}
			}
		}
	}
}
