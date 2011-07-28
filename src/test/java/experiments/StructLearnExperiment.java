package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import main.SEAMCE;
import struct.classification.KBestMiraClassifierTrainer;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedIG;
import ft.weighting.IWeighter;
import ft.weighting.methods.FeatureWeighting;

public class StructLearnExperiment {
	// http://www.seas.upenn.edu/~strctlrn/StructLearn/StructLearn.html
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+bodies"));

		ArrayList<IWeighter> transformers = new ArrayList<IWeighter>();
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE));

		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedIG());

		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		classifiers.add(new KBestMiraClassifierTrainer(5));

		int step = 10;
		int folds = 10;

		SEAMCE.x(files, transformers, filters, classifiers, step, folds);
	}
}
