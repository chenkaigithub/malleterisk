package classifiers;

import utils.classification.CombinationUtils;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

/**
 * Majority Voting meta classifier.
 * Combines the results of several classifiers.
 * 
 * @author tt
 *
 */
public class MajorityVotingClassifier extends Classifier {
	private static final long serialVersionUID = 1L;
	
	private int n;
	private InstanceList[][] splits;
	private Classifier[] classifiers;
	
	public MajorityVotingClassifier(InstanceList[][] splits, ClassifierTrainer<Classifier>[] trainers) {
		this.n = splits.length;
		
		this.splits = splits;
		this.classifiers = new Classifier[n];
		
		for (int i = 0; i < n; i++)
			classifiers[i] = trainers[i].train(splits[i][0]);
	}
	
	@Override
	public Classification classify(Instance instance) {
		Classification[] cs = new Classification[n];
		for (int i = 0; i < n; i++)
			cs[i] = classifiers[i].classify(CombinationUtils.get(instance.getName(), splits[i][1]));
		
		LabelAlphabet targetAlphabet = (LabelAlphabet) instance.getTargetAlphabet();
		double[] values = new double[targetAlphabet.size()];
		for (Classification c : cs)
			values[c.getLabeling().getBestIndex()] += 1;
		
		return new Classification(instance, this, new LabelVector(targetAlphabet, values));
	}
}
