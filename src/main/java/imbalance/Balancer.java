package imbalance;

import java.util.Collection;

import types.mallet.LabeledInstancesList;
import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public abstract class Balancer {
	private Alphabet featureAlphabet;
	private Alphabet labelAlphabet;
	public LabeledInstancesList labeledInstances;
	private final int minThreshold;
	
	/**
	 * 
	 * @param t The minimum threshold number that classes must satisfy 
	 * (i.e. minimum number of documents a class should have in order to be considered).
	 */
	public Balancer(int t) {
		this.minThreshold = t;
	}
	
	public Balancer(InstanceList instances, int t) {
		this(t);
		setInstances(instances);
	}
	
	public void setInstances(InstanceList instances) {
		this.featureAlphabet = instances.getDataAlphabet();
		this.labelAlphabet = instances.getTargetAlphabet();
		this.labeledInstances = new LabeledInstancesList(instances, featureAlphabet, labelAlphabet);
	}
	
	/**
	 * 
	 * @param n		The desired balance value (number of documents that all classes should have).
	 * @return		Balanced instancelist
	 */
	public InstanceList balance(int n) {
		Noop pipe = new Noop(new Alphabet(), this.labelAlphabet);
		InstanceList newInstanceList = new InstanceList (pipe);
		
		for (InstanceList classInstances : this.labeledInstances.getInstances()) {
			// class does not have enough documents to process, ignore
			if(classInstances.size() < this.minThreshold) continue;

			for (Instance instance : balanceHook(classInstances, n)) 
				newInstanceList.addThruPipe(instance);
		}
		
		return newInstanceList;
	}
	
	protected abstract Collection<Instance> balanceHook(InstanceList classInstances, int n);
}
