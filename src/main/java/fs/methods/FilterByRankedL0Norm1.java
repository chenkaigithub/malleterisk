package fs.methods;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import fs.IFeatureTransformer;
import fs.functions.Functions;

// reference:
// A. J. Ferreira, A. T. Figueiredo, in International Workshop on 
// Pattern Recognition in Information Systems, (2010), pp. 72-81.
/*
 * TODO: 
 * This is pretty much the same as DF. 
 * I'm just not sure if l0 == df.
 * 
 * But this method also removes all features with 
 * l0 == 0 or l0 == instances.size().
 * 
 */
public class FilterByRankedL0Norm1 implements IFeatureTransformer {
	private final int numFeatures;
	
	public FilterByRankedL0Norm1(int numFeatures) {
		this.numFeatures = numFeatures;
	}
	
	@Override
	public InstanceList transform(InstanceList instances) {
		// - step 1. compute the l0 norm of all features
		RankedFeatureVector rfv = Functions.l0norm(instances);
		
		// - step 2. remove non-informative features, i.e. l0 == 0 or l0 == n
		// we will need the ranked vector for later, and it must be consistent with
		// the 'current' alphabet (i.e. without the removed terms)
		Alphabet alphabet = instances.getDataAlphabet();
		final int n = instances.size();
		final int p = alphabet.size();
		double[] values = new double[p];
		FeatureSelection fsRemoveNonInformative = new FeatureSelection(alphabet);
		int index = 0;
		for (double v : rfv.getValues()) {
			if(v > 0 && v < n) {  // features to keep
				fsRemoveNonInformative.add(index);
				values[index] = v;
			}
			index++;
		}
		
		// retrieve the new instances from applying the fs; the alphabet is presumably new
		// create the new ranked feature vector with the kept values
		// don't apply fs if not modified (aka fs.getBitSet().cardinality() == dataAlphabet.size())
		if(fsRemoveNonInformative.getBitSet().cardinality() < p) {
			instances = Functions.fs(instances, fsRemoveNonInformative);
			rfv = new RankedFeatureVector(instances.getDataAlphabet(), values);
		}
		values = null;
		rankedFeatureVector = rfv;
		
		// - step 3. keep only the 'm' features with largest l0 norm		
		return Functions.fs(instances, new FeatureSelection(rfv, numFeatures));
	}
	
	// test stuff
	private RankedFeatureVector rankedFeatureVector;
	
	public RankedFeatureVector getRankedFeatureVector() {
		return rankedFeatureVector;
	}
}
