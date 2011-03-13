package ft.selection.methods;

import cc.mallet.types.InfoGain;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import ft.selection.Filter;

public class FilterByRankedIG extends Filter {
	@Override
	protected RankedFeatureVector calculate(InstanceList instances) {
		return new InfoGain(instances);
	}
}
