package fs;

import java.util.Collection;
import java.util.LinkedList;

import cc.mallet.types.InstanceList;

/*
 * Execution pipe for a collection of IFeatureTransformer operations.
 * Executes the given operations in order. // TODO: use a List for order constraint? 
 */
public class FeatureTransformationPipeline {
	private final LinkedList<IFeatureTransformer> featureSelectors;
	
	public FeatureTransformationPipeline(Collection<IFeatureTransformer> featureSelectors) {
		this.featureSelectors = new LinkedList<IFeatureTransformer>();
		this.featureSelectors.addAll(featureSelectors);
	}
	
	public InstanceList runThruPipe(InstanceList instances) {
		for (IFeatureTransformer fs : featureSelectors) {
			instances = fs.select(instances);
		}
		
		return instances;
	}
}
