package imbalance;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import types.mallet.LabeledInstancesList;
import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

/**
 * Performs class balancing using random under and oversampling.
 * 
 * 
 * @author tt
 *
 */
public class RandomSampler {
	private final Alphabet featureAlphabet;
	private final Alphabet labelAlphabet;
	public final LabeledInstancesList labeledInstances;
	public final int minThreshold;
	
	public RandomSampler(InstanceList instances, int minThreshold) {
		this.featureAlphabet = instances.getDataAlphabet();
		this.labelAlphabet = instances.getTargetAlphabet();
		this.labeledInstances = new LabeledInstancesList(instances, featureAlphabet, labelAlphabet);
		this.minThreshold = minThreshold;
	}
	
	public InstanceList sample(int n) {
		Noop pipe = new Noop(new Alphabet(), this.labelAlphabet);
		InstanceList newInstanceList = new InstanceList (pipe);
		
		Collection<Instance> ilist;
		for (InstanceList instances : labeledInstances.getLabeledInstances()) {
			// class does not have enough documents to process
			if(instances.size() < this.minThreshold) continue;
			
			// random sample with reposition if size < n; otherwise, no reposition 
			if(instances.size() > n) ilist = rs(instances, n, false);
			else ilist = rs(instances, n, true);
			
			for (Instance instance : ilist) newInstanceList.addThruPipe(instance);
		}
		
		return newInstanceList;
	}
	
	private Collection<Instance> rs(InstanceList instances, int n, boolean reposition) {
		LinkedList<Instance> sampledInstances = new LinkedList<Instance>();
		int k = instances.size();
		Random r = new Random();
		
		for (int i = 0; i < n; i++) {
			Instance instance =instances.get(r.nextInt(k));
			if(!reposition && sampledInstances.contains(instance)) i--;
			else sampledInstances.add(instance);
		}
		
		return sampledInstances;
	}
}
