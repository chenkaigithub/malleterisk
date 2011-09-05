package classifiers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicAssignment;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

public class TopicModelTrainerV2 extends ClassifierTrainer<Classifier> {
	private Classifier classifier;
	private final int numTopics;
	
	public TopicModelTrainerV2(int n) {
		this.numTopics = n;
	}
	
	@Override
	public Classifier getClassifier() {
		if(classifier == null)
			throw new RuntimeException("train has not been executed.");
		
		return classifier;
	}

	@Override
	public Classifier train(InstanceList trainingSet) {
		// topic model estimation
		ParallelTopicModel model = new ParallelTopicModel(numTopics);
		model.addInstances(trainingSet);
		try { model.estimate(); }
		catch (IOException e) { e.printStackTrace(); }
		
		// aggregate probabilities
		Map<Label, float[]> ctp = computeClassTopicsProbabilities(model);
		
		return (classifier = new TopicModelClassifierV2(model, ctp));
	}
	
	private Map<Label, float[]> computeClassTopicsProbabilities(ParallelTopicModel model) {
		Map<Label, float[]> classTopicsProbabilities = new HashMap<Label, float[]>();
		
		int[] topicCounts = new int[numTopics];
		
		// iterate instances
		for (TopicAssignment ta : model.getData()) {
			// calculate topics probabilities for instance
			
			// retrieve topics that occur in this instance
			int[] instanceTopics = ta.topicSequence.getFeatures();
			
			// (count the occurrences of the topics)
			for (int token=0; token < instanceTopics.length; token++)
				topicCounts[ instanceTopics[token] ]++;
			
			float[] topicProbs = new float[numTopics];
			
			// normalize with the occurrences of the topics (turning this into a dirichlet)
			int n = 0;
			for (int i : topicCounts) n += i;
			for (int topic = 0; topic < numTopics; topic++)
				topicProbs[topic] = (float) topicCounts[topic] / n; 
			
			// add to labels' topics probabilities
			float[] classProbs = classTopicsProbabilities.get(ta.instance.getTarget());
			if(classProbs == null) {
				classProbs = topicProbs;
				classTopicsProbabilities.put((Label)ta.instance.getTarget(), classProbs);
			}
			else {
				for (int i = 0; i < numTopics; i++) {
					classProbs[i] = (classProbs[i] + topicProbs[i]) / 2;
				}
			}
		}
		
		return classTopicsProbabilities;
	}
}
