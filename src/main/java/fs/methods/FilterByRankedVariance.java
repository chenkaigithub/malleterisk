package fs.methods;

import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;
import fs.functions.Functions;

public class FilterByRankedVariance implements IFeatureTransformer {
	private final int numFeatures;
	
	public FilterByRankedVariance(int n) {
		this.numFeatures = n;
	}
	
	@Override
	public InstanceList transform(InstanceList instances) {
		FeatureSelection fs = new FeatureSelection(Functions.variance(instances), numFeatures);
		return Functions.fs(instances, fs);
	}
}
