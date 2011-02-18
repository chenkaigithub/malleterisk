package fs.methods.functions;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
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
			FeatureVector newFV = FeatureVector.newFeatureVector((FeatureVector)instance.getData(), newAlphabet, fs);
			
			newInstances.add(
				pipe.instanceFrom(new Instance(newFV, instance.getTarget(), instance.getName(), instance.getSource())), 
				instances.getInstanceWeight(ii++)
			);
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
	
	public static final RankedFeatureVector l0norm(InstanceList instances) {
		return df(instances); // TODO: l0norm = df?
	}
		
	// TODO: inefficient implementation
	// create a matrix of features x classes where each cell = l0(feature, class) ?
	public static final RankedFeatureVector l0rank(InstanceList instances) {
		Alphabet featuresAlphabet = instances.getDataAlphabet();
		Alphabet classesAlphabet = instances.getTargetAlphabet();
		
		double[] r = new double[featuresAlphabet.size()];
		
		Object[] features = featuresAlphabet.toArray();
		Object[] classes = classesAlphabet.toArray();

		for (Object feature : features) {
			int featureIdx = featuresAlphabet.lookupIndex(feature);
			for (Object classL : classes) {
				int classLIdx = classesAlphabet.lookupIndex(classL);
				for (Object classK : classes) {
					int classKIdx = classesAlphabet.lookupIndex(classK);
					r[featureIdx] += Math.abs(l0norm(featureIdx, classLIdx, instances) - l0norm(featureIdx, classKIdx, instances));
				}
			}
		}
		
		return new RankedFeatureVector(featuresAlphabet, r);
	}
	
	/**
	 * Calculates the L0 norm value of a feature, constrained by a class.
	 * I.e. the number of documents of the given class where the feature occurs.
	 * 
	 * @param featureIdx
	 * @param classIdx
	 * @param instances
	 * @return
	 */
	public static int l0norm(int featureIdx, int classIdx, InstanceList instances) {
		int l0norm = 0;
		
		for (Instance instance : instances)
			if(((Label)instance.getTarget()).getBestIndex() == classIdx) {
				FeatureVector fv = (FeatureVector)instance.getData();
				if(fv.value(featureIdx) > 0) l0norm += 1;
			}
		
		return l0norm;
	}
	
	// TODO:
	// CTD (categorical descriptor term)
	// SCIW (strong class information word)
	// TS (term strength)
	// CHI
	// TC (term contribution)
	// variance
}
