package execution;

import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import types.mallet.LabeledInstancesList;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;

/**
 * Implements the One vs All strategy for classification.
 * The collection is iterated and each iteration, a different class is picked as the "One",
 * while the others are transformed into "All".
 * 
 * @author tt
 *
 */
public class OneVersusAll implements Iterable<InstanceList>, Iterator<InstanceList> {
	private final InstanceList instances;
	private final InstanceList[] labeledInstances;
	private int currentOneClassIndex;
	
	public OneVersusAll(InstanceList instances) {
		this.instances = instances;
		
		this.labeledInstances = new LabeledInstancesList(instances).getLabeledInstances();
		this.currentOneClassIndex = 0;
	}
	
	public Object getCurrentOneClass() {
		return instances.getTargetAlphabet().lookupObject(currentOneClassIndex-1);
	}

	@Override
	public boolean hasNext() {
		return currentOneClassIndex < labeledInstances.length;
	}

	@Override
	public InstanceList next() {
		// create new instancelist with changed target alphabet (empty for now, will be filled internally)
		Alphabet features = this.instances.getDataAlphabet();
		Alphabet labels = this.instances.getTargetAlphabet();
		LabelAlphabet newLabels = new LabelAlphabet();
		InstanceList newInstances = new InstanceList(features, newLabels);
		
		// get the "current" label
		Object currentLabel = labels.lookupObject(currentOneClassIndex++);
		Object labelOne = newLabels.lookupLabel(currentLabel, true);
		Object labelAll = newLabels.lookupLabel(-1, true);
		
		// set the new targets for all the instances
		for (Instance instance : this.instances) {
			newInstances.add(new Instance(
				instance.getData(), 
				((Label) instance.getTarget()).getEntry().equals(currentLabel) ? labelOne : labelAll, 
				instance.getName(), 
				instance.getSource())
			);
		}
		
		return newInstances;
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
