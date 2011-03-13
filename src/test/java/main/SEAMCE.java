package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import pp.PreProcessor;
import pp.email.body.BodyPreProcessor1;
import pp.email.subject.SubjectPreProcessor1;
import utils.IteratedExecution;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import cc.mallet.types.Labeling;
import data.IDataSet;
import ft.selection.Filter;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedDF;
import ft.selection.methods.FilterByRankedFisher;
import ft.selection.methods.FilterByRankedIG;
import ft.selection.methods.FilterByRankedL0Norm1;
import ft.selection.methods.FilterByRankedL0Norm2;
import ft.selection.methods.FilterByRankedVariance;
import ft.transformation.ITransformer;
import ft.transformation.methods.TDI;
import ft.transformation.methods.TFIDF;

/* 
 * TODO:
 * 
 * 0.
 * streamline everything into big pipelines
 * of processing
 * 
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
		// (automatically) load instance lists from file
		ArrayList<InstanceList> instanceLists = new ArrayList<InstanceList>();
		instanceLists.add(InstanceList.load(new File("instances+0+0+tests")));
//		instanceLists.add(InstanceList.load(new File("instances+1+1+bodies")));
//		instanceLists.add(InstanceList.load(new File("instances+1+2+bodies")));
//		instanceLists.add(InstanceList.load(new File("instances+1+3+bodies")));
//		instanceLists.add(InstanceList.load(new File("instances+1+4+bodies")));
//		instanceLists.add(InstanceList.load(new File("instances+1+5+bodies")));
//		instanceLists.add(InstanceList.load(new File("instances+1+6+bodies")));
//		instanceLists.add(InstanceList.load(new File("instances+1+7+bodies")));
//		instanceLists.add(InstanceList.load(new File("instances+2+1+bodies")));
		
		// apply feature transformation
		ArrayList<ITransformer> transformers = new ArrayList<ITransformer>();
		transformers.add(new TFIDF());
		transformers.add(new TDI());
		Map<ITransformer, InstanceList> ftil = transform(transformers, instanceLists);
		
		for (ITransformer transformer : ftil.keySet()) {
			InstanceList instances = ftil.get(transformer);
			
			// apply feature selection
			ArrayList<IFilter> fss = new ArrayList<IFilter>();
			fss.add(new FilterByRankedDF(instances));
			fss.add(new FilterByRankedIG(instances));
			fss.add(new FilterByRankedVariance(instances));
			fss.add(new FilterByRankedL0Norm1(instances));
			fss.add(new FilterByRankedL0Norm2(instances));
			fss.add(new FilterByRankedFisher(instances, FilterByRankedFisher.MINIMUM_SCORE));
			fss.add(new FilterByRankedFisher(instances, FilterByRankedFisher.SUM_SCORE));
			fss.add(new FilterByRankedFisher(instances, FilterByRankedFisher.SUM_SQUARED_SCORE));
			
			for (int n : new IteratedExecution(instances.getDataAlphabet().size(), 5)) {
				for (IFilter filter : fss) {
					instances = filter.filter(n);
					
					// classify
					ArrayList<ClassifierTrainer<? extends Classifier>> cs = new ArrayList<ClassifierTrainer<? extends Classifier>>();
					cs.add(new NaiveBayesTrainer());
//					cs.add(new BalancedWinnowTrainer());
					
					for (ClassifierTrainer<? extends Classifier> classifier : cs) {
						Collection<Trial> trials = crossValidate(instances, 10, classifier);
						for (Trial trial : trials) {
							FileOutputStream fos = new FileOutputStream(new File(generateOutName(transformer, filter, classifier)));
							trial2out(trial, fos);
							fos.close();
						}
					}
				}
			}
		}
	}
	
	public static final void db2file() {
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
	
	public static final String generateOutName(ITransformer transformer, IFilter filter, ClassifierTrainer<? extends Classifier> classifier) {
		StringBuffer sb = new StringBuffer();
		sb.append(transformer.getDescription());
		sb.append("+");
		sb.append(filter.getDescription());
		sb.append("+");
		sb.append(getClassifierDescription(classifier));
		sb.append("+");
		sb.append(new Date().toString());

		return sb.toString();
	}
	
	
	
	public static final Collection<PreProcessor> preprocess(IDataSet ds, Collection<PreProcessor> preprocessors) {
		Instance instance;
		while(ds.hasNext()) {
			instance = ds.next();
			
			for (PreProcessor pp : preprocessors)
				pp.addThruPipe(instance);
		}
		
		return preprocessors;
	}
	
	public static final Map<ITransformer, InstanceList> transform(Collection<ITransformer> transformers, Collection<InstanceList> instanceLists) {
		Map<ITransformer, InstanceList> m = new HashMap<ITransformer, InstanceList>();
		
		for (ITransformer transformer : transformers)
			for (InstanceList instanceList : instanceLists) 
				m.put(transformer, transformer.calculate(instanceList));
		
		return m;
	}
	
	public static final Map<Integer, Collection<Trial>> classificationRun(InstanceList instances, IFilter fs, ClassifierTrainer<Classifier> trainer, int numFolds) {
		Map<Integer, Collection<Trial>> trials = new HashMap<Integer, Collection<Trial>>();
		
		for (int nf : new IteratedExecution(instances.getAlphabet().size(), 5)) {
			InstanceList newInstances = fs.filter(nf);

			trials.put(nf, crossValidate(newInstances, numFolds, trainer));
		}
		
		return trials;
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
			classifier = trainer.train(folds[0]);
			trials.add(new Trial(classifier, folds[1]));
		}
		
		return trials;
	}
	
	/**
	 * Writes the results of the classification into the output stream in a formatted manner.
	 * (instance, real_class_idx, real_class, class_n1_idx, class_n1, class_n1_val, ..., class_nK_idx, class_nK, class_nK_val)
	 * 
	 * @param trial
	 * @param out
	 * @throws FileNotFoundException
	 */
	public static final void trial2out(Collection<Classification> trial, OutputStream out) throws FileNotFoundException {
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
	
	public static final void trialsAccuracies2out(int numFeatures, Collection<Trial> trials, OutputStream out) {
		PrintWriter pw = new PrintWriter(out);
		
		pw.write(numFeatures);
		pw.write(", ");
		int i = 0;
		for (Trial trial : trials) {
			pw.write(String.valueOf(trial.getAccuracy()));
			if(i++ < trials.size()) pw.write(", ");
		}
	}
	
	
	
	public static final String getClassifierDescription(ClassifierTrainer<? extends Classifier> classifier) {
		return classifier.getClass().getSimpleName();
	}
	
	
	
	/*
		//
		// COLLECTION ANALYSIS
		//
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

		//
		// COLLECTION PROCESSING
		//
//		processToFile(String.format("instances_%d-%d_%s", 0, 0, "tests"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 0, 0, "tests", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 1, "subjects"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 1, "subjects", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 1, "bodies"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 1, "bodies", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 2, "subjects"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 2, "subjects", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 2, "bodies"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 2, "bodies", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 3, "subjects"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 3, "subjects", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 3, "bodies"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 3, "bodies", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 4, "subjects"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 4, "subjects", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 4, "bodies"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 4, "bodies", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 5, "subjects"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 5, "subjects", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 5, "bodies"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 5, "bodies", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 6, "subjects"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 6, "subjects", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 6, "bodies"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 6, "bodies", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 7, "subjects"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 7, "subjects", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
//		processToFile(String.format("instances_%d-%d_%s", 1, 7, "bodies"), String.format("collection%d+user%d+%s+%s+%s+%s+%s", 1, 7, "bodies", "yyyy-MM-dd_hh-mm-ss".format(new Date())));
	 */
	
	// ------------------------------------------------------------------------
	
	// transition methods. deprecated.
	
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
			
//			subjects.save(new File(String.format("instances_%d-%d_%s", collectionId, userId, "subjects")));
//			bodies.save(new File(String.format("instances_%d-%d_%s", collectionId, userId, "bodies")));
//			dates.save(new File(String.format("instances_%d-%d_%s", collectionId, userId, "dates")));
//			participants.save(new File(String.format("instances_%d-%d_%s", collectionId, userId, "participants")));
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
}
