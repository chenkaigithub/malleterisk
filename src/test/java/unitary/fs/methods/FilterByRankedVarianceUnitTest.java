package unitary.fs.methods;

import java.io.File;
import java.util.Iterator;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.functions.Functions;
import fs.methods.FilterByRankedVariance;

public class FilterByRankedVarianceUnitTest {
	public static final void main(String[] args) {
		InstanceList instances = InstanceList.load(new File("instances_0-0_tests"));
		Alphabet features = instances.getDataAlphabet();
		((FeatureVector)instances.get(4).getData()).setValue(11, 10);
		for (Instance instance : instances) {
			FeatureVector v = (FeatureVector) instance.getData();
			for (Object o : features.lookupObjects(v.getIndices())) System.out.println(o+"="+v.value(o));
		}
		
		System.out.println("\n\nVARIANCE");
		Alphabet alphabet = instances.getDataAlphabet();
		Iterator<?> it = alphabet.iterator();
		while(it.hasNext()) {
			int idx = alphabet.lookupIndex(it.next());
			
			double v = Functions.variance(idx, instances);
			if(v!=0.0) System.out.println("v: "+idx+"="+v);
		}
		
		System.out.println("\n\nVARIANCE");
		InstanceList varianceInstances = new FilterByRankedVariance(5).transform(instances);
		features = varianceInstances.getDataAlphabet();
		for (Instance instance : varianceInstances) {
			FeatureVector v = (FeatureVector) instance.getData();
			int[] indices = v.getIndices();
			for (Object o : features.lookupObjects(indices)) System.out.println(o);
		}
	}
}
