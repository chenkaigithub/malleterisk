package analysis;

import java.util.HashMap;
import java.util.Map;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class TextCollectionAnalysis {
	private InstanceList instances;
	
	public TextCollectionAnalysis(InstanceList instances) {
		this.instances = instances;
	}
	
	public int getNumTerms() {
		return instances.getDataAlphabet().size();
	}
	
	public int getNumDocuments() {
		return instances.size();
	}
	
	public int getNumClasses() {
		return instances.getTargetAlphabet().size();
	}
	
	public Map<String, Double> getTermOccurrences() {
		Map<String, Double> tos = new HashMap<String, Double>();
		Alphabet terms = instances.getDataAlphabet();
//		double[] occurrences = new double[terms.size()];
//		occurrences[i] += fv.value(i);

		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			for (int i : fv.getIndices()) {
				String s = terms.lookupObject(i).toString();
				double d = fv.value(i);
				if(tos.get(s) == null) tos.put(s, d);
				else tos.put(s, tos.get(s)+d);
			}
		}
		
		return tos;
	}
	
	public double getAverageTermsPerDocument() {
		double i = 0;
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			i += fv.getIndices().length;
		}
		
		return i/instances.size();
	}
	
	public double getAverageDocumentsPerClass() {
		return (double)instances.size()/(double)instances.getTargetAlphabet().size();
	}
	
	public int getNumTermsPerDocument() {
		// TODO: 
		return 0;
	}
	
	public int getMinNumTermsPerDocument() {
		// TODO: 
		return 0;
	}
	
	public int getMaxNumTermsPerDocument() {
		// TODO: 
		return 0;
	}
	
	public int getNumDocumentsPerClass() {
		// TODO: 
		return 0;
	}
	
	public int getMinNumDocumentsPerClass() {
		// TODO: 
		return 0;
	}
	
	public int getMaxNumDocumentsPerClass() {
		// TODO: 
		return 0;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("number of terms: ");
		sb.append(getNumTerms());
		sb.append("\n");

		sb.append("number of documents: ");
		sb.append(getNumDocuments());
		sb.append("\n");

		sb.append("number of classes: ");
		sb.append(getNumClasses());
		sb.append("\n");
		
		sb.append("average number of terms per document: ");
		sb.append(getAverageTermsPerDocument());
		sb.append("\n");
		
		sb.append("average number of documents per class: ");
		sb.append(getAverageDocumentsPerClass());
		sb.append("\n");
		
		Map<String, Double> tos = getTermOccurrences();
		for (String s : tos.keySet()) {
			sb.append(s);
			sb.append(": ");
			sb.append(tos.get(s));
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
