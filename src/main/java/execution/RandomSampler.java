package execution;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import types.mallet.LabeledInstancesList;
import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class RandomSampler implements Iterable<InstanceList>, Iterator<InstanceList> {
	private final Alphabet featureAlphabet;
	private final Alphabet labelAlphabet;
	private final LabeledInstancesList labeledInstances;
	private final int minThreshold;
	
	public RandomSampler(InstanceList instances, int minThreshold) {
		this.featureAlphabet = instances.getDataAlphabet();
		this.labelAlphabet = instances.getTargetAlphabet();
		this.labeledInstances = new LabeledInstancesList(instances, featureAlphabet, labelAlphabet);
		this.minThreshold = minThreshold;
	}
	
	public InstanceList x(int n) {
		Noop pipe = new Noop(new Alphabet(), this.labelAlphabet);
		InstanceList newInstanceList = new InstanceList (pipe);
		
		Collection<Instance> ilist;
		for (InstanceList instances : labeledInstances.getLabeledInstances()) {
			// class does not have enough documents to process
			if(instances.size() < this.minThreshold) continue;
			
			// random sample with reposition if size < n; otherwise, no reposition 
			if(instances.size() > n) ilist = rsample(instances, n, false);
			else ilist = rsample(instances, n, true);
			
			for (Instance instance : ilist) newInstanceList.addThruPipe(instance);
		}
		
		return newInstanceList;
	}
	
	private Collection<Instance> rsample(InstanceList instances, int n, boolean reposition) {
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
	
	// Iterable

	@Override
	public boolean hasNext() {
		
		throw new NotImplementedException();
	}

	@Override
	public InstanceList next() {
		throw new NotImplementedException();
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}

	// Iterator
	
	@Override
	public Iterator<InstanceList> iterator() {
		return this;
	}
}
