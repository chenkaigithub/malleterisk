package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import pp.email.body.BodyPreProcessor1;
import pp.email.subject.SubjectPreProcessor1;
import utils.IteratedExecution;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.FeatureConjunction.List;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import cc.mallet.types.Labeling;
import data.IDataSet;
import fs.Filter;
import fs.IFilter;
import fs.methods.FilterByRankedDF;
import fs.methods.FilterByRankedVariance;
import fs.methods.TFIDF;

/* 
 * TODO:
 * 
 * 0.
 * streamline everything into big pipelines
 * of processing
 * 
 * 1.
 * no feature selection? não é possível recriar mesmo comportamento
 * convém saber a percentagem das features
 * penso que não está a classificar para 100% (sem fs)
 * 
 * 2.
 * matriz de confusão e gráficos
 * matriz de confusão com número de documentos / classe
 * gráficos da performance do naive bayes com diferentes métodos de fs
 * 
 * 3.
 * combater o desiquilibro das classes
 * 	variar o número de classes
 * 	equilibrar o número de documentos das classes 
 * 		reduzir o número de documentos se necessário (~100)
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
 * 
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
//	private static final String SRC_FILENAME_FORMAT = "instances_%d-%d_%s";
//	private static final String DST_FILENAME_FORMAT = "collection%d+user%d+%s+%s+%s+%s+%s";
//	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
	
	public static void main(String[] args) throws Exception {
		InstanceList instances = InstanceList.load(new File("instances_0-0_tests"));
		int m = instances.getDataAlphabet().size();
		IFilter fs = new FilterByRankedDF(TFIDF.tfidf(instances));
		InstanceList newInstances = fs.filter(m);
		
		for (Instance instance : newInstances) {
			System.out.println(instance.getName() + "|" + instance.getTarget());
		}
		
//		InstanceList[] instancesSplit = newInstances.split(new double[] {0.5, 0.5});
//		ClassifierTrainer<?> ct = new NaiveBayesTrainer();
//		Classifier c = ct.train(instancesSplit[0]);
//		ArrayList<Classification> classifications = c.classify(instancesSplit[1]);
//		for (Classification classification : classifications) {
//			System.out.println(classification.getInstance().getName());
//			Labeling labeling = classification.getLabeling();
//			for(int i=0; i < labeling.numLocations(); ++i) {
//				System.out.println(labeling.labelAtLocation(i) + ", " + labeling.valueAtLocation(i));
//			}
//		}
//		
		
		Collection<Trial> trials = crossValidate(newInstances, 3, new NaiveBayesTrainer());
		
		for (Trial trial : trials) {
			trial2file(trial, System.out);
		}
	}
	
	
	
	/*
		//
		// COLLECTION PREPROCESSING
		//
		
//		preprocessToFile();
		//
		// COLLECTION ANALYSIS
		//
		
//		System.out.println("1-1");
////		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 1, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 1, "bodies")))).toString());
//		
//		System.out.println("1-2");
////		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 2, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 2, "bodies")))).toString());
//		
//		System.out.println("1-3");
////		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 3, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 3, "bodies")))).toString());
//		
//		System.out.println("1-4");
////		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 4, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 4, "bodies")))).toString());
//		
//		System.out.println("1-5");
////		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 5, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 5, "bodies")))).toString());
//		
//		System.out.println("1-6");
////		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 6, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 6, "bodies")))).toString());
//		
//		System.out.println("1-7");
////		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 7, "subjects")))).toString());
//		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 7, "bodies")))).toString());	

		//
		// COLLECTION PROCESSING
		//
		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 1, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 1, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 1, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 1, "bodies", DATE_FORMAT.format(new Date())));
//		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 2, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 2, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 2, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 2, "bodies", DATE_FORMAT.format(new Date())));
//
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 3, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 3, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 3, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 3, "bodies", DATE_FORMAT.format(new Date())));
//		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 4, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 4, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 4, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 4, "bodies", DATE_FORMAT.format(new Date())));
//		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 5, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 5, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 5, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 5, "bodies", DATE_FORMAT.format(new Date())));
//		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 6, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 6, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 6, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 6, "bodies", DATE_FORMAT.format(new Date())));
//		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 7, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 7, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 7, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 7, "bodies", DATE_FORMAT.format(new Date())));
	 */
		
	public static final void preprocessToFile(IDataSet ds) throws SQLException {
//		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
//		int collectionId = 1;
		
//		for (int userId : dal.getUsers(collectionId)) {
//			IDataSet ds = new EnronDbDataSet(dal, collectionId, userId);
			
			InstanceList subjects = new SubjectPreProcessor1();
			InstanceList bodies = new BodyPreProcessor1();
//			InstanceList dates = new DatePreProcessor1();
//			InstanceList participants = new ParticipantsPreProcessor1();		
			for (Instance instance : ds) {
				subjects.addThruPipe(instance);
				bodies.addThruPipe(instance);
//				dates.addThruPipe(instance);
//				participants.addThruPipe(instance);
			}
			
//			subjects.save(new File(String.format(SRC_FILENAME_FORMAT, collectionId, userId, "subjects")));
//			bodies.save(new File(String.format(SRC_FILENAME_FORMAT, collectionId, userId, "bodies")));
//			dates.save(new File(String.format(SRC_FILENAME_FORMAT, collectionId, userId, "dates")));
//			participants.save(new File(String.format(SRC_FILENAME_FORMAT, collectionId, userId, "participants")));
//		}
	}
	
	public static final void processToFile(String srcFilename, String dstFilename) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(dstFilename));
		
		InstanceList instances = InstanceList.load(new File(srcFilename));
		
		int numFolds = 10;
		
