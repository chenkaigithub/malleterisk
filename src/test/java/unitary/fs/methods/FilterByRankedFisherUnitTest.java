package unitary.fs.methods;

import java.io.File;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.methods.FilterByRankedFisher;


public class FilterByRankedFisherUnitTest {
//	public static final void main(String[] args) {
//		InstanceList instances = InstanceList.load(new File("instances_0-0_tests"));
//		Alphabet features = instances.getDataAlphabet();
//				
//		for (Instance instance : instances) {
//			FeatureVector v = (FeatureVector) instance.getData();
//			for (Object o : features.lookupObjects(v.getIndices())) System.out.println(o+"="+v.value(o));
//		}
//		
//		System.out.println("\n\nMINIMUM_SCORE");
//		InstanceList rfv1 = new FilterByRankedFisher(5, FilterByRankedFisher.MINIMUM_SCORE).transform(instances);
//		features = rfv1.getDataAlphabet();
//		for (Instance instance : rfv1) {
//			FeatureVector v = (FeatureVector) instance.getData();
//			for (Object o : features.lookupObjects(v.getIndices())) System.out.println(o+"="+v.value(o));
//		}
//		
//		System.out.println("\n\nSUM_SCORE");
//		InstanceList rfv2 = new FilterByRankedFisher(5, FilterByRankedFisher.SUM_SCORE).transform(instances);
//		features = rfv2.getDataAlphabet();
//		for (Instance instance : rfv2) {
//			FeatureVector v = (FeatureVector) instance.getData();
//			for (Object o : features.lookupObjects(v.getIndices())) System.out.println(o+"="+v.value(o));
//		}
//
//		System.out.println("\n\nSUM_SQUARED_SCORE");
//		InstanceList rfv3 = new FilterByRankedFisher(5, FilterByRankedFisher.SUM_SQUARED_SCORE).transform(instances);
//		features = rfv3.getDataAlphabet();
//		for (Instance instance : rfv3) {
//			FeatureVector v = (FeatureVector) instance.getData();
//			for (Object o : features.lookupObjects(v.getIndices())) System.out.println(o+"="+v.value(o));
//		}
//	}
}
