package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import analysis.CollectionAnalysis;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import execution.ExecutionResult;
import execution.ExecutionRun;
import execution.IteratedExecution;
import execution.OneVersusAll;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedDF;
import ft.selection.methods.FilterByRankedFisher;
import ft.selection.methods.FilterByRankedIG;
import ft.selection.methods.FilterByRankedL0Norm1;
import ft.selection.methods.FilterByRankedL0Norm2;
import ft.selection.methods.FilterByRankedVariance;
import ft.transformation.ITransformer;
import ft.transformation.methods.NoTransformation;
import ft.transformation.methods.TDI;
import ft.transformation.methods.TFIDF;

/* 
 * + automation
 * this could be cleaned up using dependency injection
 * create some class that represents the processing (kind of like ProcessorRun)
 * and apply processing over the passed files
 * 
 * 3.
 * combater o desiquilibro das classes
 * 	variar o n�mero de classes
 * 	equilibrar o n�mero de documentos das classes 
 * 		reduzir o n�mero de documentos se necess�rio (~100)
 * 	one class classifiers
 * 	one vs all, all vs all
 * 
 * 5.
 * participants:
 * 	treat as a feature selection problem
 * 	documents x participants incidence matrix
 * 	apply feature selection methods
 * 
 * 6.
 * pearson correlation
 * mRMR (Minimum Redundancy Maximum Relevance)
 * feature clustering
 * 	find the most similar features (cosine similarity)
 * 	remove least relevant features from each cluster
 * 
 * X.
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
 */
public class SEAMCE {
	public static void main(String[] args) throws Exception {
		InstanceList instances = InstanceList.load(new File("instances+2+1+bodies"));
		
		OneVersusAll ova = new OneVersusAll(instances);
		while(ova.hasNext()) {
			instances = ova.next();
			
			int step = 10;
			int folds = 10;
			
			ITransformer transformer = new TFIDF();
			IFilter filter = new FilterByRankedDF();
			ClassifierTrainer<? extends Classifier> trainer = new NaiveBayesTrainer();
			
			sequentialRun("instances+2+1+bodies", instances, transformer, filter, trainer, step, folds);
		}
	}
	
	public static final void classify() throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
//		files.add(new File("instances+0+0+tests"));
		files.add(new File("instances+1+1+bodies"));
		files.add(new File("instances+1+2+bodies"));
		files.add(new File("instances+1+3+bodies"));
		files.add(new File("instances+1+4+bodies"));
		files.add(new File("instances+1+5+bodies"));
		files.add(new File("instances+1+6+bodies"));
		files.add(new File("instances+1+7+bodies"));
		files.add(new File("instances+2+1+bodies"));
		
		ArrayList<ITransformer> transformers = new ArrayList<ITransformer>();
		transformers.add(new TFIDF());
		transformers.add(new TDI());
		transformers.add(new NoTransformation());
		
		ArrayList<IFilter> filters = new ArrayList<IFilter>();
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
		
		for (File file : files) {
			InstanceList instances = InstanceList.load(file);
			for (ITransformer transformer : transformers) {
				for (IFilter filter : filters) {
					for (ClassifierTrainer<? extends Classifier> trainer : classifiers) {
						sequentialRun(file.getName(), instances, transformer, filter, trainer, step, folds);
					}
				}
			}
		}
	}
	
	public static final void analyze() {
		System.out.println("1-1");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-1_subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-1_bodies"))).toString());
		System.out.println("1-2");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-2_subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-2_bodies"))).toString());
		System.out.println("1-3");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-3_subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-3_bodies"))).toString());
		System.out.println("1-4");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-4_subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-4_bodies"))).toString());
		System.out.println("1-5");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-5_subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-5_bodies"))).toString());
		System.out.println("1-6");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-6_subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-6_bodies"))).toString());
		System.out.println("1-7");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-7_subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_1-7_bodies"))).toString());
		System.out.println("2-1");
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_2-1_subjects"))).toString());
		System.out.println(new CollectionAnalysis(InstanceList.load(new File("instances_2-1_bodies"))).toString());
	}
	
	// ------------------------------------------------------------------------
	
	public static final void sequentialRun(String name, InstanceList instances, ITransformer transformer, 
			IFilter filter, ClassifierTrainer<? extends Classifier> trainer, 
			int step, int folds) throws FileNotFoundException, InstantiationException, IllegalAccessException 
	{
		ExecutionResult r = new ExecutionResult(name, transformer.getDescription(), filter.getDescription(), ExecutionRun.getClassifierDescription(trainer));
		
		InstanceList transformedInstances = transformer.calculate(instances);
		for (int n : new IteratedExecution(transformedInstances.getDataAlphabet().size(), step)) {
			InstanceList filteredInstances = filter.filter(n, transformedInstances);
			
			// classifier trainer must be a new instance since it might accumulate the previous alphabet 
			r.trials.put(n, ExecutionRun.crossValidate(filteredInstances, folds, trainer.getClass().newInstance()));
		}
		
		r.trial2out();
		r.accuracies2out();
	}
	
	/*
	private static final void db2file() throws SQLException {
		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
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
	
	private static final Collection<PreProcessor> preprocess(IDataSet ds, Collection<PreProcessor> preprocessors) {
		Instance instance;
		while(ds.hasNext()) {
			instance = ds.next();
			
			for (PreProcessor pp : preprocessors)
				pp.addThruPipe(instance);
		}
		
		return preprocessors;
	}
	*/
}
