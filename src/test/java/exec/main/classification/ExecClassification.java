package exec.main.classification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import classifiers.LibLinearTrainer;


import cc.mallet.classify.BalancedWinnowTrainer;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.DecisionTreeTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.WinnowTrainer;
import execution.ExecutionUtils;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedIG;
import ft.weighting.IWeighter;
import ft.weighting.methods.FeatureWeighting;

public class ExecClassification {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+body"));
		files.add(new File("instances+2+2+body"));
		files.add(new File("instances+2+3+body"));
		files.add(new File("instances+2+4+body"));
		files.add(new File("instances+2+5+body"));
		files.add(new File("instances+2+6+body"));
		files.add(new File("instances+2+7+body"));
		files.add(new File("instances+2+8+body"));
		
		ArrayList<IWeighter> transformers = new ArrayList<IWeighter>();
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE));

		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedIG());
		
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		classifiers.add(new NaiveBayesTrainer());
		classifiers.add(new LibLinearTrainer());
		classifiers.add(new MaxEntTrainer());
		classifiers.add(new DecisionTreeTrainer());
//		classifiers.add(new KNNTrainer(5, new NormalizedDotProductMetric()));
		classifiers.add(new WinnowTrainer());
		classifiers.add(new BalancedWinnowTrainer());
//		classifiers.add(new KBestMiraClassifierTrainer(5));
		
		// TODO: need a way to pass arguments to classifiers [trainer.getClass().newInstance()]
		int step = 10;
		int folds = 10;
		
		ExecutionUtils.runWeightersFiltersClassifiers(files, transformers, filters, classifiers, step, folds);
	}
}
