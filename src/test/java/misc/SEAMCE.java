package misc;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import fs.FeatureTransformationPipeline;
import fs.IFeatureTransformer;
import fs.methods.PruneByDF;
import fs.methods.PruneByTF;
import fs.methods.RankByIG;
import fs.methods.TFIDF;

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
		ilBody = new FeatureTransformationPipeline(featureSelectors).runThruPipeline(ilBody);
		System.out.println(ilBody.getAlphabet().size());
		
		System.out.println("-classification-");
		InstanceList[] bodySplit = ilBody.split(new double[] { 0.8, 0.2 });
		
		ClassifierTrainer<?> bodyTrainer = new NaiveBayesTrainer();
		Classifier nbBodyClassifier = bodyTrainer.train(bodySplit[0]);
		System.out.println("body training accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[0]));
		System.out.println("body testing accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[1]));
	}
}
