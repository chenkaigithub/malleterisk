package fs.methods;

import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import fs.Filter;
import fs.functions.Functions;

public class FilterByRankedVariance extends Filter {
	public FilterByRankedVariance(InstanceList instances) {
		super(instances);
	}

	@Override
	protected RankedFeatureVector calculate() {
		return Functions.variance(instances);
	}
}
