package ft.selection.methods;

import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import ft.selection.Filter;
import ft.selection.functions.Functions;

public class FilterByRankedVariance extends Filter {
	public FilterByRankedVariance(InstanceList instances) {
		super(instances);
	}

	@Override
	protected RankedFeatureVector calculate() {
		return Functions.variance(instances);
	}
}
