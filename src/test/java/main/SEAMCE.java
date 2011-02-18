package main;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
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
 * 
 * + integrate SVM into mallet
 * 
 * + test, test, test
 * + experiment, experiment, experiment
 */
public class SEAMCE {
	public static void main(String[] args) throws SQLException {
//		System.out.println("-initialization-");
//		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
//		IDataSet ds = new EnronDbDataSet(dal, 1, 6);
//		InstanceList ilBody = new BodyPreProcessor().getInstanceList();
//		//ilBody.save(new File("ilBody.txt"));
//		//InstanceList shuffled = ilBody.shallowClone();
//		//shuffled.shuffle (new Random()); // this is where the randomness happens
//		//shuffled.save(new File("ilBodyShuffled.txt"));
//
//		System.out.println("-preprocess-");
//		for (Instance msgInstance : ds) ilBody.addThruPipe(msgInstance);
		
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
