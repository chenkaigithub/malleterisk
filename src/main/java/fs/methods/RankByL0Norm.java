package fs.methods;

import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

/**
 * Implementation of Method 2 from [1]. A L0 norm, rank based method for 
 * feature selection. 
 * 
 * reference:
 * [1] A. J. Ferreira, A. T. Figueiredo, in International Workshop on 
 * Pattern Recognition in Information Systems, (2010), pp. 72-81.
 */
public class RankByL0Norm implements IFeatureTransformer {
	private final int numFeatures;
	
	public RankByL0Norm(int numFeatures) {
		this.numFeatures = numFeatures;
	}

	@Override
	public InstanceList transform(InstanceList instances) {
		// step 1. compute l0 norm of each feature and remove non informative features
		PruneByL0Norm pl0n = new PruneByL0Norm(numFeatures);
		instances = pl0n.transform(instances);
		
		// step 2. compute the rank ri of each feature
		RankedFeatureVector rfv = Functions.l0rank(instances);
		
		// step 3. keep only the m features with largest ranks ri
		return Functions.fs(instances, new FeatureSelection(rfv, numFeatures));
	}
}
