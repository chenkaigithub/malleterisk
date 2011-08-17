package classifiers;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import types.email.IEmailMessage;
import types.email.IEmailParticipant;
import types.graph.Edge;
import types.graph.EdgeType;
import types.graph.Graph;
import types.graph.Vertex;
import utils.CollectionsUtils;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.types.Instance;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

public class Peoplefier extends Classifier {
	private static final long serialVersionUID = 1L;
	
	private final Graph graph;
	private LabelAlphabet targetAlphabet;

	public Peoplefier(Graph g, LabelAlphabet ta) {
		this.graph = g;
		this.targetAlphabet = ta;
	}
	
	@Override
	public Classification classify(Instance instance) {
		List<String> verticesIds = new LinkedList<String>();
		for (IEmailParticipant p : ((IEmailMessage)instance.getData()).getParticipants())
			verticesIds.add(String.valueOf(p.getParticipantId()));
		
		Map<Label, Double> labelsScores = scores(this.graph, verticesIds);
		int n = labelsScores.size() > 0 ? labelsScores.size() : targetAlphabet.size();
		Label[] labels = new Label[n];
		double[] counts = new double[n];
		
		int i = 0;
		if(labelsScores.size() > 0) {
			List<Entry<Label, Double>> ls = CollectionsUtils.asSortedList(labelsScores.entrySet(), new Comparator<Entry<Label, Double>>() {
				@Override
				public int compare(Entry<Label, Double> o1, Entry<Label, Double> o2) {
					Double d1 = o1.getValue();
					Double d2 = o2.getValue();
					
					return d1 == d2 ? 0 : (d1 > d2 ? -1 : 1); // for descending order
				}
			});
			
			for (Entry<Label, Double> e : ls) {
				labels[i] = e.getKey();
				counts[i] = e.getValue();
				
				i++;
			}
		}
		else {
			Iterator<?> it = targetAlphabet.iterator();
			while(it.hasNext()) {
				labels[i] = targetAlphabet.lookupLabel(it.next());
				i++;
			}
		}
		
		return new Classification(instance, this, new LabelVector(labels, counts));
	}
	
	//
	// Scoring Methods
	//
	
	public Map<Label, Double> scores(Graph g, Collection<String> verticesIds) {
		Map<Label, Double> scores = new HashMap<Label, Double>();
		
		for (String vertexId : verticesIds)
			this.vertexScores(g, vertexId, scores);
		
		return scores;
	}
	
	public void vertexScores(Graph g, String vertexId, Map<Label, Double> scores) {
		Vertex v = g.vertex(vertexId);
		Set<Object> labels = g.getLabels(v);
		if(labels!=null) {
			for (Object label : labels) {
				Label l = this.targetAlphabet.lookupLabel(label);
				double s = scores.containsKey(l) ? scores.get(l) : 0;
				s += this.partialScore(v, label);
				
				scores.put(l, s);
			}
		}
	}
	
	public double partialScore(Vertex v, Object l) {
		double s = 0;
		
		Set<Edge> labelEdges = v.getEdges(l);
		
		for (Edge e : labelEdges)
			s += getEdgeTypeValue(e.getType()) * e.getWeight();
		
		return s;
	}
	
	public static final double getEdgeTypeValue(EdgeType t) {
		switch(t) {
		case CC:
			return 0.5;
		case BCC:
			return 0.25;
		default:
			return 1;
		}
	}
}
