package fs;

import java.util.Collection;
import java.util.LinkedList;

import cc.mallet.types.InstanceList;

/*
 * Execution pipe for a collection of IFeatureTransformer operations.
 * Executes the given operations in order. TODO: use a List for order constraint? 
 * 
 * TODO: it would be great if I could somehow aggregate into here
 * all loop operations (i.e. avoid the heavy processing whenever possible)
 * doubt it will be possible given the way things are built.. 
 */
public class FeatureTransformationPipeline {
	private final LinkedList<IFeatureTransformer> featureSelectors;
	
	public FeatureTransformationPipeline(Collection<IFeatureTransformer> featureSelectors) {
		this.featureSelectors = new LinkedList<IFeatureTransformer>();
		this.featureSelectors.addAll(featureSelectors);
	}
	
	public InstanceList runThruPipeline(InstanceList instances) {
		for (IFeatureTransformer fs : featureSelectors) {
			instances = fs.transform(instances);
		}
		
		return instances;
	}
}
