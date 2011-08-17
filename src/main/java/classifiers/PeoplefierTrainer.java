package classifiers;

import java.util.Collection;

import types.email.IEmailMessage;
import types.email.IEmailParticipant;
import types.email.ParticipantType;
import types.graph.EdgeType;
import types.graph.Graph;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;

public class PeoplefierTrainer extends ClassifierTrainer<Classifier> {
	private Classifier classifier;
	private final Graph graph;
	
	public PeoplefierTrainer() {
		this.graph = new Graph();
	}
	
	@Override
	public Classifier getClassifier() {
		if(classifier == null)
			throw new RuntimeException("train has not been executed.");

		return classifier;
	}

	@Override
	public Classifier train(InstanceList trainingSet) {
		// training means to fill the graph
		for (Instance instance : trainingSet) {
			IEmailMessage msg = (IEmailMessage)instance.getData();
			Object label = msg.getClassId();
			
			for (IEmailParticipant f : msg.getFrom()) {
				int srcId = f.getParticipantId();
				
				addAll(srcId, msg.getTo(), label);
				addAll(srcId, msg.getCc(), label);
				addAll(srcId, msg.getBcc(), label);
			}
		}
		
		return (classifier = new Peoplefier(this.graph, (LabelAlphabet)trainingSet.getTargetAlphabet()));
	}
	
	private void addAll(int srcId, Collection<IEmailParticipant> dsts, Object label) {
		for (IEmailParticipant dst : dsts)
			add(srcId, dst.getParticipantId(), dst.getParticipantType(), label);
	}
	
	private void add(int srcId, int dstId, ParticipantType type, Object label) {
		this.graph.edge(
			this.graph.vertex(String.valueOf(srcId)), 
			this.graph.vertex(String.valueOf(dstId)), 
			1, 
			getEdgeType(type), 
			label
		);
	}

	private EdgeType getEdgeType(ParticipantType type) {
		switch(type) {
		case TO: return EdgeType.TO;
		case CC: return EdgeType.CC;
		case BCC: return EdgeType.BCC;
		}
		
		return null;
	}
}
