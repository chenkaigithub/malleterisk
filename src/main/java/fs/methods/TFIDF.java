package fs.methods;

import java.util.Iterator;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;
import fs.methods.functions.Functions;

public class TFIDF implements IFeatureTransformer {
	// TODO: kinda inefficient - think abut a new solution
	// TODO: implement the several variants of TFIDF?
	// tfidf = ntf * log(N/N(t))
	// tfidf ltc = tf(t, d) * log(N/N(t))
	// tfidf nltc = tfidf ltc / sqrt( sum(tfidf.ltc^2) )
	// in: Natural language processing and text mining edited by Anne Kao, Stephen R. Poteet
	//
	// more definitions in:
	// http://nlp.stanford.edu/IR-book/html/htmledition/variant-tf-idf-functions-1.html
	// http://nlp.stanford.edu/IR-book/html/htmledition/document-and-query-weighting-schemes-1.html 
	// https://openaccess.leidenuniv.nl/bitstream/1887/13575/8/Appendix%2BB.pdf

	@Override
	public InstanceList transform(InstanceList instances) {
		return tfidf(instances);
	}

	public static InstanceList tfidf(InstanceList instances) {
		int n = instances.size();
		
		// iterate features
		Alphabet alphabet = instances.getDataAlphabet();
		@SuppressWarnings("unchecked")
		Iterator<Object> it = alphabet.iterator();
		while(it.hasNext()) {
			Object feature = it.next();
			int featureIdx = alphabet.lookupIndex(feature);
			int df = Functions.df(featureIdx, instances);
			double n_df = n/(1+df);
			
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
