package main;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import pp.email.body.BodyPreProcessor1;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
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
 * + test, test, test
 * + experiment, experiment, experiment
 */
public class SEAMCE {
	public static void main(String[] args) throws SQLException {
		System.out.println("-loading documents-");
		InstanceList ilBody = InstanceList.load(new File("ilBodyShuffled.txt"));
		
		// normalize the feature iteration (0% ... 100%)
		final double step = ilBody.getAlphabet().size() / 100.0;
		for(int i = 100; i > 0; i -= 5) {
  			int nf = (int)Math.ceil(i*step);
			
  			System.out.println("-feature selection-");
			LinkedList<IFeatureTransformer> featureSelectors = new LinkedList<IFeatureTransformer>();
			featureSelectors.add(new TFIDF());
			featureSelectors.add(new PruneByTF(5, 100));
			featureSelectors.add(new PruneByDF(nf));
			featureSelectors.add(new RankByIG(nf));
			featureSelectors.add(new PruneByL0Norm(nf));
			featureSelectors.add(new RankByL0Norm(nf));
			InstanceList newInstances = new FeatureTransformationPipeline(featureSelectors).runThruPipeline(ilBody);
			System.out.print(i + "|" + newInstances.getAlphabet().size() + "|");
			
			System.out.println("-classification-");
			int numFolds = 10;
			double v = 0;
			Collection<Trial> trials = crossValidate(newInstances, numFolds, new NaiveBayesTrainer());
			for (Trial trial : trials) v += trial.getAccuracy();
			System.out.println(v/numFolds);
//			System.out.println(new ConfusionMatrix(trial));
		}
	}
	
	public void x() throws SQLException {
		// TODO: how do I decouple this? 
		// what kind of dependency injection can i add here?
		
		// which collection and user to process?
		System.out.println("-document loading-");
		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
		int collectionId = 1;
		int userId = 6;
		IDataSet ds = new EnronDbDataSet(dal, collectionId, userId);
		
		// preprocessing options - what kind of preprocessing to apply?
		// TODO: create new classes for different setups 
		System.out.println("-preprocessing-");
//		InstanceList ilSubject = new SubjectPreProcessor1(ds);
		InstanceList ilBody = new BodyPreProcessor1(ds);
//		InstanceList ilDate = new DatePreProcessor1(ds);
//		InstanceList ilParticipants = new ParticipantsPreProcessor1(ds);		
		
		// TODO: save/load instancelist from file
//		InstanceList ilSubject = InstanceList.load(new File("ilSubjectShuffled.txt"));
//		InstanceList ilBody = InstanceList.load(new File("ilBodyShuffled.txt"));
//		InstanceList ilDate = InstanceList.load(new File("ilDateShuffled.txt"));
//		InstanceList ilParticipants = InstanceList.load(new File("ilParticipantsShuffled.txt"));
		
		// TODO: repeat this for each instance list
		// this highly coupled with the feature selection
//		for (int nf : new IteratedExecution(ilBody.getAlphabet().size(), 5)) {
//			// do the feature selection here
//			// do the classification here
//			// print out results to somewhere
//		}
		
		// TODO: do this for subject, body, date, participants
		// feature selection
		System.out.println("-feature selection-");
		int nf = 2500; // TODO: parametrize the number of features for fs
		LinkedList<IFeatureTransformer> featureSelectors = new LinkedList<IFeatureTransformer>();
		// TODO: parametrize the feature selection operations
		featureSelectors.add(new TFIDF());
		featureSelectors.add(new PruneByTF(5, 100));
		featureSelectors.add(new PruneByDF(nf));
		featureSelectors.add(new RankByIG(nf));
		featureSelectors.add(new PruneByL0Norm(nf));
		featureSelectors.add(new RankByL0Norm(nf));
		InstanceList newInstances = new FeatureTransformationPipeline(featureSelectors).runThruPipeline(ilBody);

		System.out.println("-classification-");
		int numFolds = 10;
		double v = 0;
		Collection<Trial> trials = crossValidate(newInstances, numFolds, new NaiveBayesTrainer());
		for (Trial trial : trials) v += trial.getAccuracy();
		System.out.println(v/numFolds);
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
