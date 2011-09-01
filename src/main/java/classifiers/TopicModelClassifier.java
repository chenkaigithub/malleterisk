package classifiers;

import java.util.Map;
import java.util.Set;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

public class TopicModelClassifier extends Classifier {
	private static final long serialVersionUID = 1L;
	private final ParallelTopicModel model;
	private final Map<Integer, Set<Label>> classTopics;
	
	public TopicModelClassifier(ParallelTopicModel m, Map<Integer, Set<Label>> cts) {
		this.model = m;
		this.classTopics = cts;
	}

	@Override
	public Classification classify(Instance instance) {
		// classification is done in the following way:
		// 1. infer topic probabilities for given instance
		// 2. iterate topics:
		// a. retrieve previously associated classes
		// b. iterate classes, and add to it's value the topic probability
		
        TopicInferencer inferencer = model.getInferencer(); 
		double[] topicProbs = inferencer.getSampledDistribution(instance, 100, 10, 10);
		
		LabelAlphabet ta = (LabelAlphabet)instance.getTargetAlphabet();
		double[] values = new double[ta.size()];
		
		for (int topicIdx = 0; topicIdx < topicProbs.length; topicIdx++) {
			Set<Label> classes = classTopics.get(topicIdx);
			
			if(classes!=null)
				for (Label label : classes)
					values[label.getIndex()] += topicProbs[topicIdx];
			// what happens if topic was not seen in training?
		}
		
		return new Classification(instance, this, new LabelVector(ta, values));
	}
}
