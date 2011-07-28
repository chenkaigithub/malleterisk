package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import pp.PreProcessor;
import pp.email.body.BodyPreProcessor1;
import pp.email.date.DatePreProcessor1;
import pp.email.participants.ParticipantsPreProcessor1;
import pp.email.subject.SubjectPreProcessor1;
import utils.IteratedExecution;
import utils.IteratedExecution2;
import analysis.CollectionAnalysis;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import data.IDataSet;
import data.db.DbConnector;
import data.db.DbDataAccess;
import data.enron.EnronDbDataSet;
import execution.ExecutionResult;
import execution.ExecutionRun;
import ft.selection.IFilter;
import ft.weighting.IWeighter;

/* 
 * + automation
 * this could be cleaned up using dependency injection
 * create some class that represents the processing (kind of like ProcessorRun)
 * and apply processing over the passed files
 * 
 * 0.
 * things that I want to know when executing:
 * number of instances of each class used in train and in test
 * logging seems like a great idea..
 * 
 * X.
 * participants:
 * 	treat as a feature selection problem
 * 	documents x participants incidence matrix
 * 	apply feature selection methods
 * 
 * Y.
 * pearson correlation
 * mRMR (Minimum Redundancy Maximum Relevance)
 * feature clustering
 * 	find the most similar features (cosine similarity)
 * 	remove least relevant features from each cluster
 * 
 * Z.
 * + tests
 * - unit test for every implementation
 * - test everything in detail
 * - must make sure everything is correctly implemented
 * - try out different users (use enron-flat, it's big enough)
 * 
 * + implement the remaining stuff
 * - analyzers
 * - different preprocessing setups
 * - need a decent nomenclature for the preprocessing
 * - preprocessors
 * - topic models
 * - classifiers
 * - integrate SVM into mallet (libsvm)
 * svm, knn, trees
 */
public class SEAMCE {
	public static void main(String[] args) {
		// do stuff
	}
	
	// ------------------------------------------------------------------------
	
	public static final void analyze() {
		System.out.println("1-1");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+1+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+1+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+1+bodies"))).toString());
		System.out.println("1-2");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+2+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+2+bodies"))).toString());
		System.out.println("1-3");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+3+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+3+bodies"))).toString());
		System.out.println("1-4");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+4+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+4+bodies"))).toString());
		System.out.println("1-5");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+5+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+5+bodies"))).toString());
		System.out.println("1-6");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+6+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+6+bodies"))).toString());
		System.out.println("1-7");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+7+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+1+7+bodies"))).toString());
		System.out.println("2-1");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+2+1+subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances+2+1+bodies"))).toString());
	}
	
	// this method just iterates over the files and deserializes the InstanceLists.
	public static final void x(
		ArrayList<File> files, 
		ArrayList<IWeighter> transformers, 
		ArrayList<IFilter> filters, 
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers, 
		int step, 
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		for (File file : files) {
			System.out.println("+ processing " + file.getName());
			InstanceList instances = InstanceList.load(file);
			y(file.getName(), instances, transformers, filters, classifiers, step, folds);
		}
	}
	
	// iterates over the IWeighters, IFilters and classifiers and issues a single execution order.
	public static final void y(
		String runName,
		InstanceList instances, 
		ArrayList<IWeighter> transformers, 
		ArrayList<IFilter> filters, 
		ArrayList<ClassifierTrainer<? extends Classifier>> classifiers, 
		int step, 
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		for (IWeighter transformer : transformers) {
			System.out.println("- transformer: " + transformer.getDescription());
			for (IFilter filter : filters) {
				System.out.println("- filter: " + filter.getDescription());
				for (ClassifierTrainer<? extends Classifier> trainer : classifiers) {
					SEAMCE.sequentialRun(runName, instances, transformer, filter, trainer, step, folds);
					Runtime.getRuntime().gc();
				}
			}
		}
	}
	
