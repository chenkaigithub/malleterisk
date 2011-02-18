package misc;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import cc.mallet.types.MatrixOps;
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
 * + must automatize all the testing
 * - try out different users (use enron-flat, it's big enough)
 * - create classes that for each setup, write out results 
 * to somewhere so I can plot some graphs in matlab/etc 
 * 
 * + implement the remaining stuff
 * 
 * + integrate SVM into mallet
 * 
 * + test, test, test
 */
public class SEAMCE {
	public static void main(String[] args) throws SQLException {
//		System.out.println("-initialization-");
//		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
//		IDataSet ds = new EnronDbDataSet(dal, 1, 6);
//		InstanceList ilBody = new BodyPreProcessor().getInstanceList();
//		//ilBody.save(new File("ilBody.txt"));
//
//		System.out.println("-preprocess-");
//		for (Instance msgInstance : ds) ilBody.addThruPipe(msgInstance);
		
		System.out.println("-loading documents-");
		InstanceList ilBody = InstanceList.load(new File("ilBody.txt"));
		
		System.out.println("-feature selection-");
		System.out.println(ilBody.getAlphabet().size());
		LinkedList<IFeatureTransformer> featureSelectors = new LinkedList<IFeatureTransformer>();
		featureSelectors.add(new TFIDF());
		featureSelectors.add(new PruneByTF(5, 100));
		featureSelectors.add(new PruneByDF(5000));
		featureSelectors.add(new RankByIG(2500));
		featureSelectors.add(new PruneByL0Norm(1000));
		featureSelectors.add(new RankByL0Norm(2500));
		ilBody = new FeatureTransformationPipeline(featureSelectors).runThruPipeline(ilBody);
		System.out.println(ilBody.getAlphabet().size());
		
		System.out.println("-classification-");
		InstanceList[] bodySplit = ilBody.split(new double[] { 0.8, 0.2 });
		
		ClassifierTrainer<?> bodyTrainer = new NaiveBayesTrainer();
		Classifier nbBodyClassifier = bodyTrainer.train(bodySplit[0]);
		System.out.println("body training accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[0]));
		System.out.println("body testing accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[1]));
	}
	
	// TODO: code from InstanceList; modify it for a deterministic instance list splitting
	public static void splitInstances(InstanceList instances, double[] proportions) {
		// InstanceList.split
		InstanceList shuffled = instances.shallowClone();
		shuffled.shuffle (new Random()); // this is where the randomness happens

		// InstanceList.splitInOrder
		InstanceList[] ret = new InstanceList[proportions.length];
		double maxind[] = proportions.clone();
		MatrixOps.normalize(maxind);
		for (int i = 0; i < maxind.length; i++) {
			ret[i] = instances.cloneEmpty();  // Note that we are passing on featureSelection here.
			if (i > 0) 
				maxind[i] += maxind[i-1];
		}
		for (int i = 0; i < maxind.length; i++) { 
			// Fill maxind[] with the highest instance index to go in each corresponding returned InstanceList
			maxind[i] = Math.rint (maxind[i] * instances.size());
		}
		for (int i = 0, j = 0; i < instances.size(); i++) {
			// This gives a slight bias toward putting an extra instance in the last InstanceList.
			while (i >= maxind[j] && j < ret.length) 
				j++;
			ret[j].add(instances.get(i));
		}
		
		// return ret;
	}
}
