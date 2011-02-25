package fs.methods;

import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InfoGain;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

public class FilterByRankedIG implements IFeatureTransformer {
	private int numFeatures;

	public FilterByRankedIG(int numFeatures) {
		this.numFeatures = numFeatures;
	}
	
	@Override
	public InstanceList transform(InstanceList instances) {
		return Functions.fs(instances, new FeatureSelection (new InfoGain (instances), numFeatures));
	}
}