	// Used by class imbalance. Uses custom filtering.
	public static final void z(
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
					SEAMCE.sequentialRunCustomFiltering(runName, instances, transformer, filter, trainer, ns, folds);
					Runtime.getRuntime().gc();
				}
			}
		}
	}
	
	// transforms, filters (with several feature selection levels) and classifies with cross-validation the given instances
	// results are written out to pre-specified files
	public static final void sequentialRun(
		String name, 
		InstanceList instances, 
		IWeighter transformer, 
		IFilter filter, 
		ClassifierTrainer<? extends Classifier> trainer,
		int step, 
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ExecutionResult r = new ExecutionResult(name, transformer.getDescription(), filter.getDescription(), ExecutionRun.getClassifierDescription(trainer));
		
		InstanceList transformedInstances = transformer.calculate(instances);
		for (int n : new IteratedExecution(transformedInstances.getDataAlphabet().size(), step)) {
			InstanceList filteredInstances = filter.filter(n, transformedInstances);
			
			// classifier trainer must be a new instance since it might accumulate the previous alphabet
			// associate the trials to the run with 'n' features
			r.trials.put(n, ExecutionRun.crossValidate(filteredInstances, folds, trainer.getClass().newInstance()));
		}
		
		// write results
		r.trial2out();
		r.accuracies2out();
	}
	
	// Used when custom steps (int[] ns) are needed in the feature selection stage.
	// Everything else is identical to sequentialRun.
	public static final void sequentialRunCustomFiltering(
		String name, 
		InstanceList instances, 
		IWeighter transformer, 
		IFilter filter, 
		ClassifierTrainer<? extends Classifier> trainer,
		int[] ns, 
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ExecutionResult r = new ExecutionResult(name, transformer.getDescription(), filter.getDescription(), ExecutionRun.getClassifierDescription(trainer));
		
		InstanceList transformedInstances = transformer.calculate(instances);
		for (int n : new IteratedExecution2(transformedInstances.getDataAlphabet().size(), ns)) {
			InstanceList filteredInstances = filter.filter(n, transformedInstances);
			
			// classifier trainer must be a new instance since it might accumulate the previous alphabet
			// associate the trials to the run with 'n' features
			r.trials.put(n, ExecutionRun.crossValidate(filteredInstances, folds, trainer.getClass().newInstance()));
		}
		
		// write results
		r.trial2out();
		r.accuracies2out();
	}
	
	// stores email data from database to preprocessed files
	public static final void db2file() throws SQLException {
		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
		for (int collectionId : dal.getCollections()) {
			for (int userId : dal.getUsers(collectionId)) {
				// get dataset of specified collection and user
				IDataSet ds = new EnronDbDataSet(dal, collectionId, userId);
				
				// preprocess data from the dataset into instance lists (aka preprocessors)
				ArrayList<PreProcessor> preprocessors = new ArrayList<PreProcessor>();
				preprocessors.add(new SubjectPreProcessor1());
				preprocessors.add(new BodyPreProcessor1());
				preprocessors.add(new DatePreProcessor1());
				preprocessors.add(new ParticipantsPreProcessor1());
				Collection<PreProcessor> instanceLists = preprocess(ds, preprocessors);
				
				// save into files of the following format: instances+collection+user+field+preprocessing
				for (PreProcessor instanceList : instanceLists)
					instanceList.save(new File(String.format("instances+%d+%d+%s", collectionId, userId, instanceList.getDescription())));
			}
		}
	}
	
	// preprocess the instances of the given dataset through the given preprocessors
	public static final Collection<PreProcessor> preprocess(IDataSet ds, Collection<PreProcessor> preprocessors) {
		Instance instance;
		while(ds.hasNext()) {
			instance = ds.next();
			
			for (PreProcessor pp : preprocessors)
				pp.addThruPipe(instance);
		}
		
		return preprocessors;
	}
}
