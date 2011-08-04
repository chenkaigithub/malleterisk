package types.mallet.classify;

import types.mallet.LabeledInstancesList;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.Trial;
import cc.mallet.types.InstanceList;

/**
 * Extension of Mallet's Trial class, adding cross validation information.
 * Holds the fold number plus the train and test instances.
 * 
 * @author tt
 *
 */
public class ExtendedTrial extends Trial {
	private static final long serialVersionUID = 1L;
	
	private final InstanceList trainInstances;
	private final InstanceList testInstances;
	public final int fold;
	
	private LabeledInstancesList trainLabeledInstances;
	private LabeledInstancesList testLabeledInstances;
	
	public ExtendedTrial(Classifier c, InstanceList trainInstances, InstanceList testInstances, int fold) {
		super(c, testInstances);
		this.trainInstances = trainInstances;
		this.testInstances = testInstances;
		this.fold = fold;
	}
	
	public LabeledInstancesList getTrainLabeledInstances() {
		if(trainLabeledInstances==null)
			trainLabeledInstances = new LabeledInstancesList(trainInstances);
		
		return trainLabeledInstances;
	}
	
	public LabeledInstancesList getTestLabeledInstances() {
		if(testLabeledInstances==null)
			testLabeledInstances = new LabeledInstancesList(testInstances);
		
		return testLabeledInstances;
	}
}
