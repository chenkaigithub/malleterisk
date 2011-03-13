package ft.selection;

import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import ft.selection.functions.Functions;

public abstract class Filter implements IFilter {
	protected InstanceList instances;
	protected RankedFeatureVector rfv;
	
	public Filter(InstanceList instances) {
		this.instances = instances;
	}

	@Override
	public InstanceList filter(int numFeatures) {
		// the calculation needs to be done only once (for the given instancelist)
		if(this.rfv==null) this.rfv = calculate();

		// the filtering can be done infinite times over the cached calculations
		// return the _HIGHEST_ ranked _numFeatures_ features
		return Functions.fs(instances, new FeatureSelection(this.rfv, numFeatures));
	}
	
	protected abstract RankedFeatureVector calculate();
	
	@Override
	public String getDescription() {
		return this.getClass().getSimpleName();
	}
}
