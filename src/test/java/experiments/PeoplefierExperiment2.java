package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Random;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import classifiers.PeoplefierTrainer;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class PeoplefierExperiment2 {
	public static void main(String[] args) throws SQLException, FileNotFoundException, InstantiationException, IllegalAccessException {
		int collectionId = 1;
		int userId = 6;
		String filename = "instances+" + collectionId + "+" + userId + "+participants";
		
//		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
//		EnronDbDataSet enron = new EnronDbDataSet(dal, collectionId, userId);
//		
//		InstanceList il = new ParticipantsPreProcessor1(enron);
//		il.save(new File(filename));
//		
//		System.out.println("-------------------------------------------------");
		
		InstanceList instances = InstanceList.load(new File(filename));
		ClassifierTrainer<? extends Classifier> ct = new PeoplefierTrainer();
		
//		run("participants", instances, ct, 10);
		
		InstanceList[] folds = instances.split(new Random(), new double[] { 0.5, 0.5 });
		Trial t = new Trial(ct.train(folds[0]), folds[1]);
		System.out.println(t.getAccuracy());

		Classifier classifier = ct.train(folds[0]);
		for (Instance instance : folds[1]) {
			Classification c = classifier.classify(instance);
			System.out.println("REAL: " + instance.getTarget() + "\tBEST: " + c.toString());
		}
	}
	
	public static final void run(
		String name, 
		InstanceList instances, 
		ClassifierTrainer<? extends Classifier> trainer,
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ExecutionResult r = new ExecutionResult(name, "", "", ExecutionUtils.getClassifierDescription(trainer));
		r.trials.put(0, ExecutionUtils.crossValidate(instances, folds, trainer.getClass().newInstance()));

		r.outputTrials();
		r.outputAccuracies();
	}
}

