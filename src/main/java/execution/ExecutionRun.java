package execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import ft.selection.IFilter;
import ft.transformation.ITransformer;

// ATTN: this will waste your memory! not recommended!
public class ExecutionRun {
	public final String name;
	public final InstanceList instances;
	
	public final Collection<ITransformer> transformers;
	public final Collection<IFilter> filters;
	public final Collection<ClassifierTrainer<? extends Classifier>> classifiers;
	
	public final Collection<TransformedInstances> transformedInstances;
	public final Collection<FilteredInstances> filteredInstances;
	
	public final Collection<ExecutionResult> results;
	
	public ExecutionRun(File file) {
		this.name = file.getName();
		
		this.instances = InstanceList.load(file);
		
		this.transformers = new ArrayList<ITransformer>();
		this.filters = new ArrayList<IFilter>();
		this.classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		
		this.transformedInstances = new ArrayList<ExecutionRun.TransformedInstances>();
		this.filteredInstances = new ArrayList<ExecutionRun.FilteredInstances>();
		
		this.results = new ArrayList<ExecutionResult>();
	}
	
	public void run(int step, int folds) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		this.transform();
		this.filter(step);
		this.classify(folds);
		this.results();
	}
	
	public void transform() {
		for (ITransformer transformer : this.transformers)
			this.transformedInstances.add(new TransformedInstances(transformer, transformer.calculate(this.instances)));
	}
	
	public void filter(int step) {
		for (TransformedInstances tis : this.transformedInstances) {
			InstanceList transformedInstances = tis.instances;
			
			for (IFilter filter : this.filters) {
				FilteredInstances fi = new FilteredInstances(filter, tis.transformer);
				
				for (int n : new IteratedExecution(transformedInstances.getDataAlphabet().size(), step))
					fi.instances.put(n, filter.filter(n, transformedInstances));
				
				this.filteredInstances.add(fi);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void classify(int numFolds) throws InstantiationException, IllegalAccessException {
		for (ClassifierTrainer<? extends Classifier> trainer : this.classifiers) {
			for (FilteredInstances fi : this.filteredInstances) {
				ExecutionResult r = new ExecutionResult(this.name, fi.transformer.getDescription(), fi.filter.getDescription(), getClassifierDescription(trainer));
				for (Integer n : fi.instances.keySet()) {
					trainer = trainer.getClass().newInstance();
					
					r.trials.put(n, crossValidate(fi.instances.get(n), numFolds, trainer));
				}
				this.results.add(r);
			}
		}
	}
	
	public void results() throws FileNotFoundException {
		for (ExecutionResult r : this.results) {
			r.trial2out();
			r.accuracies2out();
		}
	}
	
	/**
	 * Performs cross validation to an instance list.
	 * Returns the results of all the runs in the form of trial objects.
	 * 
	 * @param instances
	 * @param numFolds
	 * @param trainer
	 * @return
	 */
	public static final Collection<Trial> crossValidate(InstanceList instances, int numFolds, ClassifierTrainer<?> trainer) {
		LinkedList<Trial> trials = new LinkedList<Trial>();
		
		CrossValidationIterator cvi = instances.crossValidationIterator(numFolds);
		InstanceList[] folds = null;
		Classifier classifier = null;
		while(cvi.hasNext()) {
			folds = cvi.next();
			
			/* TODO: operate here! 
			 * here's the information regarding number of instances in train/test
			 * how should i give back the info? wrap in an object?
			 * there should be info for each fold (10fold = 10 objects with #instances in train/test) 
			 */
			
			classifier = trainer.train(folds[0]);
			trials.add(new Trial(classifier, folds[1]));
		}
		
		return trials;
	}
	
	public static final String getClassifierDescription(ClassifierTrainer<? extends Classifier> classifier) {
		return classifier.getClass().getSimpleName();
	}
	
	//
	// Helper classes
	//
	
	class TransformedInstances {
		public final ITransformer transformer;
		public final InstanceList instances;
		
		public TransformedInstances(ITransformer t, InstanceList il) {
			this.transformer = t;
			this.instances = il;
		}
	}
	
	class FilteredInstances {
		public final IFilter filter;
		public final ITransformer transformer;
		public final Map<Integer, InstanceList> instances;
		
		public FilteredInstances(IFilter f, ITransformer t) {
			this.filter = f;
			this.transformer = t;
			this.instances = new HashMap<Integer, InstanceList>();
		}
	}
}
