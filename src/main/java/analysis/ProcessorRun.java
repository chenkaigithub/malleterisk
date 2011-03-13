package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import utils.IteratedExecution;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import cc.mallet.types.Labeling;
import ft.selection.IFilter;
import ft.transformation.ITransformer;

public class ProcessorRun {
	public final String name;
	public final InstanceList instances;
	
	public final Collection<ITransformer> transformers;
	public final Collection<IFilter> filters;
	public final Collection<ClassifierTrainer<? extends Classifier>> classifiers;
	
	public final Collection<TransformedInstances> transformedInstances;
	public final Collection<FilteredInstances> filteredInstances;
	
	public final Collection<Result> results;
	
	public ProcessorRun(File file) {
		this.name = file.getName();
		
		this.instances = InstanceList.load(file);
		
		this.transformers = new ArrayList<ITransformer>();
		this.filters = new ArrayList<IFilter>();
		this.classifiers = new ArrayList<ClassifierTrainer<? extends Classifier>>();
		
		this.transformedInstances = new ArrayList<ProcessorRun.TransformedInstances>();
		this.filteredInstances = new ArrayList<ProcessorRun.FilteredInstances>();
		
		this.results = new ArrayList<ProcessorRun.Result>();
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
				Result r = new Result(fi.transformer.getDescription(), fi.filter.getDescription(), getClassifierDescription(trainer));
				for (Integer n : fi.instances.keySet()) {
					trainer = trainer.getClass().newInstance();
					
					r.trials.put(n, crossValidate(fi.instances.get(n), numFolds, trainer));
				}
				this.results.add(r);
			}
		}
	}
	
	public void results() throws FileNotFoundException {
		for (Result r : this.results) {
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
	private static final Collection<Trial> crossValidate(InstanceList instances, int numFolds, ClassifierTrainer<?> trainer) {
		LinkedList<Trial> trials = new LinkedList<Trial>();
		
		CrossValidationIterator cvi = instances.crossValidationIterator(numFolds);
		InstanceList[] folds = null;
		Classifier classifier = null;
		while(cvi.hasNext()) {
			folds = cvi.next();
			classifier = trainer.train(folds[0]);
			trials.add(new Trial(classifier, folds[1]));
		}
		
		return trials;
	}
	
	private static final String getClassifierDescription(ClassifierTrainer<? extends Classifier> classifier) {
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
	
	class Result {
		public String transformer;
		public String filter;
		public String classifier;
		
		public Map<Integer, Collection<Trial>> trials;
		
		public Result(String t, String f, String c) {
			this.transformer = t;
			this.filter = f;
			this.classifier = c;
			this.trials = new HashMap<Integer, Collection<Trial>>();
		}
		
		/**
		 * Writes the results of the classification into the output stream in a formatted manner.
		 * (instance, real_class_idx, real_class, class_n1_idx, class_n1, class_n1_val, ..., class_nK_idx, class_nK, class_nK_val)
		 */
		public void trial2out() throws FileNotFoundException {
			Instance instance;
			Labeling labeling;
			
			for (Integer n : this.trials.keySet()) {
				for (Trial trial : this.trials.get(n)) {
					FileOutputStream out = new FileOutputStream(getTrialOutName());
					PrintWriter pw = new PrintWriter(out);
					
					for (Classification classification : trial) {
						// write out results in the form of:
						// instance, real_class_idx, real_class, class_n1_idx, class_n1, val_n1, ..., class_nK_idx, class_nK, val_nK
						// class_nN = class classified at position N
						// val_nN = value of class at position N
						
						// instance
						instance = classification.getInstance();
						pw.write(instance.getName() + ", ");
						
						// real class
						pw.write(instance.getLabeling().getBestIndex() + ", ");
						pw.write(instance.getLabeling().getBestLabel() + ", ");
						
						// pairs of class_nN, val_nN
						labeling = classification.getLabeling();
						int nl = labeling.numLocations();
						for(int i=0; i < nl; ++i) {
							pw.write(labeling.indexAtLocation(i) + ", ");
							pw.write(labeling.labelAtLocation(i) + ", ");
							pw.write(String.valueOf(labeling.valueAtLocation(i)));
							
							if(i+1 < nl) pw.write(", ");
						}
						pw.write('\n');
					}
					
					pw.flush();
					pw.close();
				}
			}
		}
		
		/**
		 * Writes the accuracies of the trials into a file, in a formatted manner.
		 * (number_of_features, trial1_accuracy, trial2_accuracy, ..., trialN_accuracy)
		 * 
		 * Used to plot feature selection/classification accuracy graph.
		 * 
		 */
		public void accuracies2out() throws FileNotFoundException {
			FileOutputStream out = new FileOutputStream(getAccuraciesOutName());
			PrintWriter pw = new PrintWriter(out);
			
			for (Integer n : this.trials.keySet()) {
				pw.write(n.intValue());
				pw.write(", ");
				int i = 0;
				for (Trial trial : this.trials.get(n)) {
					pw.write(String.valueOf(trial.getAccuracy()));
					if(i++ < trials.size()) pw.write(", ");
				}
				pw.write('\n');
			}
			
			pw.flush();
			pw.close();
		}
		
		private String getTrialOutName() {
			StringBuffer sb = new StringBuffer();
			sb.append("trial");
			sb.append("+");
			sb.append(ProcessorRun.this.name);
			sb.append("+");
			sb.append(transformer);
			sb.append("+");
			sb.append(filter);
			sb.append("+");
			sb.append(classifier);
			sb.append("+");
			sb.append(new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date()));
			
			return sb.toString();
		}
		
		private String getAccuraciesOutName() {
			StringBuffer sb = new StringBuffer();
			sb.append("accuracies");
			sb.append("+");
			sb.append(ProcessorRun.this.name);
			sb.append("+");
			sb.append(transformer);
			sb.append("+");
			sb.append(filter);
			sb.append("+");
			sb.append(classifier);
			sb.append("+");
			sb.append(new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date()));
			
			return sb.toString();
		}
	}
}
