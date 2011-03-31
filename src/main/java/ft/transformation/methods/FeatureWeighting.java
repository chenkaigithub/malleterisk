package ft.transformation.methods;

import java.util.TreeMap;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import ft.selection.functions.Functions;
import ft.transformation.Transformer;

/**
 * Implements some feature weighting schemes, described in:
 * http://nlp.stanford.edu/IR-book/html/htmledition/document-and-query-weighting-schemes-1.html
 * https://openaccess.leidenuniv.nl/bitstream/1887/13575/8/Appendix%2BB.pdf
 */
public class FeatureWeighting extends Transformer {
	private int tf;
	private int idf;
	private int normalization;
	
	public FeatureWeighting(int tf, int idf, int normalization) {
		this.tf = tf;
		this.idf = idf;
		this.normalization = normalization;
	}
	
	public InstanceList calculate(InstanceList instances) {
		return normalization(this.normalization, idf(this.idf, tf(this.tf, instances)));
	}
	
	@Override
	public String getDescription() {
		StringBuffer sb = new StringBuffer(super.getDescription());

		switch(this.tf) {
		case TF_NONE: sb.append("-TF"); break;
		case TF_MAX_NORM: sb.append("-TF-Max-Norm"); break;
		case TF_LOG: sb.append("TF-Log"); break;
		case TF_BOOLEAN: sb.append("TF-Boolean"); break;
		}

		switch(this.idf) {
		case IDF_NONE: break;
		case IDF_IDF: sb.append("-IDF"); break;
		}

		switch(this.normalization) {
		case NORMALIZATION_NONE: break;
		case NORMALIZATION_COSINE: sb.append("-Cosine-Normalization"); break;
		}
		
		return sb.toString();
	}
	
	public static final int TF_NONE = 1;
	public static final int TF_MAX_NORM = 2;
	public static final int TF_LOG = 3;
	public static final int TF_BOOLEAN = 4;
	
	public static final int IDF_NONE = 1;
	public static final int IDF_IDF = 2;

	public static final int NORMALIZATION_NONE = 1;
	public static final int NORMALIZATION_COSINE = 2;

	public static final InstanceList tf(int tf, InstanceList instances) {
		switch(tf) {
		case TF_NONE: return tf_none(instances);
		case TF_MAX_NORM: return tf_max_norm(instances);
		case TF_LOG: return tf_log(instances);
		case TF_BOOLEAN: return tf_boolean(instances);
		default: return null;
		}
	}
	
	public static final InstanceList idf(int idf, InstanceList instances) {
		switch(idf) {
		case IDF_NONE: return idf_none(instances);
		case IDF_IDF: return idf_t(instances);
		default: return null;
		}
	}
	
	public static final InstanceList normalization(int normalization, InstanceList instances) {
		switch(normalization) {
		case NORMALIZATION_NONE: return normalization_none(instances);
		case NORMALIZATION_COSINE: return normalization_cosine(instances);
		default: return null;
		}
	}

	// TFs (always return a new instance list)
	
	public static final InstanceList tf_none(InstanceList instances) {
		InstanceList newInstances = new InstanceList(instances.getDataAlphabet(), instances.getTargetAlphabet());
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			newInstances.add(new Instance(
				new FeatureVector(instance.getDataAlphabet(), fv.getIndices(), fv.getValues()), // copy values, since we're using old arrays
				instance.getTarget(), 
				instance.getName(), 
				instance.getSource()
			));
		}
		
		return newInstances;
	}
	
	public static final InstanceList tf_max_norm(InstanceList instances) {
		InstanceList newInstances = new InstanceList(instances.getDataAlphabet(), instances.getTargetAlphabet());
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			int[] indices = fv.getIndices();
			double[] values = new double[indices.length];
			
			// find largest value
			double max = Double.MIN_VALUE;
			for (double d : fv.getValues()) if(d > max) max = d;
			
			// compute max norm = tf(t,d) / max_t[ tf(t,d) ]
			for(int i= 0; i < indices.length; i++) values[i] = fv.value(indices[i]) / max;
			
			newInstances.add(new Instance(
				new FeatureVector(instance.getDataAlphabet(), indices, values), // kinda inneficient since their copying arrays
				instance.getTarget(), 
				instance.getName(), 
				instance.getSource()
			));
		}
		
		return newInstances;
	}
	
	public static final InstanceList tf_log(InstanceList instances) {
		InstanceList newInstances = new InstanceList(instances.getDataAlphabet(), instances.getTargetAlphabet());
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			int[] indices = fv.getIndices();
			double[] values = new double[indices.length];
			
			// compute log = 1 + log[tf(t,d)]
			for(int i= 0; i < indices.length; i++) values[i] = 1.0 + Math.log10(fv.value(indices[i]));
			
			newInstances.add(new Instance(
				new FeatureVector(instance.getDataAlphabet(), indices, values), 
				instance.getTarget(), 
				instance.getName(), 
				instance.getSource()
			));
		}
		
		return newInstances;
	}
	
	public static InstanceList tf_boolean(InstanceList instances) {
		InstanceList newInstances = new InstanceList(instances.getDataAlphabet(), instances.getTargetAlphabet());
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			double[] oldValues = fv.getValues();
			double[] newValues = new double[oldValues.length];
			
			for (int i=0; i < oldValues.length; ++i) newValues[i] = oldValues[i] > 0 ? 1 : 0;
			
			newInstances.add(new Instance(
				new FeatureVector(instance.getDataAlphabet(), fv.getIndices(), newValues), 
				instance.getTarget(), 
				instance.getName(), 
				instance.getSource()
			));
		}
		
		return newInstances;
	}

	// IDFs (operates on passed instance list)

	public static InstanceList idf_none(InstanceList instances) {
		// should not be used (it can, but shouldn't, since I want to use idf)
		return instances;
	}

	public static InstanceList idf_t(InstanceList instances) {
		// cache the idfs
		TreeMap<Integer, Double> idfs = new TreeMap<Integer, Double>();
		
		// number of documents
		double n = instances.size();
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			int[] indices = fv.getIndices();
			
			for(int idx : indices) {
				Double idf; // check the cache for the value
				
				if((idf=idfs.get(idx))==null) {
					idf = Math.log10(n / (1.0 + Functions.df(idx, instances)));
					idfs.put(idx, idf);
				}

				fv.setValue(idx, fv.value(idx) * idf);
			}
		}

		return instances;
	}

	// NORMALIZATIONs (operates on passed instance list)
	
	public static InstanceList normalization_none(InstanceList instances) {
		return instances;
	}	
	
	public static InstanceList normalization_cosine(InstanceList instances) {
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			int[] indices = fv.getIndices();
			
			double sum_t = 0.0; // sum(tfidf^2)
			for (double d : fv.getValues()) sum_t += Math.pow(d, 2);
			
			// compute normalization = sqrt[ tf(t,d) x idf(t,d) / sum_t [tf(t,d) x idf(t,d)]^2 ]
			for(int idx : indices) fv.setValue(idx, Math.sqrt(fv.value(idx) / sum_t));
		}
		
		return instances;
	}
}
