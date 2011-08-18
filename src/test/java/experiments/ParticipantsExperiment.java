package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.classify.evaluate.ConfusionMatrix;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class ParticipantsExperiment {
	public static void main(String[] args) throws SQLException, FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+participants"));
		files.add(new File("instances+1+2+participants"));
		files.add(new File("instances+1+3+participants"));
		files.add(new File("instances+1+4+participants"));
		files.add(new File("instances+1+5+participants"));
		files.add(new File("instances+1+6+participants"));
		files.add(new File("instances+1+7+participants"));
		files.add(new File("instances+2+1+participants"));
		
		for (File file : files) {
			InstanceList instances = InstanceList.load(file);
			ClassifierTrainer<? extends Classifier> ct = new NaiveBayesTrainer();

			InstanceList[] folds = instances.split(new Random(), new double[] { 0.5, 0.5 });

			Classifier classifier = ct.train(folds[0]);
			for (Instance instance : folds[1]) {
				Classification c = classifier.classify(instance);
				System.out.println("REAL: " + instance.getTarget() + "\tBEST: " + c.getLabeling().toString());
			}

			Trial t = new Trial(ct.train(folds[0]), folds[1]);
			System.out.println(t.getAccuracy());
			
			ConfusionMatrix cm = new ConfusionMatrix(t);
			System.out.println(cm.toString());
		}
	}
}
