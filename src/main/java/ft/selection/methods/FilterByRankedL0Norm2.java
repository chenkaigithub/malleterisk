package ft.selection.methods;

import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import ft.selection.Filter;
import ft.selection.functions.Functions;

/**
 * Implementation of Method 2 from [1]. A L0 norm, rank based method for 
 * feature selection. 
 * 
 * reference:
 * [1] A. J. Ferreira, A. T. Figueiredo, in International Workshop on 
 * Pattern Recognition in Information Systems, (2010), pp. 72-81.
 */
public class FilterByRankedL0Norm2 extends Filter {
	public FilterByRankedL0Norm2(InstanceList instances) {
		super(instances);
	}

	@Override
	protected RankedFeatureVector calculate() {
		// step 1. compute l0 norm of each feature and remove non informative features
		RankedFeatureVector rfv = Functions.l0norm(instances);
		instances = FilterByRankedL0Norm1.removeNonInformativeFeatures(instances, rfv);
		
		// step 2. compute the rank ri of each feature
		// step 3. keep only the m features with largest ranks ri
		return Functions.l0rank(instances);
	}
}
