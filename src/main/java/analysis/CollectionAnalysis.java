package analysis;

import java.util.Map;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class CollectionAnalysis {
	private InstanceList instances;
	
	public CollectionAnalysis(InstanceList instances) {
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
	
	public Map<String, Integer> getTermOccurrences() {
		// term | #occurrs
		return null;
	}
	
	public double getAverageTermsDocuments() {
		double i = 0;
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			i += fv.getIndices().length;
		}
		
		return i/instances.size();
	}
	
	public double getAverageDocumentsClass() {
		return instances.size()/instances.getTargetAlphabet().size();
	}
	
	/*
	-- nœmero de mensagens por data
	select email_id, class_id, date
	from email_message
	order by date;
	*/
	
	/*
	-- diferentes classes atribu’das aos participantes pelos utilizadores 
	select distinct m.user_id, p.participant_id, m.class_id
	from email_message m, email_participants p
	where m.email_id = p.email_id
	order by participant_id, user_id;
	*/	
}
