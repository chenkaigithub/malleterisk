package main.executions;

import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedIG;
import ft.selection.methods.FilterByRankedVariance;
import ft.weighting.IWeighter;
import ft.weighting.methods.FeatureWeighting;
import imbalance.Balancer;
import imbalance.RandomSampler;
import imbalance.SMOTE;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import main.SEAMCE;
import utils.IteratedExecution2;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;

public class ExecImbalance {
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
		
		ArrayList<Balancer> balancers = new ArrayList<Balancer>();
		balancers.add(new RandomSampler(1)); // minimum threshold = 1
		balancers.add(new SMOTE(5, 1)); // k-nn = 5, minimum threshold = 1
		
		ArrayList<IWeighter> transformers = new ArrayList<IWeighter>();
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_LOG, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE));
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE));

		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedIG());
		filters.add(new FilterByRankedVariance());
		
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		classifiers.add(new NaiveBayesTrainer());
		
		int folds = 10;
		
		// iterate all collections
		for (File file : files) {
			// name prefix
			final String name = file.getName();
			System.out.println("+ processing " + name);
			
			InstanceList instances = InstanceList.load(file);

			// iterate all balancers
			for (Balancer balancer : balancers) {
				balancer.setInstances(instances);
				final String name2 = name + "+" + balancer.getClass().getSimpleName();
				
				// prepare the number of samples to be used in the balancing
				int[] samplePcts = IteratedExecution2.generatePercentages(10);
				samplePcts = new int[] { samplePcts[1], samplePcts[5], samplePcts[7], samplePcts[9] };
				for (int sp : samplePcts) System.out.println("sample %: " + sp);

				// setup number of features to be used in feature selection
				int[] featurePcts = IteratedExecution2.generatePercentages(10);
				featurePcts = Arrays.copyOfRange(featurePcts, 0, 3); // 10%, 20%, 30%
				for (int fp : featurePcts) System.out.println("feature %: " + fp);
				
				for (int ns : new IteratedExecution2(balancer.labeledInstances.getMaxNumInstances(), samplePcts)) {
					System.out.println("- balancing with " + name2 + ": " + ns);
					
					SEAMCE.z(
						name2 + "+" + ns, 
						balancer.balance(ns), 
						transformers, 
						filters, 
						classifiers,
						featurePcts, 
						folds
					);
				}
			}
		}
	}
}
