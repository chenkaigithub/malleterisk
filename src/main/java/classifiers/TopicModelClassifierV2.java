package classifiers;

import java.util.Map;
import java.util.Map.Entry;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

public class TopicModelClassifierV2 extends Classifier {
	private static final long serialVersionUID = 1L;
	
	private final ParallelTopicModel model;
	private final Map<Label, float[]> classTopicsProbabilities;
	
	public TopicModelClassifierV2(ParallelTopicModel m, Map<Label, float[]> ctp) {
		this.model = m;
		this.classTopicsProbabilities = ctp;
	}

	@Override
	public Classification classify(Instance instance) {
		TopicInferencer inferencer = model.getInferencer(); 
		double[] instanceTopicsProbabilities = inferencer.getSampledDistribution(instance, 100, 10, 10);
		
		LabelAlphabet ta = (LabelAlphabet)instance.getTargetAlphabet();
		double[] values = new double[ta.size()];
		
		for (Entry<Label, float[]> e : classTopicsProbabilities.entrySet())
			values[e.getKey().getIndex()] = cosineSimilarity(instanceTopicsProbabilities, e.getValue());
			//dotProduct(instanceTopicsProbabilities, e.getValue());
		
		return new Classification(instance, this, new LabelVector(ta, values));
	}
	
	private static double cosineSimilarity(double[] instanceTopicsProbabilities, float[] classTopicsProbabilities) {
		return (double) dotProduct(instanceTopicsProbabilities, classTopicsProbabilities) / 
			(magnitude(instanceTopicsProbabilities) * magnitude(classTopicsProbabilities));
	}
	
	private static double dotProduct(double[] instanceTopicsProbabilities, float[] classTopicsProbabilities) {
		double d = 0;
		
		for (int i = 0; i < classTopicsProbabilities.length; i++)
			d += instanceTopicsProbabilities[i] * classTopicsProbabilities[i];
		
		return d;
	}
	
	private static double magnitude(double[] vals) {
		double d = 0;
		
		for (double v : vals)
			d += v*v;
		
		return Math.sqrt(d);
	}
	
	private static double magnitude(float[] vals) {
		double d = 0;
		
		for (float v : vals)
			d += v*v;
		
		return Math.sqrt(d);
	}
}
