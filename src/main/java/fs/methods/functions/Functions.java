package fs.methods.functions;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;

public class Functions {
	/**
	 * Applies the given feature selection method to the instance list
	 * and returns a new instance list, without the unselected features.
	 * 
	 * @param instances
	 * @param fs
	 * @return
	 */
	public static final InstanceList fs(InstanceList instances, FeatureSelection fs) {
		// create a new alphabet, an empty pipe and instancelist
		Alphabet newAlphabet = new Alphabet();
		Noop pipe = new Noop(newAlphabet, instances.getTargetAlphabet());
		InstanceList newInstances = new InstanceList (pipe);
		
		// go through all instances, create a new vector based on the old one,
		// but applying the fs method to it; create a new instance and add it to the new list
		int ii = 0;
		for (Instance instance : instances) {
			FeatureVector oldFV = (FeatureVector) instance.getData();
			FeatureVector newFV = FeatureVector.newFeatureVector (oldFV, newAlphabet, fs);
			instance.unLock();
			instance.setData(null); // allow this to get gc-ed
			
			Instance newInstance = new Instance(newFV, instance.getTarget(), instance.getName(), instance.getSource());
			newInstances.add(pipe.instanceFrom(newInstance), instances.getInstanceWeight(ii++));
		}
		
		return newInstances;
	}

	// used by tfidf 
	public static int df(int featureIdx, InstanceList instances) {
		int df = 0;
		
		for(Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			if(fv.location(featureIdx)>=0) df++; // fv.location works with Arrays.binarySearch
		}
		
		return df;
	}
	
	// Returns the document frequency of all features in the alphabet
	public static RankedFeatureVector df(InstanceList instances) {
		
		double[] dfs = new double[instances.getDataAlphabet().size()];
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			for(int idx : fv.getIndices()) dfs[idx] += 1;
		}
		
		return new RankedFeatureVector(instances.getDataAlphabet(), dfs);
	}

	// Returns the term frequency sum of all features in the alphabet
	public static RankedFeatureVector tfSum(InstanceList instances) {
		Alphabet dataAlphabet = instances.getDataAlphabet();
		// array with the same size of the alphabet
		double[] tfs = new double[dataAlphabet.size()];
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			for(int idx : fv.getIndices()) tfs[idx] += fv.value(idx);
		}
		
		return new RankedFeatureVector(dataAlphabet, tfs);
	}	
	
	
	// reference:
	// A. J. Ferreira, A. T. Figueiredo, in International Workshop on 
	// Pattern Recognition in Information Systems, (2010), pp. 72-81.
	public static final RankedFeatureVector l0norm(InstanceList instances) {
		// TODO: TBI (to be implemented)
		
		// l0norm = df?
		
		return null;
	}

	// reference:
	// A. J. Ferreira, A. T. Figueiredo, in International Workshop on 
	// Pattern Recognition in Information Systems, (2010), pp. 72-81.
	public static final void l0rank(InstanceList instances) {
		// TODO: TBI
		
		// supervised (wrapper) feature selection method
		
		// rank(t) = |l0(t, c1) - l0(t, c2) |
		
	}
}
