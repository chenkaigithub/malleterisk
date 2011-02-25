package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import pp.email.body.BodyPreProcessor1;
import pp.email.subject.SubjectPreProcessor1;
import utils.IteratedExecution;
import analysis.TextCollectionAnalysis;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import data.IDataSet;
import data.enron.EnronDbDataSet;
import data.enron.db.EnronDbConnector;
import data.enron.db.EnronDbDataAccess;
import fs.FeatureTransformationPipeline;
import fs.IFeatureTransformer;
import fs.methods.PruneByDF;
import fs.methods.PruneByL0Norm;
import fs.methods.PruneByTF;
import fs.methods.RankByIG;
import fs.methods.RankByL0Norm;
import fs.methods.TFIDF;

/*
 * TODO:
 * 
 * 0. 
 * minimum number of docs / class
 * maximum number of docs / class
 * 
 * 2.
 * supervised feature selection
 * unsupervised feature selection
 * [fisher, variance]
 * 
 * 3.
 * varying number of classes
 * 
 * 4.
 * pearson correlation
 * mRMR (Minimum Redundancy Maximum Relevance)
 * feature clustering
 * 	find the most similar features (cosine similarity)
 * 	remove least relevant features from each cluster
 * 
 * 5.
 * participants: 
 * 	treat as a feature selection problem
 * 	documents x participants incidence matrix
 * 	apply feature selection methods
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
 * - feature selection methods
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
	private static final String SRC_FILENAME_FORMAT = "instances_%d-%d_%s";
	private static final String DST_FILENAME_FORMAT = "results_%d-%d_%s_%s";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
	
	public static void main(String[] args) throws SQLException, FileNotFoundException {
//		preprocessToFile();
		
//		analyzeCollection();
		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 1, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 1, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 1, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 1, "bodies", DATE_FORMAT.format(new Date())));
		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 2, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 2, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 2, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 2, "bodies", DATE_FORMAT.format(new Date())));

//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 3, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 3, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 3, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 3, "bodies", DATE_FORMAT.format(new Date())));
		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 4, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 4, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 4, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 4, "bodies", DATE_FORMAT.format(new Date())));
		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 5, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 5, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 5, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 5, "bodies", DATE_FORMAT.format(new Date())));
		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 6, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 6, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 6, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 6, "bodies", DATE_FORMAT.format(new Date())));
		
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 7, "subjects"), String.format(DST_FILENAME_FORMAT, 1, 7, "subjects", DATE_FORMAT.format(new Date())));
//		processToFile(String.format(SRC_FILENAME_FORMAT, 1, 7, "bodies"), String.format(DST_FILENAME_FORMAT, 1, 7, "bodies", DATE_FORMAT.format(new Date())));
	}
	
	public static final void preprocessToFile() throws SQLException {
		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
		int collectionId = 1;
		
		for (int userId : dal.getUsers(collectionId)) {
			IDataSet ds = new EnronDbDataSet(dal, collectionId, userId);
			
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
			
			subjects.save(new File(String.format(DST_FILENAME_FORMAT, collectionId, userId, "subjects")));
			bodies.save(new File(String.format(DST_FILENAME_FORMAT, collectionId, userId, "bodies")));
//			dates.save(new File(String.format(s, collectionId, userId, "dates")));
//			participants.save(new File(String.format(s, collectionId, userId, "participants")));
		}
	}
	
	public static final void analyzeCollection() {
		System.out.println("1-1");
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 1, "subjects")))).toString());
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 1, "bodies")))).toString());
		
		System.out.println("1-2");
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 2, "subjects")))).toString());
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 2, "bodies")))).toString());
		
		System.out.println("1-3");
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 3, "subjects")))).toString());
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 3, "bodies")))).toString());
		
		System.out.println("1-4");
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 4, "subjects")))).toString());
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 4, "bodies")))).toString());
		
		System.out.println("1-5");
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 5, "subjects")))).toString());
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 5, "bodies")))).toString());
		
		System.out.println("1-6");
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 6, "subjects")))).toString());
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 6, "bodies")))).toString());
		
		System.out.println("1-7");
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 7, "subjects")))).toString());
		System.out.println(new TextCollectionAnalysis(InstanceList.load(new File(String.format(SRC_FILENAME_FORMAT, 1, 7, "bodies")))).toString());	
	}
	
	
	
	public static final void processToFile(String srcFilename, String dstFilename) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(dstFilename));
		
		InstanceList instances = InstanceList.load(new File(srcFilename));
		
		int numFolds = 10;
		
		for (int nf : new IteratedExecution(instances.getAlphabet().size(), 5)) {
			LinkedList<IFeatureTransformer> featureSelectors = new LinkedList<IFeatureTransformer>();
			featureSelectors.add(new TFIDF());
//			featureSelectors.add(new PruneByTF(5, 100));
//			featureSelectors.add(new PruneByDF(nf));
//			featureSelectors.add(new RankByIG(nf));
//			featureSelectors.add(new PruneByL0Norm(nf));
			featureSelectors.add(new RankByL0Norm(nf));
			InstanceList newInstances = new FeatureTransformationPipeline(featureSelectors).runThruPipeline(instances);

			Collection<Trial> trials = crossValidate(newInstances, numFolds, new NaiveBayesTrainer());
			
			writeToFile("rankbyl0norm"+nf, trials, pw);
		}
		
		pw.flush();
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
}
