package fs.methods;

import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

/*
 * TODO:
 * normalize TF: term-frequency, count of a term;
 * freq(tj,d) / max(freq(t,d))
 * 
 * alternative tf weight: (maybe for some other IFeatureTransformer?)
 * w(t,d) = 1 + log10 tf(t,d), if tf(t,d) > 0
 *          0, otherwise
 * 
 */
public class PruneByTF implements IFeatureTransformer {
	private final double minOccurs;
	private final double maxOccurs;
	
	public PruneByTF(double minOccurs, double maxOccurs) {
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
	}
	
	@Override
	public InstanceList transform(InstanceList instances) {
		return prune(instances, minOccurs, maxOccurs);
	}
	
	public static InstanceList prune(InstanceList instances, double minOccurs, double maxOccurs) {
		RankedFeatureVector rfv = Functions.tfSum(instances);
		FeatureSelection fs = new FeatureSelection(instances.getDataAlphabet());
		
		int index = 0;
		for (double v : rfv.getValues()) {
			if(v > minOccurs && v < maxOccurs) fs.add(index); // keep
			index++;
		}
				
		return Functions.fs(instances, fs);
	}
}
