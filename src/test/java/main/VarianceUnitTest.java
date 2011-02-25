package main;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.SparseVector;
import cc.mallet.util.VectorStats;
import fs.methods.functions.Functions;
import gnu.trove.TIntHashSet;

public class VarianceUnitTest {
	public static final void main(String[] args) {
		/*	manually generated term occurrences:
		duke: 8.0
		citru: 6.0
		glatzer: 1.0
		schedul: 6.0
		montauk: 9.0
		settlement: 7.0
		ecoga: 4.0
		legal: 5.0
		expens: 3.0
		8: 4.0
		17: 3.0
		00: 3.0
	 */
		InstanceList instances = InstanceList.load(new File("instances_0-0_tests"));
		System.out.println("VectorStats.variance");
		VectorStats.variance(instances);
		System.out.println("VectorStats.mean");
		System.out.println(VectorStats.mean(instances).toString());
		
		System.out.println();
		Alphabet alphabet = instances.getDataAlphabet();
		Iterator<?> it = alphabet.iterator();
		while(it.hasNext()) {
			int idx = alphabet.lookupIndex(it.next());
			
			double v = Functions.variance(idx, instances);
			if(v!=0.0) System.out.println("v: "+idx+"="+v);
			
			double m = Functions.sum(idx, instances);
			if(m!=0.0) System.out.println("m: "+idx+"="+(m/(double)alphabet.size()));
		}
	}
	
	
	
	
	
	public static SparseVector variance(InstanceList instances, SparseVector mean, boolean unbiased)
	{
		double factor = 1.0 / (double) (instances.size() - (unbiased ? 1.0 : 0.0));
		System.out.println("factor = " + factor);
		SparseVector v;
		// var = (x^2 - n*mu^2)/(n-1)
		SparseVector vv = (SparseVector) mean.cloneMatrix();
		vv.timesEqualsSparse(vv, -(double) instances.size() * factor);
		Iterator<Instance> instanceItr = instances.iterator();
		Instance instance;

		while (instanceItr.hasNext()) {
			instance = (Instance) instanceItr.next();
			v = (SparseVector) ((SparseVector) (instance.getData())).cloneMatrix();
			v.timesEqualsSparse(v);
			vv.plusEqualsSparse(v, factor);
		}

		System.out.println("Var:\n" + vv);
		return vv;
	}
	
	public static SparseVector mean(InstanceList instances) {
		SparseVector v;
		int indices[];
		int maxSparseIndex = -1;
		int maxDenseIndex = -1;

		// First, we find the union of all the indices used in the instances
		TIntHashSet hIndices = new TIntHashSet(instances.getDataAlphabet().size());

		for (Instance instance : instances) {
			v = (SparseVector) (instance.getData());
			indices = v.getIndices();

			if (indices != null) {
				hIndices.addAll(indices);

				if (indices[indices.length - 1] > maxSparseIndex)
					maxSparseIndex = indices[indices.length - 1];
			} 
			else if (v.numLocations() > maxDenseIndex)
				maxDenseIndex = v.numLocations() - 1;
		}

		if (maxDenseIndex > -1) // dense vectors were present
		{
			if (maxSparseIndex > maxDenseIndex)
			// sparse vectors were present and they had greater indices than
			// the dense vectors
			{
				// therefore, we create sparse vectors and
				// add all the dense indices
				for (int i = 0; i <= maxDenseIndex; i++)
					hIndices.add(i);
			} else
			// sparse indices may have been present, but we don't care
			// since they never had indices that exceeded those of the
			// dense vectors
			{
				return mean(instances, maxDenseIndex + 1);
			}
		}

		// reaching this statement implies we can create a sparse vector
		return mean(instances, hIndices.toArray());
	}
	
	public static SparseVector mean(InstanceList instances, int numIndices) {
		SparseVector mv = new SparseVector(new double[numIndices], false);

		return mean(instances, mv);
	}
	
	public static SparseVector mean(InstanceList instances, int[] indices) {
		Arrays.sort(indices);
		SparseVector mv = new SparseVector(indices, new double[indices.length], false, false, false);

		return mean(instances, mv);
	}
	
	private static SparseVector mean(InstanceList instances, SparseVector meanVector) {
		SparseVector v;
		double factor = 1.0 / (double) instances.size();

		for (Instance instance : instances) {
			v = (SparseVector) (instance.getData());

			meanVector.plusEqualsSparse(v, factor);
		}

		return meanVector;
	}
}
