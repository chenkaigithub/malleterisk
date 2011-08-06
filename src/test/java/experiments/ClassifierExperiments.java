package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

import struct.classification.KBestMiraClassifierTrainer;

import classifiers.LibLinearTrainer;

import cc.mallet.classify.BalancedWinnowTrainer;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.DecisionTreeTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.classify.WinnowTrainer;
import cc.mallet.types.InstanceList;

public class ClassifierExperiments {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		InstanceList instances = InstanceList.load(new File("instances+1+1+bodies"));
		InstanceList[] folds = instances.split(new Random(2), new double[] {0.5, 0.5});
		ClassifierTrainer<? extends Classifier> classifier;
		
		classifier = new DecisionTreeTrainer();
		System.out.println("DecisionTree: " + new Trial(classifier.train(folds[0]), folds[1]).getAccuracy());
		
		classifier = new NaiveBayesTrainer();
		System.out.println("Naive Bayes: " + new Trial(classifier.train(folds[0]), folds[1]).getAccuracy());

		classifier = new WinnowTrainer();
		System.out.println("Winnow: " + new Trial(classifier.train(folds[0]), folds[1]).getAccuracy());
		
		classifier = new BalancedWinnowTrainer();
		System.out.println("Balanced Winnow: " + new Trial(classifier.train(folds[0]), folds[1]).getAccuracy());
		
		classifier = new LibLinearTrainer();
		System.out.println("LibLinear: " + new Trial(classifier.train(folds[0]), folds[1]).getAccuracy());
		
		classifier = new KBestMiraClassifierTrainer(1);
		System.out.println("K-Best MIRA: " + new Trial(classifier.train(folds[0]), folds[1]).getAccuracy());
		
		classifier = new MaxEntTrainer();
		System.out.println("Maximum Entropy: " + new Trial(classifier.train(folds[0]), folds[1]).getAccuracy());
	}
}
