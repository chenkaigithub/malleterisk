package analysis;

import java.util.Map;

import cc.mallet.types.InstanceList;

public class ContentAnalysis {
	public ContentAnalysis(InstanceList instances) {
		
	}
	
	public int getNumTerms() {
		// instances.getAlphabet.length
		return 0;
	}
	
	public int getNumDocuments() {
		// instances.length
		return 0;
	}
	
	public int getNumClasses() {
		
		return 0;
	}
	
	public Map<String, Integer> getTermOccurrences() {
		// term | #occurrs
		return null;
	}
}
