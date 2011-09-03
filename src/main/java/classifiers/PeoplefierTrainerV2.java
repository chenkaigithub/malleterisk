package classifiers;

import types.graph.Edge;
import types.graph.EdgeType;
import types.graph.Graph;
import types.graph.Vertex;
import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;

public class PeoplefierTrainerV2  {
	public static InstanceList peoplefyInstances(InstanceList instances) {
		PeoplefierTrainer pt = new PeoplefierTrainer();
		pt.train(instances);
		
		return toInstanceList(pt.getGraph(), (LabelAlphabet)instances.getTargetAlphabet());
	}
	
	public static InstanceList toInstanceList(Graph graph, LabelAlphabet targetAlphabet) {
		Alphabet dataAlphabet = new Alphabet();
		InstanceList il = new InstanceList(dataAlphabet, targetAlphabet);
		for (Vertex v : graph.getVertices()) {
			for (Object label : v.getLabels()) {
				AugmentableFeatureVector afv = new AugmentableFeatureVector(dataAlphabet);
				for (Edge e : v.getEdges(label)) {
					int idx = dataAlphabet.lookupIndex(new PeoplefierFeature(e.getDestinationVertex().getId(), e.getType()));
					afv.add(idx, e.getWeight());
				}
				
				// random string for name for a consistent file output
				il.add(new Instance(afv, label, "/Work/msc/code/malleterisk/"+v.getId()+"+"+label, null));
			}
		}
		
		return il;
	}
}

class PeoplefierFeature {
	public final String participantId;
	public final EdgeType type;
	
	public PeoplefierFeature(String participantId, EdgeType type) {
		this.participantId = participantId;
		this.type = type;
	}
	
	public int hashCode() {
		return participantId.hashCode() * type.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof PeoplefierFeature) {
			PeoplefierFeature other = (PeoplefierFeature) o;
			
			return this.participantId.equals(other.participantId) &&
					this.type == other.type;
		}
		
		return false;
	}
	
	public String toString() {
		return participantId + ", " + type.toString();
	}
}