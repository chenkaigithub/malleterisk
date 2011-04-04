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
import analysis.CollectionAnalysis;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import data.IDataSet;
import data.enron.EnronDbDataSet;
import data.enron.db.EnronDbConnector;
import data.enron.db.EnronDbDataAccess;
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
import ft.selection.methods.FilterByRankedTF;
import ft.selection.methods.FilterByRankedVariance;
import ft.transformation.ITransformer;
import ft.transformation.methods.FeatureWeighting;

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
 * 1.
 * combater o desiquilibro das classes
 *  - equilibrar o nœmero de documentos das classes 
 * 		reduzir o nœmero de documentos se necess‡rio (~100)
 *  - como? escolher documentos representativos
 * 		random sampling
 * 		clustering + escolha representantes
 * 		clustering + divis‹o em subclasses
 * 	- variar o nœmero de classes
 * 	- one class classifiers
 * 	- one vs all, all vs all
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
	public static void main(String[] args) throws Exception {
	}
	
	public static final void oneVsAll() throws FileNotFoundException, InstantiationException, IllegalAccessException {
		InstanceList instances = InstanceList.load(new File("instances+2+1+bodies"));
		
		OneVersusAll ova = new OneVersusAll(instances);
		while(ova.hasNext()) {
			instances = ova.next();
			
			int step = 10;
			int folds = 10;
			
			ITransformer transformer = new FeatureWeighting(FeatureWeighting.TF_BOOLEAN, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_COSINE); // ltc
			IFilter filter = new FilterByRankedDF();
			ClassifierTrainer<? extends Classifier> trainer = new NaiveBayesTrainer();
			
			sequentialRun("instances+2+1+bodies+"+ova.currentOneClassLabel, instances, transformer, filter, trainer, step, folds);
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
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE)); // nnn
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE)); // ntn
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_BOOLEAN, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_COSINE)); // ltc
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_MAX_NORM, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_COSINE)); // mtc
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_BOOLEAN, FeatureWeighting.IDF_NONE, FeatureWeighting.NORMALIZATION_NONE)); // boolean
		
		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedTF());
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
		
		x(files, transformers, filters, classifiers, step, folds);
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
	
	public static final void x(ArrayList<File> files, ArrayList<ITransformer> transformers, ArrayList<IFilter> filters, ArrayList<ClassifierTrainer<? extends Classifier>> classifiers, int step, int folds) 
		throws FileNotFoundException, InstantiationException, IllegalAccessException {
		for (File file : files) {
			System.out.println("+ processing " + file.getName());
			InstanceList instances = InstanceList.load(file);
			for (ITransformer transformer : transformers) {
				System.out.println("- transformer: " + transformer.getDescription());
				for (IFilter filter : filters) {
					System.out.println("- filter: " + filter.getDescription());
					for (ClassifierTrainer<? extends Classifier> trainer : classifiers) {
						SEAMCE.sequentialRun(file.getName(), instances, transformer, filter, trainer, step, folds);
					}
				}
			}
		}
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
	
	public static final void db2file() throws SQLException {
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
