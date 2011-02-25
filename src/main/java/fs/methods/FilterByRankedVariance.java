package fs.methods;

import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

public class FilterByRankedVariance implements IFeatureTransformer {
	private final int numFeatures;
	
	public FilterByRankedVariance(int n) {
		this.numFeatures = n;
	}
	
	@Override
	public InstanceList transform(InstanceList instances) {
		return Functions.fs(instances, new FeatureSelection(Functions.variance(instances), numFeatures));
	}

}