package types.mallet;

import java.util.HashMap;
import java.util.Map;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

public class LabeledInstancesList {
	private final InstanceList[] labeledInstances;
	public final Map<Integer, Object> labels = new HashMap<Integer, Object>();
	
	public LabeledInstancesList(InstanceList instances) {
		final Alphabet features = instances.getDataAlphabet();
		final Alphabet labels = instances.getTargetAlphabet();
		
		InstanceList[] labeledInstances = new InstanceList[labels.size()];
		InstanceList lInstances;
		
		for (Instance instance : instances) {
			int labelIdx = ((Label)instance.getTarget()).getIndex();
			this.labels.put(labelIdx, ((Label)instance.getTarget()).getBestLabel().toString());
			
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
