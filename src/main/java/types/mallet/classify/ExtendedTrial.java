package types.mallet.classify;

import types.mallet.LabeledInstancesList;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.Trial;
import cc.mallet.types.InstanceList;

public class ExtendedTrial extends Trial {
	private static final long serialVersionUID = 1L;
	
	private final InstanceList trainInstances;
	private final InstanceList testInstances;

	private LabeledInstancesList trainLabeledInstances;
	private LabeledInstancesList testLabeledInstances;
	
	public ExtendedTrial(Classifier c, InstanceList trainInstances, InstanceList testInstances) {
		super(c, testInstances);
		this.trainInstances = trainInstances;
		this.testInstances = testInstances;
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
