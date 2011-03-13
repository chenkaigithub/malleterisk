package ft.selection.methods;

import cc.mallet.types.InfoGain;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import ft.selection.Filter;

public class FilterByRankedIG extends Filter {
	public FilterByRankedIG(InstanceList instances) {
		super(instances);
	}
	
	@Override
	protected RankedFeatureVector calculate() {
		return new InfoGain(instances);
	}
}
