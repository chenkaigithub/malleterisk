package exec.main.topics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cc.mallet.types.InstanceList;
import classifiers.TopicModelTrainer;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class ExecTopicModelClassifier {
	public static void main(String[] args) throws FileNotFoundException {
//		//
//		// Single Run Test
//		//
//		
//		InstanceList instances = InstanceList.load(new File("instances+1+1+topics"));
//		InstanceList[] split = instances.split(new double[] {0.8, 0.2});
//		
//		TopicModelTrainer trainer = new TopicModelTrainer(33);
//		Classifier classifier = trainer.train(split[0]);
//		
//		Trial trial = new Trial(classifier, split[1]);
//		System.out.println(trial.getAccuracy());
//		
//		for (Instance instance : split[1]) {
//			Classification c = classifier.classify(instance);
//			Collection<Label> topLabels = ExecutionUtils.getTopLabels(c, 3);
//			
//			System.out.println(instance.getName());
//			System.out.println("real: " + ((Label)instance.getTarget()).getBestLabel());
//			for (Label label : topLabels)
//				System.out.print(label + ", ");
//		}
		
		//
		// Cross Validation Test
		//
		
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+topics"));
		files.add(new File("instances+2+2+topics"));
		files.add(new File("instances+2+3+topics"));
		files.add(new File("instances+2+4+topics"));
		files.add(new File("instances+2+5+topics"));
		files.add(new File("instances+2+6+topics"));
		files.add(new File("instances+2+7+topics"));
		files.add(new File("instances+2+8+topics"));
		
		for (File file : files) {
			InstanceList instances = InstanceList.load(file);
			ExecutionResult r = new ExecutionResult(file.getName(), null, null, "TopicModelClassifier");
			r.trials.put(0, ExecutionUtils.crossValidate(instances, 10, new TopicModelTrainer(instances.getTargetAlphabet().size())));
			r.outputTrials();
			r.outputAccuracies();
		}
	}
}
