package experiments;

import java.io.File;
import java.util.Random;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Labeling;
import classifiers.LibLinearTrainer;

public class LibLinearExperiment {
	public static void main(String[] args) {
		InstanceList instances = InstanceList.load(new File("instances+1+1+bodies"));
		InstanceList[] folds = instances.split(new Random(2), new double[] {0.5, 0.5});
		
		Classifier classifier = new LibLinearTrainer().train(folds[0]);
		for (Instance instance : folds[1]) {
			Classification c = classifier.classify(instance);
			Labeling l = c.getLabeling();
			
			System.out.println("REAL: " + instance.getTarget() + "\tBEST: " + l.getBestLabel() + "\n" + l.toString());
		}
	}
}
