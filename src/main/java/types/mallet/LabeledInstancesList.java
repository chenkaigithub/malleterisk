package types.mallet;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class LabeledInstancesList {
	private final InstanceList[] labeledInstances;
	
	public LabeledInstancesList(InstanceList instances) {
		final Alphabet features = instances.getDataAlphabet();
		final Alphabet labels = instances.getTargetAlphabet();
		
		InstanceList[] labeledInstances = new InstanceList[labels.size()];
		InstanceList lInstances;
		
		for (Instance instance : instances) {
			int labelIdx = labels.lookupIndex(instance.getTarget());
			
			if((lInstances=labeledInstances[labelIdx])==null) 
				lInstances = new InstanceList(features, labels);
			
			lInstances.add(instance);
		}
		
		this.labeledInstances = labeledInstances;
	}
	
	public InstanceList getInstances(int labelIdx) {
		return labeledInstances[labelIdx];
	}
}
