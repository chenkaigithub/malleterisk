package exec.main.topics;

import java.io.File;
import java.io.FileNotFoundException;

import cc.mallet.types.InstanceList;
import classifiers.TopicModelTrainer;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class TopicModelClassifier {
	public static void main(String[] args) throws FileNotFoundException {
//		InstanceList instances = InstanceList.load(new File("instances+1+1+topics"));
//		InstanceList[] split = instances.split(new double[] {0.8, 0.2});
//		
//		TopicModelTrainer trainer = new TopicModelTrainer(33);
//		Classifier classifier = trainer.train(split[0]);
//		
//		Trial trial = new Trial(classifier, split[1]);
//		System.out.println(trial.getAccuracy());
		
//		for (Instance instance : split[1]) {
//			Classification c = classifier.classify(instance);
//			Labeling l = c.getLabeling();
//			
//			System.out.println(instance.getName());
//			System.out.println("real: " + ((Label)instance.getTarget()).getBestLabel());
//			for (int i = 0; i < l.numLocations(); i++)
//				System.out.println(l.getLabelAtRank(i) + ": " + l.getValueAtRank(i));
//		}
		
		InstanceList instances = InstanceList.load(new File("instances+1+1+topics"));
		ExecutionResult r = new ExecutionResult("instances+1+1+topics", null, null, "TopicModelTrainer");
		r.trials.put(0, ExecutionUtils.crossValidate(instances, 10, new TopicModelTrainer(33)));
		r.outputTrials();
		r.outputAccuracies();
	}
}
