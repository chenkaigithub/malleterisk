package fs;

import cc.mallet.types.InstanceList;

public interface IFeatureSelector {
	InstanceList select(InstanceList instances);
}
