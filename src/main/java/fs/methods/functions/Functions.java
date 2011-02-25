package fs.methods.functions;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.RankedFeatureVector;
import cc.mallet.types.SparseVector;

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

	//
	// Document Frequency (DF)
	//
	
	// used by tfidf 
	public static final double df(int featureIdx, InstanceList instances) {
		double df = 0.0;
		
		for(Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			if(fv.location(featureIdx)>=0) df++; // fv.location works with Arrays.binarySearch
		}
		
		return df;
	}
	
	// Returns the document frequency of all features in the alphabet
	public static final RankedFeatureVector df(InstanceList instances) {
		
		double[] dfs = new double[instances.getDataAlphabet().size()];
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			for(int idx : fv.getIndices()) dfs[idx] += 1;
		}
		
		return new RankedFeatureVector(instances.getDataAlphabet(), dfs);
	}

	//
	// Total Term Frequency
	//
	
	// Returns the term frequency sum for all features in the alphabet.
	public static RankedFeatureVector ttf(InstanceList instances) {
		Alphabet dataAlphabet = instances.getDataAlphabet();
		RankedFeatureVector ttf = new RankedFeatureVector(dataAlphabet, new double[instances.getAlphabet().size()]);
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			ttf.vectorAdd(fv, 1);
		}
		
		return ttf;
	}	
	
	//
	// L0-Norm
	//
	
	public static final RankedFeatureVector l0norm(InstanceList instances) {
		return df(instances); // l0-norm = document frequency
	}
	
	//
	// L0-Rank
	//
	
	// TODO: 
	// this is an inefficient implementation
	// maybe I should create a matrix of features x classes
	// where each cell = l0(feature, class)
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
					r[featureIdx] += Math.abs(
						constrainedl0norm(featureIdx, classLIdx, instances) 
						- 
						constrainedl0norm(featureIdx, classKIdx, instances)
					);
				}
			}
		}
		
		return new RankedFeatureVector(featuresAlphabet, r);
	}

	// Calculates the L0 norm value of a feature, constrained by a class.
	// I.e. the number of documents of the given class where the feature occurs.
	public static final int constrainedl0norm(int featureIdx, int classIdx, InstanceList instances) {
		int l0norm = 0;
		
		for (Instance instance : instances)
			if(((Label)instance.getTarget()).getBestIndex() == classIdx) {
				FeatureVector fv = (FeatureVector)instance.getData();
				if(fv.value(featureIdx) > 0) l0norm += 1;
			}
		
		return l0norm;
	}
	
	//
	// Feature Variance
	//
	
	public static final RankedFeatureVector variance(InstanceList instances) {
		Alphabet featuresAlphabet = instances.getDataAlphabet();
		final double[] r = new double[featuresAlphabet.size()];
		
		for (Object feature : featuresAlphabet.toArray()) {
			int featureIdx = featuresAlphabet.lookupIndex(feature);
			r[featureIdx] = variance(featureIdx, instances);
		}
		
		return new RankedFeatureVector(featuresAlphabet, r);
	}
	
	public static final double variance(int featureIdx, InstanceList instances) {
		final double p = p(featureIdx, instances);
		
		double e = 0;
		double m = 0;
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			
			e += Math.pow(fv.value(featureIdx), 2);
			m += fv.value(featureIdx);
		}
		
		e *= p;
		m *= p;
		
		return e-m;
	}
	
	public static final double p(int featureIdx, InstanceList instances) {
		double tf = 0;
		SparseVector ttf = new SparseVector(new double[instances.getAlphabet().size()], false);
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			tf += fv.value(featureIdx);
			ttf = ttf.vectorAdd(fv, 1);
		}
		
		double sums = 0;
		for(int idx : ttf.getIndices()) sums += ttf.value(idx);
		
		return tf/sums;
	}
	
	//
	// Fisher's Criterion
	//
	
	public static final double fisher() {
		
		
		return 0;
	}

	
	
	// TODO:
	// CTD (categorical descriptor term)
	// SCIW (strong class information word)
	// TS (term strength)
	// CHI
	// TC (term contribution)
	
	/*
		f(ti, dj) = tf(ti,dj) * log(N / df(ti, dj))
		sim(di, dj) = sum[i ... m] ( f(t, di) * f(t, dj) )
		
		TC(tk) = sum[i, j] ( f(tk, di) * f(tk, dj) )
		
		TVQ:
		q(ti) = (sum[j=1 .. n] fij^2) - (1/n)(sum[j=1 .. n] fij)^2
		n = number of documents in which t occurs at least once
		fij >= 1
		j=1 ... n
		
		TV:
		v(ti) = sum[j=1 .. N] (fij - avg_fi)^2
	 */
}
