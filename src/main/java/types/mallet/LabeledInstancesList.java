package types.mallet;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

/**
 * Organizes the instances and it's corresponding labels.
 * Allows quick retrieval of the instances of a given class.
 * 
 * @author tt
 */
public class LabeledInstancesList {
	private final Object[] labels;
	private final InstanceList[] instancelists;
	
	public LabeledInstancesList(InstanceList instances) {
		this(instances, instances.getDataAlphabet(), instances.getTargetAlphabet());
	}
	
	public LabeledInstancesList(InstanceList instances, Alphabet featureAlphabet, Alphabet labelAlphabet) {
		int n = labelAlphabet.size();
		labels = new Object[n];
		instancelists = new InstanceList[n];
		
		// iterate all instances and split them according to their label
		InstanceList lInstances; // the current label's instancelist that's being iterated (same as instancelists[labelIdx])
		for (Instance instance : instances) {
			// get current instance's label
			int labelIdx = ((Label)instance.getTarget()).getIndex();
			
			// unseen label, instantiate an instancelist and store the label object
			if((lInstances=instancelists[labelIdx])==null) {
				instancelists[labelIdx] = lInstances = new InstanceList(featureAlphabet, labelAlphabet);
				labels[labelIdx] = labelAlphabet.lookupObject(labelIdx);
			}
			
			lInstances.add(instance);
		}
	}
	
	public Object[] getLabels() {
		return labels;
	}
	
	public int getNumLabels() {
		return labels.length;
	}
	
	public InstanceList[] getInstanceLists() {
		return instancelists;
	}
	
	public int getNumInstances() {
		int i = 0;
		for (InstanceList instances : instancelists) i+= instances.size();
		
		return i;
	}
	
	public Object getLabel(int labelIdx) {
		return labels[labelIdx];
	}
	
	public InstanceList getLabelInstances(int labelIdx) {
		return instancelists[labelIdx];
	}
	
	public int getNumLabelInstances(int labelIdx) {
		if(instancelists[labelIdx] == null) return 0;
		
		return instancelists[labelIdx].size();
	}
	
	public int getMaxNumInstances() {
		int max = 0;
		int current;
		
		for (InstanceList instances : instancelists) {
			current = instances.size();
			if(current > max) max = current;
		}
		
		return max;
	}
}
