package execution;

import java.util.Collection;
import java.util.LinkedList;

import types.mallet.classify.ExtendedTrial;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;

public class ExecutionRun {
	/**
	 * Performs cross validation to an instance list.
	 * Returns the results of all the runs in the form of trial objects.
	 * 
	 * @param instances
	 * @param numFolds
	 * @param trainer
	 * @return
	 */
	public static final Collection<ExtendedTrial> crossValidate(InstanceList instances, int numFolds, ClassifierTrainer<?> trainer) {
		LinkedList<ExtendedTrial> trials = new LinkedList<ExtendedTrial>();
		
		CrossValidationIterator cvi = instances.crossValidationIterator(numFolds);
		InstanceList[] folds = null;
		Classifier classifier = null;
		InstanceList trainInstances, testInstances;
		int foldCounter = 0;
		while(cvi.hasNext()) {
			folds = cvi.next();
			
			trainInstances = folds[0];
			testInstances = folds[1];
			
			classifier = trainer.train(trainInstances);
			trials.add(new ExtendedTrial(classifier, trainInstances, testInstances, foldCounter++));
		}
		
		return trials;
	}
	
	public static final String getClassifierDescription(ClassifierTrainer<? extends Classifier> classifier) {
		return classifier.getClass().getSimpleName();
	}
}
