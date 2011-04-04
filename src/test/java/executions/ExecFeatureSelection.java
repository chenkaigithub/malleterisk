package executions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import main.SEAMCE;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedDF;
import ft.selection.methods.FilterByRankedFisher;
import ft.selection.methods.FilterByRankedIG;
import ft.selection.methods.FilterByRankedL0Norm1;
import ft.selection.methods.FilterByRankedL0Norm2;
import ft.selection.methods.FilterByRankedTF;
import ft.selection.methods.FilterByRankedVariance;
import ft.transformation.ITransformer;
import ft.transformation.methods.FeatureWeighting;

public class ExecFeatureSelection {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+bodies"));
		files.add(new File("instances+1+2+bodies"));
		files.add(new File("instances+1+3+bodies"));
		files.add(new File("instances+1+4+bodies"));
		files.add(new File("instances+1+5+bodies"));
		files.add(new File("instances+1+6+bodies"));
		files.add(new File("instances+1+7+bodies"));
		files.add(new File("instances+2+1+bodies"));
		
//		System.out.println("tdi + feature selection");
//		ArrayList<ITransformer> booleanTransformer = new ArrayList<ITransformer>();
//		booleanTransformer.add(new FeatureWeighting(FeatureWeighting.TF_BOOLEAN, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE));
//		x(files, booleanTransformer);
//		Runtime.getRuntime().gc();

//		System.out.println("tf-log + feature selection");
//		ArrayList<ITransformer> tflogTransformer = new ArrayList<ITransformer>();
//		tflogTransformer.add(new FeatureWeighting(FeatureWeighting.TF_LOG, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE));
//		x(files, tflogTransformer);
//		Runtime.getRuntime().gc();

		System.out.println("tf-idf + feature selection");
		ArrayList<ITransformer> tfidfTransformer = new ArrayList<ITransformer>();
		tfidfTransformer.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE));
		x(files, tfidfTransformer);
		Runtime.getRuntime().gc();
	}

	public static void x(ArrayList<File> files, ArrayList<ITransformer> transformers) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedTF());
		filters.add(new FilterByRankedDF());
		filters.add(new FilterByRankedIG());
		filters.add(new FilterByRankedVariance());
		filters.add(new FilterByRankedL0Norm1());
		filters.add(new FilterByRankedL0Norm2());
		filters.add(new FilterByRankedFisher(FilterByRankedFisher.MINIMUM_SCORE));
		filters.add(new FilterByRankedFisher(FilterByRankedFisher.SUM_SCORE));
		filters.add(new FilterByRankedFisher(FilterByRankedFisher.SUM_SQUARED_SCORE));
		
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		classifiers.add(new NaiveBayesTrainer());
		
		int step = 10;
		int folds = 10;
		
		SEAMCE.x(files, transformers, filters, classifiers, step, folds);
	}
}
