package fs.methods;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

// normalize TF: term-frequency, count of a term; freq(tj,d)/max(freq(t,d))
//w(t,d) = 1 + log10 tf(t,d), if tf(t,d) > 0
//         0, otherwise
public class TermFreqPruner implements IFeatureTransformer {
	private final double minOccurs;
	private final double maxOccurs;
	
	public TermFreqPruner(double minOccurs, double maxOccurs) {
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
	}
	
	@Override
	public InstanceList select(InstanceList instances) {
		return prune(instances, minOccurs, maxOccurs);
	}
	
	public static InstanceList prune(InstanceList instances, double minOccurs, double maxOccurs) {
		double[] sums = Functions.tfSum(instances);
		
		for (int i = 0; i < sums.length; i++) {
			if(sums[i] <= minOccurs || sums[i] >= maxOccurs) {					// tf is not high enough or it's too high
				// TODO: how to remove indices and remove from alphabet?
				// do I need to create a new alphabet? 
				// and then how do I affect all instances at once?
				for (Instance instance : instances) {							// nullify the term in all documents (frequency = 0)
					FeatureVector fv = (FeatureVector) instance.getData();
					if(fv.location(i) >= 0) fv.setValue(i, 0);
				}
			}
		}
		
		return instances;
	}
}
