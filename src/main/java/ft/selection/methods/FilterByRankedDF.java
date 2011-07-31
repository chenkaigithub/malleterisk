package ft.selection.methods;

import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import ft.selection.Filter;
import ft.selection.functions.Functions;

// TODO: look at Mallet's FeatureCounts
public class FilterByRankedDF extends Filter {
	@Override
	protected RankedFeatureVector calculate(InstanceList instances) {
		return Functions.df(instances);
	}
}
