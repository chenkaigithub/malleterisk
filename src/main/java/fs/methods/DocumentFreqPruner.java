package fs.methods;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

public class DocumentFreqPruner implements IFeatureTransformer {
	private int threshold;
	
	public DocumentFreqPruner(int threshold) {
		this.threshold = threshold;
	}
	
	@Override
	public InstanceList select(InstanceList instances) {
		return prune(threshold, instances);
	}

	public static final InstanceList prune(int threshold, InstanceList instances) {
		double[] dfs = Functions.df(instances);
		
		for (int i = 0; i < dfs.length; i++) {
			if(dfs[i] <= threshold) {
				for (Instance instance : instances) {
					FeatureVector fv = (FeatureVector) instance.getData();
					if(fv.location(i) >= 0) fv.setValue(i, 0);
				}
			}
		}
		
		return null;
	}
}
