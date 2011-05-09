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

import utils.IteratedExecution;

import main.SEAMCE;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;

public class ExecImbalance {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+6+subjects"));
//		files.add(new File("instances+1+1+bodies"));
//		files.add(new File("instances+1+2+bodies"));
//		files.add(new File("instances+1+3+bodies"));
//		files.add(new File("instances+1+4+bodies"));
//		files.add(new File("instances+1+5+bodies"));
//		files.add(new File("instances+1+6+bodies"));
//		files.add(new File("instances+1+7+bodies"));
//		files.add(new File("instances+2+1+bodies"));
		
		ArrayList<Balancer> balancers = new ArrayList<Balancer>();
		balancers.add(new RandomSampler(5));
		balancers.add(new SMOTE(5));
		
		ArrayList<IWeighter> transformers = new ArrayList<IWeighter>();
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_LOG, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE));
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE));

		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedIG());
		filters.add(new FilterByRankedVariance());
		
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		classifiers.add(new NaiveBayesTrainer());
		
		int step = 10;
		int folds = 10;
		
		for (File file : files) {
			String name = file.getName();
			System.out.println("+ processing " + name);
			InstanceList instances = InstanceList.load(file);
			for (Balancer balancer : balancers) {
				balancer.setInstances(instances);
				name += "+" + balancer.getClass().getSimpleName();
				IteratedExecution itex = new IteratedExecution(instances.size(), 10);
				for (Integer n : itex) {
					System.out.println("- balancing with " + name + ": " + n);
					SEAMCE.y(name + "+" + n, balancer.balance(n), transformers, filters, classifiers, step, folds);
					break;
				}
			}
		}
	}
}
