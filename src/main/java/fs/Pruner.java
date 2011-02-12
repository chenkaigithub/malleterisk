package fs;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class Pruner implements IFeatureSelector {
	private final double minOccurs;
	private final double maxOccurs;
	
	public Pruner(double minOccurs, double maxOccurs) {
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
	}
	
	@Override
	public InstanceList select(InstanceList instances) {
		return prune(instances, minOccurs, maxOccurs);
	}
	
	public static InstanceList prune(InstanceList instances, double minOccurs, double maxOccurs) {
		Double[] sums = sumFeatureVectors(instances);
		
		for (int i = 0; i < sums.length; i++) {
			if(sums[i] <= minOccurs) {
				for (Instance instance : instances) {
					FeatureVector fv = (FeatureVector) instance.getData();
					if(fv.location(i) >= 0) fv.setValue(i, 0);
					
					// TODO: how to remove indices and remove from alphabet?
				}
			}
		}
		
		return instances;
	}

	public static Double[] sumFeatureVectors(InstanceList instances) {
		Double[] fv_sums = new Double[instances.getDataAlphabet().size()];
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			for(int idx : fv.getIndices()) {
				double d = fv.value(idx);
				
				if(fv_sums[idx]==null) fv_sums[idx] = d;
				else fv_sums[idx] += d;
			}
		}
		
		return fv_sums;
	}
}
