package types.mallet;

import java.util.HashMap;
import java.util.Map;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

public class LabeledInstancesList {
	private final Object[] labels;
	private final InstanceList[] instancelists;
	public final Map<Object, InstanceList> labelsInstancelists;
	
	public LabeledInstancesList(InstanceList instances) {
		final Alphabet featureAlphabet = instances.getDataAlphabet();
		final Alphabet labelAlphabet = instances.getTargetAlphabet();
		
		int n = labelAlphabet.size();
		instancelists = new InstanceList[n];
		labels = new Object[n];
		labelsInstancelists = new HashMap<Object, InstanceList>(n);
		
		// iterate all instances and split them according to their label
		// ATTN: we are safely assuming that the instances are ordered by their target
		// otherwise we would need to use the labelsInstancelists.get()
		InstanceList lInstances; // the current label's instancelist that's being iterated (same as instancelists[labelIdx])
		for (Instance instance : instances) {
			// get current instance's label
			int labelIdx = ((Label)instance.getTarget()).getIndex();
			
			// new label?
			if((lInstances=instancelists[labelIdx])==null) {
				instancelists[labelIdx] = lInstances = new InstanceList(featureAlphabet, labelAlphabet);
				labels[labelIdx] = labelAlphabet.lookupObject(labelIdx);
				
				labelsInstancelists.put(labels[labelIdx], instancelists[labelIdx]);
			}
			
			lInstances.add(instance);
		}
	}
	
	public InstanceList[] getLabeledInstances() {
		return instancelists;
	}
	
	public InstanceList getInstances(int labelIdx) {
		return instancelists[labelIdx];
	}
	
	public int size() {
		return instancelists.length;
	}
}