//		Filter fs = new FilterByRankedFisher(TFIDF.tfidf(instances), FilterByRankedFisher.MINIMUM_SCORE);
		Filter fs = new FilterByRankedVariance(TFIDF.tfidf(instances));
		for (int nf : new IteratedExecution(instances.getAlphabet().size(), 5)) {
			InstanceList newInstances = fs.filter(nf);

			Collection<Trial> trials = crossValidate(newInstances, numFolds, new NaiveBayesTrainer());
			
			writeToFile(""+nf, trials, pw);
			pw.flush();
		}
		
		pw.close();
	}
	
	private static void writeToFile(String title, Collection<Trial> trials, PrintWriter pw) {
		pw.write(title);
		pw.write("\n");
		for (Trial trial : trials) {
			pw.write(String.valueOf(trial.getAccuracy()));
			pw.write("\n");
		}
	}
	
	
	// ------------------------------------------------------------------------
	
	public static final Map<Integer, Collection<Trial>> classificationRun(InstanceList instances, IFilter fs, ClassifierTrainer<Classifier> trainer, int numFolds) {
		Map<Integer, Collection<Trial>> trials = new HashMap<Integer, Collection<Trial>>();
		
		for (int nf : new IteratedExecution(instances.getAlphabet().size(), 5)) {
			InstanceList newInstances = fs.filter(nf);

			trials.put(nf, crossValidate(newInstances, numFolds, trainer));
		}
		
		return trials;
	}

	// ------------------------------------------------------------------------
	
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
			classifier = trainer.train(folds[0]);
			trials.add(new Trial(classifier, folds[1]));
		}
		
		return trials;
	}
	
	public static final void trial2file(Collection<Classification> trial, OutputStream out) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(out);
		
		Instance instance;
		Labeling labeling;
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
			int n = labeling.numLocations();
			for(int i=0; i < n; ++i) {
				pw.write(labeling.indexAtLocation(i) + ", ");
				pw.write(labeling.labelAtLocation(i) + ", ");
				pw.write(String.valueOf(labeling.valueAtLocation(i)));
				
				if(i+1 < n) pw.write(", ");
			}
			pw.write('\n');
		}
		
		pw.flush();
//		pw.close();
	}
	
}
