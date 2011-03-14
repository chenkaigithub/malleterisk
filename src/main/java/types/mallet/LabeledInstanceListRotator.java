package types.mallet;

import cc.mallet.types.InstanceList;

// TODO: 
// the idea is to remove or add groups of instances from the main instancelist
// groups of instances = instances of class C
public class LabeledInstanceListRotator {
	private final InstanceList instances;
	private final LabeledInstancesList labeledInstanceList;
	
	public LabeledInstanceListRotator(InstanceList instances) {
		this.instances = instances;
		this.labeledInstanceList = new LabeledInstancesList(instances);
		
		instances.removeAll(instances);
		
	}
	
	public InstanceList removeLabelInstances(int labelIdx) {
		InstanceList newInstances = (InstanceList)instances.clone();
		newInstances.removeAll(labeledInstanceList.getInstances(labelIdx));
		
		return newInstances;
	}
	
	
}
