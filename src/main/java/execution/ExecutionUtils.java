package execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import pp.PreProcessor;
import types.mallet.classify.ExtendedTrial;
import utils.IteratedExecution;
import utils.ParametrizedIteratedExecution;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import data.loader.IDataSetLoader;
import ft.selection.IFilter;
import ft.weighting.IWeighter;

/**
 * Helper class with several functions for processing the data.
 * 
 * @author tt
 */
public class ExecutionUtils {
	//
	// Preprocessing
	//
		
	// preprocess the instances of the given dataset through the given preprocessors
	public static final Collection<PreProcessor> preprocess(IDataSetLoader ds, Collection<PreProcessor> preprocessors) {
		while(ds.hasNext())
			for (PreProcessor pp : preprocessors)
				pp.addThruPipe(ds.next());
		
		return preprocessors;
	}
	
	//
	// Processing
	//
	
	// TODO: ARRRRGGGGGGGGHHHHHHHHHHHHHHH
	
	// iterates over the IWeighters, IFilters and classifiers and issues a single execution order.
	public static final void runWeightersFiltersClassifiers(
		ArrayList<File> files, 
		ArrayList<IWeighter> transformers, 
		ArrayList<IFilter> filters, 
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers, 
		int step, 
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		for (File file : files) {
			String filename = file.getName();
			System.out.println("+ processing " + filename);
			InstanceList instances = InstanceList.load(file);
			for (IWeighter transformer : transformers) {
				System.out.println("- transformer: " + transformer.getDescription());
				for (IFilter filter : filters) {
					System.out.println("- filter: " + filter.getDescription());
					for (ClassifierTrainer<? extends Classifier> trainer : classifiers) {
						run(filename, instances, transformer, filter, trainer, step, folds);
					}
				}
			}
		}
	}
	
	// uses custom steps in filtering
	public static final void runWeightersFiltersWithCustomStepClassifiers(
		String runName,
		InstanceList instances, 
		ArrayList<IWeighter> transformers, 
		ArrayList<IFilter> filters, 
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers, 
		int[] ns, 
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		for (IWeighter transformer : transformers) {
			System.out.println("- transformer: " + transformer.getDescription());
			for (IFilter filter : filters) {
				System.out.println("- filter: " + filter.getDescription());
				for (ClassifierTrainer<? extends Classifier> trainer : classifiers) {
					runWithCustomStep(runName, instances, transformer, filter, trainer, ns, folds);
				}
			}
		}
	}
	
	// transforms, filters (with several feature selection levels) and classifies with cross-validation the given instances
	// results are written out to pre-specified files
	public static final void run(
		String name, 
		InstanceList instances, 
		IWeighter transformer, 
		IFilter filter, 
		ClassifierTrainer<? extends Classifier> trainer,
		int step, 
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ExecutionResult r = new ExecutionResult(name, transformer.getDescription(), filter.getDescription(), ExecutionUtils.getClassifierDescription(trainer));
		
		InstanceList transformedInstances = transformer.calculate(instances);
		for (int n : new IteratedExecution(transformedInstances.getDataAlphabet().size(), step)) {
			InstanceList filteredInstances = filter.filter(n, transformedInstances);
			
			// classifier trainer must be a new instance since it might accumulate the previous alphabet
			// associate the trials to the run with 'n' features
			r.trials.put(n, ExecutionUtils.crossValidate(filteredInstances, folds, trainer.getClass().newInstance()));
		}
		
		// write results
		r.outputTrials();
		r.outputAccuracies();
	}
	
	// Used when custom steps (int[] ns) are needed in the feature selection stage.
	// Everything else is identical to sequentialRun.
	public static final void runWithCustomStep(
		String name, 
		InstanceList instances, 
		IWeighter transformer, 
		IFilter filter, 
		ClassifierTrainer<? extends Classifier> trainer,
		int[] ns, 
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ExecutionResult r = new ExecutionResult(name, transformer.getDescription(), filter.getDescription(), ExecutionUtils.getClassifierDescription(trainer));
		
		InstanceList transformedInstances = transformer.calculate(instances);
		for (int n : new ParametrizedIteratedExecution(transformedInstances.getDataAlphabet().size(), ns)) {
			InstanceList filteredInstances = filter.filter(n, transformedInstances);
			
			// classifier trainer must be a new instance since it might accumulate the previous alphabet
			// associate the trials to the run with 'n' features
			r.trials.put(n, ExecutionUtils.crossValidate(filteredInstances, folds, trainer.getClass().newInstance()));
		}
		
		// write results
		r.outputTrials();
		r.outputAccuracies();
	}
	
	//
	// Cross Validation
	//
	
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
		InstanceList trainInstances, testInstances;
		int foldCounter = 0;
		while(cvi.hasNext()) {
			folds = cvi.next();
			
			trainInstances = folds[0];
			testInstances = folds[1];
			
			trials.add(new ExtendedTrial(
				trainer.train(trainInstances), // the classifier 
				trainInstances, 
				testInstances, 
				foldCounter++
			));
		}
		
		return trials;
	}
	
	public static final String getClassifierDescription(ClassifierTrainer<? extends Classifier> classifier) {
		return classifier.getClass().getSimpleName();
	}
}
