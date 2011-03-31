package ft.selection.methods;

import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import ft.selection.Filter;
import ft.selection.functions.Functions;

public class FilterByRankedTF extends Filter {
	@Override
	protected RankedFeatureVector calculate(InstanceList instances) {
		return Functions.ttf(instances);
	}
}
