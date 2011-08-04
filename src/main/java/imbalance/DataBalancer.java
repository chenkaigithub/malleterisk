package imbalance;

import java.util.Collection;

import types.mallet.LabeledInstancesList;
import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

/**
 * Base class for the data balancing methods.
 * TODO: reference with explanation of data balancing methods
 * 
 * 
 * 
 * @author tt
 *
 */
public abstract class DataBalancer {
	private Alphabet featureAlphabet;
	private Alphabet labelAlphabet;
	public LabeledInstancesList labeledInstances; // TODO: hmm.. it's only public because someone calls .getMaxNumInstances()
	private final int minThreshold;
	
	/**
	 * @param t The minimum threshold number that classes must satisfy 
	 * (i.e. minimum number of documents a class should have in order to be considered).
	 * classes with #documents < t will not be kept. 
	 */
	public DataBalancer(int t) {
		this.minThreshold = t;
	}
	
	public DataBalancer(InstanceList instances, int t) {
		this(t);
		setInstances(instances);
	}
	
	public void setInstances(InstanceList instances) {
		this.featureAlphabet = instances.getDataAlphabet();
		this.labelAlphabet = instances.getTargetAlphabet();
		this.labeledInstances = new LabeledInstancesList(instances, featureAlphabet, labelAlphabet);
	}
	
	/**
	 * Balances the data. Concrete subclass' balanceHook implementation will be
	 * called to apply the specific balancing algorithm.
	 * 
	 * @param n		The desired balance value (number of documents that all classes should have)
	 * @return		Balanced instancelist
	 * @throws Exception if no instancelist has been initially set
	 */
	public InstanceList balance(int n) throws Exception {
		if(this.labeledInstances==null || this.labelAlphabet==null || this.featureAlphabet==null)
			throw new Exception("DataBalancer not initialized. Please set an InstanceList.");
		
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
	
	/**
	 * Subclasses should implement the desired balancing algorithm here.
	 * Assuming algorithms work with the data in an independent way, i.e. deciding
	 * the instances to be kept depends only on one class' instances and not on
	 * previous or all classes.
	 * 
	 * @param classInstances	The instances of one class. 
	 * @param n					The number of instances that should be kept.
	 * @return					Returns the kept instances.
	 */
	protected abstract Collection<Instance> balanceHook(InstanceList classInstances, int n);
}
