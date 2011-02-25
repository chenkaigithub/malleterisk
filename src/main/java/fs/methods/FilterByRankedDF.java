package fs.methods;

import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

// TODO: look at FeatureCounts
public class FilterByRankedDF implements IFeatureTransformer {
	private int numFeatures;
	
	public FilterByRankedDF(int numFeatures) {
		this.numFeatures = numFeatures;
	}
	
	@Override
	public InstanceList transform(InstanceList instances) {
		return Functions.fs(instances, new FeatureSelection(Functions.df(instances), numFeatures));
	}
}
