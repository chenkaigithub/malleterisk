package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import main.old.enron.EnronDbDataSet;
import pp.email.participants.ParticipantsPreProcessor1;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class PeoplefierExperiment {
	public static void main(String[] args) throws SQLException, FileNotFoundException, InstantiationException, IllegalAccessException {
		int collectionId = 1;
		int userId = 6;
		String filename = "instances+" + collectionId + "+" + userId + "+participants.il";
		
		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
		EnronDbDataSet enron = new EnronDbDataSet(dal, collectionId, userId);
		
		InstanceList il = new ParticipantsPreProcessor1(enron);
		il.save(new File(filename));
		
		System.out.println("-------------------------------------------------");
		
		InstanceList instances = InstanceList.load(new File(filename));
		ClassifierTrainer<? extends Classifier> ct = new NaiveBayesTrainer();
		
		run("participants", instances, ct, 10);
		
//		InstanceList[] folds = instances.split(new Random(), new double[] { 0.5, 0.5 });
//		Trial t = new Trial(ct.train(folds[0]), folds[1]);
//		System.out.println(t.getAccuracy());
	}
	
	public static final void run(
		String name, 
		InstanceList instances, 
		ClassifierTrainer<? extends Classifier> trainer,
		int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ExecutionResult r = new ExecutionResult(name, "", "", ExecutionUtils.getClassifierDescription(trainer));
		r.trials.put(instances.getDataAlphabet().size(), ExecutionUtils.crossValidate(instances, folds, trainer.getClass().newInstance()));

		r.outputTrials();
		r.outputAccuracies();
	}
}

