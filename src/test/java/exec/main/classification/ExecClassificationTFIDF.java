package exec.main.classification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.InstanceList;
import classifiers.TFIDFTrainer;
import execution.ExecutionResult;
import execution.ExecutionUtils;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedIG;
import ft.weighting.IWeighter;
import ft.weighting.methods.FeatureWeighting;

public class ExecClassificationTFIDF {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+body"));
//		files.add(new File("instances+2+2+body"));
//		files.add(new File("instances+2+3+body"));
//		files.add(new File("instances+2+4+body"));
//		files.add(new File("instances+2+5+body"));
//		files.add(new File("instances+2+6+body"));
//		files.add(new File("instances+2+7+body"));
//		files.add(new File("instances+2+8+body"));
		
		ArrayList<IWeighter> transformers = new ArrayList<IWeighter>();
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_LOG, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_COSINE));

		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedIG());
		
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		classifiers.add(new TFIDFTrainer());
		
//		int step = 10;
//		int folds = 10;
//		ExecutionUtils.runWeightersFiltersClassifiers(files, transformers, filters, classifiers, step, folds);

		for (File file : files) {
			ClassifierTrainer<Classifier> ct = new TFIDFTrainer();
			
			InstanceList instances = InstanceList.load(file);
			ExecutionResult r = new ExecutionResult(file.getName(), null, null, ExecutionUtils.getClassifierDescription(ct));
			r.trials.put(0, ExecutionUtils.crossValidate(instances, 10, ct));
			r.outputTrials();
			r.outputAccuracies();
		}
	}
}
