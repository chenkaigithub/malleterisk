package fs.methods;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import fs.Filter;
import fs.functions.Functions;

// reference:
// A. J. Ferreira, A. T. Figueiredo, in International Workshop on 
// Pattern Recognition in Information Systems, (2010), pp. 72-81.
/**
 * TODO: 
 * This is pretty much the same as DF. 
 * 
 * But this method also removes all features with 
 * l0 == 0 or l0 == instances.size().
 * 
 */
public class FilterByRankedL0Norm1 extends Filter {
	
	public FilterByRankedL0Norm1(InstanceList instances) {
		super(instances);
	}
	
	@Override
	protected RankedFeatureVector calculate() {
		// this method (possibly) applies a feature selection step before
		// returning the ranked features (step 2. removal of non-informative features)
		
		// - step 1. compute the l0 norm of all features
		RankedFeatureVector rfv = Functions.l0norm(instances);
		
		// - step 2. remove non-informative features, i.e. l0 == 0 or l0 == n
		instances = removeNonInformativeFeatures(instances, rfv);  // NOTE: InstanceList is changed here
		
		// create a new rfv with the new data alphabet and kept values
		return new RankedFeatureVector(instances.getDataAlphabet(), rfv.getValues()); // TODO: exception (hope not, but it will happen)
	}
		
	public static final InstanceList removeNonInformativeFeatures(InstanceList instances, RankedFeatureVector rfv) {
		Alphabet alphabet = instances.getDataAlphabet();
		final int n = instances.size();
		
		// features are removed with a FeatureSelection object
		// iterate all features, check their l0norm value and keep if (> 0) && (< n)
		FeatureSelection fsRemoveNonInformative = new FeatureSelection(alphabet);
		int index = 0;
		for (double v : rfv.getValues()) {
			if(v > 0 && v < n) fsRemoveNonInformative.add(index); // features to keep
			index++;
		}
		
		// retrieve the new instances from applying the fs
		// don't apply fs if not modified (aka fs.getBitSet().cardinality() == dataAlphabet.size())
		if(fsRemoveNonInformative.getBitSet().cardinality() < alphabet.size())
			instances = Functions.fs(instances, fsRemoveNonInformative);

		return instances;
	}
	
}
