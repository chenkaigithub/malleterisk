package analysis;

import types.mallet.LabeledInstancesList;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.SparseVector;

public class CollectionAnalysis {
	private final InstanceList instances;
	private final LabeledInstancesList labeledInstances;
	private final Alphabet dataAlphabet;
	private final Alphabet targetAlphabet;
	
	public CollectionAnalysis(InstanceList instances) {
		this.instances = instances;
		this.dataAlphabet = instances.getDataAlphabet();
		this.targetAlphabet = instances.getTargetAlphabet();
		this.labeledInstances = new LabeledInstancesList(instances);
	}
	
	// terms, classes, documents
	
	public int getNumTerms() {
		return dataAlphabet.size();
	}
	
	public int getNumClasses() {
		return targetAlphabet.size();
	}
	
	public int getNumDocuments() {
		return instances.size();
	}
	
	public SparseVector getTermOccurrences() {
		SparseVector sv = new SparseVector(new double[dataAlphabet.size()]);
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			sv.plusEqualsSparse(fv);
		}
		
		return sv;
	}	
	
	// terms / document
	
	public double getAverageTermsPerDocument() {
		double i = 0;
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			i += fv.getIndices().length;
		}
		
		return i/instances.size();
	}
	
	public int getMinNumTermsInDocuments() {
		int m = -1;
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			int i = fv.getIndices().length;
			if(m == -1 || m > i) m = i;
		}
		
		return m;
	}
	
	public int getMaxNumTermsInDocument() {
		int m = 0;
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector) instance.getData();
			int i = fv.getIndices().length;
			if(i > m) m = i;
		}
		
		return m;
	}
	
	// documents / classes
	
	public double getAverageDocumentsPerClass() {
		return (double)instances.size()/(double)instances.getTargetAlphabet().size();
	}
	
	public int getMinNumDocumentsInClass() {
		int c = -1;

		for (InstanceList instances : labeledInstances.getLabeledInstances()) {
			int i = instances.size();
			if(c==-1 || c > i) c = i;
		}

		return c;
	}
	
	public int getMaxNumDocumentsInClass() {
		int c = 0;

		for (InstanceList instances : labeledInstances.getLabeledInstances()) {
			int i = instances.size();
			if(i > c) c = i;
		}

		return c;
	}
	
	// print
	
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

		sb.append("max number of documents in class: ");
		sb.append(getMaxNumDocumentsInClass());
		sb.append("\n");
		
		sb.append("min number of documents in class: ");
		sb.append(getMinNumDocumentsInClass());
		sb.append("\n");
		
		sb.append("max number of terms in documents: ");
		sb.append(getMaxNumTermsInDocument());
		sb.append("\n");
		
		sb.append("min number of terms in documents: ");
		sb.append(getMinNumTermsInDocuments());
		sb.append("\n");
				
		return sb.toString();
	}
}
