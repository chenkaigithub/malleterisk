package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import pp.PreProcessor;
import analysis.ProcessorRun;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.Instance;
import data.IDataSet;
import ft.selection.methods.FilterByRankedDF;
import ft.selection.methods.FilterByRankedFisher;
import ft.selection.methods.FilterByRankedIG;
import ft.selection.methods.FilterByRankedL0Norm1;
import ft.selection.methods.FilterByRankedL0Norm2;
import ft.selection.methods.FilterByRankedVariance;
import ft.transformation.methods.NoTransformation;
import ft.transformation.methods.TDI;
import ft.transformation.methods.TFIDF;

/* 
 * TODO:
 * 3.
 * combater o desiquilibro das classes
 * 	variar o nœmero de classes
 * 	equilibrar o nœmero de documentos das classes 
 * 		reduzir o nœmero de documentos se necess‡rio (~100)
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
 * 
 * + must automatize all the experiments
 * - try out different users (use enron-flat, it's big enough)
 * - create classes that for each setup, write out results 
 * to somewhere so I can plot some graphs in matlab/etc 
 * 
 * + implement the remaining stuff
 * - analyzers
 * - different preprocessing setups
 * - need a decent nomenclature for the preprocessing
 * - preprocessors
 * - topic models
 * - classifiers
 * - integrate SVM into mallet (libsvm)
 * 
 * TODO: use a log-based approach! e.g.
 * LinkedList<String> operations = new LinkedList<String>();
 * operations.add("preprocessing: tokenization");
 * operations.add("preprocessing: part of speech tagging");
 * operations.add("..");
 * operations.add("feature transformation: tf idf");
 * operations.add("feature selection: ig, keeping xxx features");
 * operations.add("classification: naive bayes");
 * in the end, dump this with the results
 * must do this in some generic way, maybe with reflection?
 * must intercept values at constructor time, etc
 * AOP looks nice for this?
 * 
 * + test, test, test
 * + experiment, experiment, experiment
 */
public class SEAMCE {
	public static void main(String[] args) throws Exception {
		ArrayList<ProcessorRun> runs = new ArrayList<ProcessorRun>();
		runs.add(new ProcessorRun(new File("instances+0+0+bodies")));
		runs.add(new ProcessorRun(new File("instances+1+1+bodies")));
		runs.add(new ProcessorRun(new File("instances+1+2+bodies")));
		runs.add(new ProcessorRun(new File("instances+1+3+bodies")));
		runs.add(new ProcessorRun(new File("instances+1+4+bodies")));
		runs.add(new ProcessorRun(new File("instances+1+5+bodies")));
		runs.add(new ProcessorRun(new File("instances+1+6+bodies")));
		runs.add(new ProcessorRun(new File("instances+1+7+bodies")));
		runs.add(new ProcessorRun(new File("instances+2+1+bodies")));

		for (ProcessorRun run : runs) {
			// setup feature transformation
			run.transformers.add(new TFIDF());
			run.transformers.add(new TDI());
			run.transformers.add(new NoTransformation());
			
			// setup filter selection
			run.filters.add(new FilterByRankedDF());
			run.filters.add(new FilterByRankedIG());
			run.filters.add(new FilterByRankedVariance());
			run.filters.add(new FilterByRankedL0Norm1());
			run.filters.add(new FilterByRankedL0Norm2());
			run.filters.add(new FilterByRankedFisher(FilterByRankedFisher.MINIMUM_SCORE));
			run.filters.add(new FilterByRankedFisher(FilterByRankedFisher.SUM_SCORE));
			run.filters.add(new FilterByRankedFisher(FilterByRankedFisher.SUM_SQUARED_SCORE));
			
			// setup classifiers
			run.classifiers.add(new NaiveBayesTrainer());
			
			// run
			run.run(10, 10);
		}
		
		
		
		
		
//		ProcessorRun pr1 = new ProcessorRun(new File("instances+1+1+bodies"));
//		
//		// setup feature transformation
//		pr1.transformers.add(new TFIDF());
//		pr1.transformers.add(new TDI());
//		pr1.transformers.add(new NoTransformation());
//		
//		// setup filter selection
//		pr1.filters.add(new FilterByRankedDF());
//		pr1.filters.add(new FilterByRankedIG());
//		pr1.filters.add(new FilterByRankedVariance());
//		pr1.filters.add(new FilterByRankedL0Norm1());
//		pr1.filters.add(new FilterByRankedL0Norm2());
//		pr1.filters.add(new FilterByRankedFisher(FilterByRankedFisher.MINIMUM_SCORE));
//		pr1.filters.add(new FilterByRankedFisher(FilterByRankedFisher.SUM_SCORE));
//		pr1.filters.add(new FilterByRankedFisher(FilterByRankedFisher.SUM_SQUARED_SCORE));
//		
//		// setup classifiers
//		pr1.classifiers.add(new NaiveBayesTrainer());
//		
//		// run
//		pr1.run(10, 10);
	}
	
	
	
	private static final void db2file() {
//		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
//		for (int collectionId : dal.getCollections()) {
//			for (int userId : dal.getUsers(collectionId)) {
//				// get dataset of specified collection and user
//				IDataSet ds = new EnronDbDataSet(dal, collectionId, userId);
//				
//				// preprocess data from the dataset into instance lists (aka preprocessors)
//				ArrayList<PreProcessor> preprocessors = new ArrayList<PreProcessor>();
//				preprocessors.add(new SubjectPreProcessor1());
//				preprocessors.add(new BodyPreProcessor1());
//				preprocessors.add(new DatePreProcessor1());
//				preprocessors.add(new ParticipantsPreProcessor1());
//				Collection<PreProcessor> instanceLists = preprocess(ds, preprocessors);
//				
//				// save into files of the following format: instances+collection+user+field+preprocessing
//				for (PreProcessor instanceList : instanceLists)
//					instanceList.save(new File(String.format("instances+%d+%d+%s", collectionId, userId, instanceList.getDescription())));
//			}
//		}
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
	
	/*
//		System.out.println("1-1");
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 1, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 1, "bodies")))).toString());
//		System.out.println("1-2");
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 2, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 2, "bodies")))).toString());
//		System.out.println("1-3");
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 3, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 3, "bodies")))).toString());
//		System.out.println("1-4");
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 4, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 4, "bodies")))).toString());
//		System.out.println("1-5");
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 5, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 5, "bodies")))).toString());
//		System.out.println("1-6");
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 6, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 6, "bodies")))).toString());
//		System.out.println("1-7");
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 7, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format("instances_%d-%d_%s", 1, 7, "bodies")))).toString());	
	 */
}
