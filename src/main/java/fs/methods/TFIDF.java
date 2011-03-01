package fs.methods;

import java.util.TreeMap;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.functions.Functions;

public class TFIDF {
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

	public InstanceList calculate(InstanceList instances) {
		return tfidf(instances);
	}

	public static final InstanceList tfidf(InstanceList instances) {
		Alphabet alphabet = instances.getDataAlphabet();
		InstanceList newInstances = new InstanceList(alphabet, instances.getTargetAlphabet());
		
		// cache the idfs
		TreeMap<Integer, Double> idfs = new TreeMap<Integer, Double>();
		
		// number of documents
		double n = instances.size();
		
		for (Instance instance : instances) {
			// get features of current instance
			FeatureVector fv = (FeatureVector) instance.getData();
			int[] indices = fv.getIndices();
			
			double[] tfidfValues = new double[indices.length];
			int i = 0;
			for(int idx : indices) {
				final double tf = fv.value(idx);
				Double idf; // check the cache for the value
				if((idf=idfs.get(idx))==null) {
					idf = Math.log10(n / (1.0+Functions.df(idx, instances)));
					idfs.put(idx, idf);
				}
				
				tfidfValues[i++] = tf*idf;
			}
			
			newInstances.add(new Instance(
				new FeatureVector(instance.getDataAlphabet(), indices, tfidfValues), 
				instance.getTarget(), 
				instance.getName(), 
				instance.getSource()
			));
		}
		
		return newInstances;
	}
}
