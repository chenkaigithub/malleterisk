package fs;

import java.util.Collection;
import java.util.LinkedList;

import cc.mallet.types.InstanceList;

public class FeatureSelectionPipe {
	private final LinkedList<IFeatureSelector> featureSelectors;
	
	public FeatureSelectionPipe(Collection<IFeatureSelector> featureSelectors) {
		this.featureSelectors = new LinkedList<IFeatureSelector>();
		this.featureSelectors.addAll(featureSelectors);
	}
	
	public InstanceList runThruPipe(InstanceList instances) {
		for (IFeatureSelector fs : featureSelectors) {
			instances = fs.select(instances);
		}
		
		return instances;
	}
}
