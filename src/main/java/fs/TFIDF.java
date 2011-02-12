package fs;

import java.util.Iterator;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class TFIDF implements IFeatureSelector {
	@Override
	public InstanceList select(InstanceList instances) {
		return tfidf(instances);
	}

	public static InstanceList tfidf(InstanceList instances) {
		// TODO: normalize tfidf?
		int n = instances.size();
		
		// iterate features
		Alphabet alphabet = instances.getDataAlphabet();
		@SuppressWarnings("unchecked")
		Iterator<Object> it = alphabet.iterator();
		while(it.hasNext()) {
			Object feature = it.next();
			int f_idx = alphabet.lookupIndex(feature);
			int df = df(f_idx, instances);
			double n_df = n/df;
			
			if(n_df > 0) {
				double idf = Math.log(n_df);
				// for every document, compute the tfidf of each feature
				for (Instance instance : instances) {
					FeatureVector fv = (FeatureVector) instance.getData();
					double tf = 0;
					int location = fv.location(f_idx);
					if(location>=0) {
						tf = fv.valueAtLocation(location);
						double tfidf = tf*idf;
						fv.setValueAtLocation(location, tfidf);
					}
				}
			}
		}
		
		return instances;
	}

	// Returns the document frequency of the specified feature,
	// i.e. the number of documents the feature is present in.
	public static int df(int f_idx, InstanceList instances) {
		int df = 0;
		
		for(Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			if(fv.location(f_idx)>=0) df++; // fv.location works with Arrays.binarySearch
		}
		
		return df;
	}
}
