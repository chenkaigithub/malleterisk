package classifiers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicAssignment;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

import com.google.common.primitives.Ints;

public class TopicModelTrainer extends ClassifierTrainer<Classifier> {
	private Classifier classifier;
	private final int numTopics;
	
	public TopicModelTrainer(int n) {
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
		ParallelTopicModel lda = new ParallelTopicModel(numTopics);
		lda.addInstances(trainingSet);
		try { lda.estimate(); }
		catch (IOException e) { e.printStackTrace(); }
		Map<Integer, Set<Label>> ct = associateTopicClasses(lda);
		
		return (classifier = new TopicModelClassifier(lda, ct));
	}
	
	public Map<Integer, Set<Label>> associateTopicClasses(ParallelTopicModel model) {
		Map<Integer, Set<Label>> topicClassesAssociation = new HashMap<Integer, Set<Label>>();
		
		int[] topicCounts = new int[numTopics];
		
		// iterate instances
		for (TopicAssignment ta : model.getData()) {
			// retrieve topics that occur in this instance
			int[] instanceTopics = ta.topicSequence.getFeatures();
			
			// determine the topic with highest frequency in this instance
			
			// (count the occurrences of the topics)
			for (int token=0; token < instanceTopics.length; token++)
				topicCounts[ instanceTopics[token] ]++;

			// (find topic with most occurrences) 
			int topicIndex = Ints.indexOf(topicCounts, Ints.max(topicCounts));
			Arrays.fill(topicCounts, 0); // clean for reuse
			
			// associate topic & class
			Set<Label> classes = topicClassesAssociation.get(topicIndex);
			
			if(classes==null) {
				classes = new HashSet<Label>();
				topicClassesAssociation.put(topicIndex, classes);
			}
			
			classes.add((Label)ta.instance.getTarget());
		}
		
		return topicClassesAssociation;
	}
}
