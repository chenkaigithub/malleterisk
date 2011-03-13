package ft.selection.methods;

import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import ft.selection.Filter;
import ft.selection.functions.Functions;

// TODO: look at FeatureCounts
public class FilterByRankedDF extends Filter {
	public FilterByRankedDF(InstanceList instances) {
		super(instances);
	}

	@Override
	protected RankedFeatureVector calculate() {
		return Functions.df(instances);
	}
}
