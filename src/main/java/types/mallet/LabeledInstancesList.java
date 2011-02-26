package types.mallet;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

public class LabeledInstancesList {
	private final InstanceList[] labeledInstances;
	
	public LabeledInstancesList(InstanceList instances) {
		final Alphabet features = instances.getDataAlphabet();
		final Alphabet labels = instances.getTargetAlphabet();
		
		InstanceList[] labeledInstances = new InstanceList[labels.size()];
		InstanceList lInstances;
		
		for (Instance instance : instances) {
			int labelIdx = ((Label)instance.getTarget()).getIndex();
			
			if((lInstances=labeledInstances[labelIdx])==null)
				labeledInstances[labelIdx] = lInstances = new InstanceList(features, labels);
			
			lInstances.add(instance);
		}
		
		this.labeledInstances = labeledInstances;
	}
	
	public InstanceList[] getLabeledInstances() {
		return labeledInstances;
	}
	
	public InstanceList getInstances(int labelIdx) {
		return labeledInstances[labelIdx];
	}
}
