package execution;

import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import types.mallet.LabeledInstancesList;
import cc.mallet.types.InstanceList;

public class RandomSampling implements Iterable<InstanceList>, Iterator<InstanceList> {
	private final InstanceList instances;
	private final InstanceList[] labeledInstances;
	
	public RandomSampling(InstanceList instances) {
		this.instances = instances;
		
		this.labeledInstances = new LabeledInstancesList(instances).getLabeledInstances();
	}
	
	@Override
	public boolean hasNext() {
//		return currentOneClassIndex < labeledInstances.length;
		throw new NotImplementedException();
	}

	@Override
	public InstanceList next() {
//		// create new instancelist with changed target alphabet (empty for now, will be filled internally)
//		Alphabet features = this.instances.getDataAlphabet();
//		Alphabet labels = this.instances.getTargetAlphabet();
//		LabelAlphabet newLabels = new LabelAlphabet();
//		InstanceList newInstances = new InstanceList(features, newLabels);
//		
//		// get the "current" label
//		this.currentOneClassLabel = labels.lookupObject(currentOneClassIndex++);
//		Object labelOne = newLabels.lookupLabel(this.currentOneClassLabel, true);
//		Object labelAll = newLabels.lookupLabel(-1, true);
//		
//		// set the new targets for all the instances
//		for (Instance instance : this.instances) {
//			newInstances.add(new Instance(
//				instance.getData(), 
//				((Label) instance.getTarget()).getEntry().equals(this.currentOneClassLabel) ? labelOne : labelAll, 
//				instance.getName(), 
//				instance.getSource())
//			);
//		}
//		
//		return newInstances;
		throw new NotImplementedException();
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}

	@Override
	public Iterator<InstanceList> iterator() {
		return this;
	}
}
