package fs.functions;

import java.util.Arrays;

import types.mallet.LabeledInstancesList;
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
		SparseVector sv = new SparseVector(new double[instances.getAlphabet().size()], false);
		for (Instance instance : instances)
			sv.plusEqualsSparse((FeatureVector) instance.getData());
		
		return new RankedFeatureVector(dataAlphabet, sv.getValues());
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

	public static final RankedFeatureVector l0rank(InstanceList instances) {
		LabeledInstancesList lil = new LabeledInstancesList(instances);
		
		Alphabet features = instances.getDataAlphabet();
		Alphabet labels = instances.getTargetAlphabet();
		int K = labels.size();
		
		SparseVector sv = new SparseVector(new double[features.size()], false);
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < K; j++) {
				sv.plusEqualsSparse(rank(lil, i, j));
			}
		}
		
		return new RankedFeatureVector(features, sv);
	}
	
	private static final SparseVector rank(LabeledInstancesList lil, int class1idx, int class2idx) {
		InstanceList cls1instances = lil.getInstances(class1idx);
		InstanceList cls2instances = lil.getInstances(class2idx);
		
		// Calculate the L0 norm value of a feature, constrained by a class.
		// I.e. the number of documents of the given class where the feature occurs.
		// there's no real constraint since we're passing the instances of class X 
		SparseVector sv = l0(cls1instances);
		sv.plusEqualsSparse(l0(cls2instances));
		
		return sv;
	}

	// need an unraked vector
	private static final SparseVector l0(InstanceList instances) {
		double[] dfs = new double[instances.getDataAlphabet().size()];

		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			for(int idx : fv.getIndices()) dfs[idx] += 1;
		}

		return new SparseVector(dfs, false);
	}

	//
	// Feature Variance
	//
	
	public static final RankedFeatureVector variance(InstanceList instances) {
		Alphabet alphabet = instances.getAlphabet();
		// Var(x) = E[(x-m)^2] = E[x^2] - m^2 = E[x^2] - E[x]^2
		// E[x] = Sum[x * p(x)] = Sum(x) * p(x)
		
		// calculate:
		// Sum(x^2) for E[x^2]
		// Sum(x) for E[x] and p(x) 
		int s = alphabet.size();
		SparseVector x2 = new SparseVector(new double[s], false);
		SparseVector m = new SparseVector(new double[s], false);
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			
			// Sum(x^2)
			SparseVector xi2 = (SparseVector)fv.cloneMatrix();
			xi2.timesEqualsSparse(xi2);
			x2.plusEqualsSparse(xi2);
			
			// Sum(x)
			m.plusEqualsSparse(fv);
		}
		
		SparseVector p = p(m, tdtf(m));	// p(x) = Sum(x) / Sum(total_x)
		x2.timesEqualsSparse(p);		// E[x^2] = Sum(x^2) * p(x)
		m.timesEqualsSparse(p);			// E[x] = Sum(x) * p(x)
		m.timesEqualsSparse(m);			// E[x]^2 = E[x] * E[x]
		x2.plusEqualsSparse(m, -1); 	// Var(x) = E[x^2] - E[x]^2
		
		return new RankedFeatureVector(alphabet, x2);
	}
	
	public static final SparseVector p(SparseVector tf, double tdtf) {
		SparseVector p = (SparseVector)tf.cloneMatrix();
		p.timesEquals(1.0/tdtf);
		return p;
	}
		
	//
	// Fisher's Criterion
	//
	
	public static final FeatureVector fisher(InstanceList instances, int class1idx, int class2idx) {
		LabeledInstancesList lil = new LabeledInstancesList(instances);
		InstanceList cls1instances = lil.getInstances(class1idx);
		InstanceList cls2instances = lil.getInstances(class2idx);
		final double n1 = cls1instances.size();
		final double n2 = cls2instances.size();
		
		// n1 * n2 * ((n2 * sum(cls1instances, tf, tdtf)) - (n1 * sum(cls2instances, tf, tdtf)))^2
		final SparseVector evClass1 = conditionalExpectedValue(cls1instances);
		final SparseVector evClass2 = conditionalExpectedValue(cls2instances);
		
		final SparseVector varClass1 = conditionalVariance(cls1instances, evClass1);
		final SparseVector varClass2 = conditionalVariance(cls2instances, evClass2);
		
		evClass1.timesEquals(n2);
		evClass2.timesEquals(n1);
		
		SparseVector numerator = evClass1.vectorAdd(evClass2, -1.0);
		numerator.timesEqualsSparse(numerator);
		numerator.timesEquals(n1*n2);
		
		varClass1.timesEquals(n2*n2);
		varClass2.timesEquals(n1*n1);
		SparseVector denominator = varClass1.vectorAdd(varClass2, 1);
		
		Alphabet alphabet = instances.getDataAlphabet();
		double[] values = new double[alphabet.size()];
		int[] indices = numerator.getIndices();
		
		for (int i : indices) values[i] = numerator.value(i) / denominator.value(i); 
		
		return new FeatureVector(alphabet, values);
	}
	
	public static final SparseVector conditionalExpectedValue(InstanceList clsInstances) {
		SparseVector sv = new SparseVector(new double[clsInstances.getAlphabet().size()], false);
		
		for (Instance instance : clsInstances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			final double itdtf = 1.0 / tdtf(fv);
			sv.plusEqualsSparse(fv, itdtf);
		}
		
		return sv;
	}
	
	public static final SparseVector conditionalVariance(InstanceList clsInstances, SparseVector ev) {
		SparseVector sv = new SparseVector(new double[clsInstances.getAlphabet().size()], false);
		final double n = clsInstances.size();
		
		for (Instance instance : clsInstances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			
			SparseVector vv = (SparseVector) fv.cloneMatrix();
			vv.timesEquals(n/tdtf(fv));
			vv.plusEqualsSparse(ev, -1);
			vv.timesEqualsSparse(vv);
			
			sv.plusEqualsSparse(vv);
		}
		
		return sv;
	}
	
	// total document term frequency
	public static final double tdtf(SparseVector m) {
		double s = 0;
		for(double d : m.getValues()) s+= d;
		return s;
	}
	
	//
	// 
	//
	
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
