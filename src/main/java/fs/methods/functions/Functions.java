package fs.methods.functions;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class Functions {
	public static int df(int featureIdx, InstanceList instances) {
		int df = 0;
		
		for(Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			if(fv.location(featureIdx)>=0) df++; // fv.location works with Arrays.binarySearch
		}
		
		return df;
	}
	
	// Returns the document frequency of all features in the alphabet
	public static double[] df(InstanceList instances) {
		double[] dfs = new double[instances.getDataAlphabet().size()];
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			for(int idx : fv.getIndices()) dfs[idx] += 1;
		}
		
		return dfs;
	}

	// Returns the term frequency sum of all features in the alphabet
	public static double[] tfSum(InstanceList instances) {
		// array with the same size of the alphabet
		double[] tfs = new double[instances.getDataAlphabet().size()];
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			for(int idx : fv.getIndices()) tfs[idx] += fv.value(idx);
		}
		
		return tfs;
	}
	
	
}
