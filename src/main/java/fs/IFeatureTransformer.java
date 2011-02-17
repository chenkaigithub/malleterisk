package fs;

import cc.mallet.types.InstanceList;

/*
 * Interface for a feature selection/transformation/etc operation.
 */
public interface IFeatureTransformer {
	InstanceList transform(InstanceList instances);
}
