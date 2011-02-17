package misc;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InfoGain;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.FeatureTransformationPipeline;
import fs.IFeatureTransformer;
import fs.methods.TFIDF;

public class SEAMCE {
	public static void main(String[] args) throws SQLException {
//		System.out.println("-initialization-");
//		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
//		IDataSet ds = new EnronDbDataSet(dal, 1, 6);
//		IPreProcessor bodyPreProcessor = new BodyPreProcessor();
//		InstanceList ilBody = bodyPreProcessor.getInstanceList();
//
//		System.out.println("-preprocess-");
//		for (Instance msgInstance : ds) ilBody.addThruPipe(msgInstance);

		InstanceList ilBody = InstanceList.load(new File("ilBody.txt"));
		
		System.out.println("-feature selection-");
		System.out.println(ilBody.getAlphabet().size());
		LinkedList<IFeatureTransformer> featureSelectors = new LinkedList<IFeatureTransformer>();
//		featureSelectors.add(new TermFreqPruner(5, 100));
		featureSelectors.add(new TFIDF());
		ilBody = new FeatureTransformationPipeline(featureSelectors).runThruPipe(ilBody);

		System.out.println(ilBody.getAlphabet().size());
		Alphabet alphabet = new Alphabet ();
		Noop pipe = new Noop (alphabet, ilBody.getTargetAlphabet());
		InstanceList instances = new InstanceList (pipe);
		InfoGain ig = new InfoGain (ilBody);
		FeatureSelection fs = new FeatureSelection (ig, 5000);
		for (int ii = 0; ii < ilBody.size(); ii++) {
			Instance instance = ilBody.get(ii);
			FeatureVector oldFV = (FeatureVector) instance.getData();
			FeatureVector newFV = FeatureVector.newFeatureVector (oldFV, alphabet, fs);
			instance.unLock();
			instance.setData(null);
			instances.add(pipe.instanceFrom(new Instance(newFV, instance.getTarget(), instance.getName(), instance.getSource())), ilBody.getInstanceWeight(ii));
		}
		ilBody = instances;
		System.out.println(ilBody.getAlphabet().size());
		
		System.out.println("-classification-");
		InstanceList[] bodySplit = ilBody.split(new double[] { 0.8, 0.2 });
		
		ClassifierTrainer<?> bodyTrainer = new NaiveBayesTrainer();
		Classifier nbBodyClassifier = bodyTrainer.train(bodySplit[0]);
		System.out.println("body training accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[0]));
		System.out.println("body testing accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[1]));
	}
}
