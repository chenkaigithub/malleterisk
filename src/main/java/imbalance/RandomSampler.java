package imbalance;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

/**
 * Performs class balancing using random under and oversampling.
 * 
 * - Classes with more instances than given N are undersampled (a subset of the 
 * instances is randomly selected).
 * - Classes with less instances than given N are oversampled (a set of the 
 * instances is randomly and repeatedly selected).
 * 
 * When undersampling, repeated instances are _added_ to the existent instances.
 * 
 * @author tt
 */
public class RandomSampler extends Balancer {
	public static final Random r = new Random();
	
	public RandomSampler(int minThreshold) {
		super(minThreshold);
	}
	
//	public RandomSampler(InstanceList instances, int minThreshold) {
//		super(instances, minThreshold);
//	}
	
	@Override
	protected Collection<Instance> balanceHook(InstanceList classInstances, int n) {
		return sample(classInstances, n);
	}
	
	public Collection<Instance> sample(InstanceList classInstances, int n) {
		// random sample with reposition if size < n; otherwise, no reposition 
		Collection<Instance> ilist;
		if(classInstances.size() > n) ilist = rs(classInstances, n, false);
		else ilist = rs(classInstances, n, true);
		return ilist;
	}
	
	private Collection<Instance> rs(InstanceList instances, int n, boolean reposition) {
		LinkedList<Instance> sampledInstances = new LinkedList<Instance>();
		int k = instances.size(); // number of samples
		
		// when oversampling, keep all of the original samples and only sample until (N - instances.size())
		// this way we avoid the chance of randomly not selecting given instances
		if(reposition) {
			sampledInstances.addAll(instances);
			n -= sampledInstances.size();
		}
		
		for (int i = 0; i < n; i++) {
			// grab a random sample
			Instance instance =instances.get(r.nextInt(k));
			
			// when sampling with no reposition, verify if it is repeated
			// in such case, re-sample until a new one is found; finally add it
			if(!reposition && sampledInstances.contains(instance)) i--; 
			else sampledInstances.add(instance);
		}
		
		return sampledInstances;
	}
}
