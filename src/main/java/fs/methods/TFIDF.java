package fs.methods;

import java.util.Iterator;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

public class TFIDF implements IFeatureTransformer {
	// TODO: implement the several variants of TFIDF (ltc, etc)
	
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
			int featureIdx = alphabet.lookupIndex(feature);
			int df = Functions.df(featureIdx, instances);
			double n_df = n/df;
			
			if(n_df > 0) {
				double idf = Math.log(n_df);
				// for every document, compute the tfidf of each feature
				for (Instance instance : instances) {
					FeatureVector fv = (FeatureVector) instance.getData();
					double tf = 0;
					int location = fv.location(featureIdx);
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
}
