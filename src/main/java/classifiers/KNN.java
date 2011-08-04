package classifiers;

import java.util.Arrays;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Metric;
import cc.mallet.types.NormalizedDotProductMetric;

// TODO: KNNTrainer and KNN, extends Trainer, etc
public class KNN {
	// TODO: extract cosine (either receive the metric in the method or in a ctor)
	private static final Metric cosine = new NormalizedDotProductMetric();
	
	public static Instance[] knn(int k, Instance instance, InstanceList instances) {
		if(k >= instances.size()) return instances.toArray(new Instance[0]);
		
		KInstanceCollection kic = new KInstanceCollection(k);
		
		FeatureVector instanceVector = (FeatureVector)instance.getData();
		for (Instance inst : instances) {
			if(!instance.equals(inst)) {
				// key: distance, value: instance
				double d = cosine.distance(instanceVector, (FeatureVector)inst.getData());
				kic.put(d, inst);
			}
		}
		
		return kic.getInstances();
	}
}

class KInstanceCollection {
	// this only stores K items, it's hardly worth it to optimize this
	// (e.g. by keeping the arrays sorted)
	private final double[] costs;
	private final Instance[] items;
	
	public KInstanceCollection(int maxN) {
		this.costs = new double[maxN];
		this.items = new Instance[maxN];
		Arrays.fill(costs, Double.MAX_VALUE);
	}
	
	public boolean put(double cost, Instance item) {
		boolean added = false;
		
		int idx = isLesserCost(cost, costs);
		if(idx!=-1) {
			costs[idx] = cost;
			items[idx] = item;
			added = true;
		}
		
		return added;
	}
	
	public Instance[] getInstances() {
		return items;
	}
	
	private static final int isLesserCost(double cost, double[] costs) {
		for (int i=0; i<costs.length; ++i) {
			if(costs[i] > cost)
				return i;
		}
		
		return -1;
	}
}
