package experiments;

import java.io.File;
import java.sql.SQLException;
import java.util.Random;

import main.old.enron.EnronDbDataSet;
import pp.email.participants.ParticipantsPreProcessor1;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.InstanceList;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;

public class PeoplefierExperiment {
	public static void main(String[] args) throws SQLException {
		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
		EnronDbDataSet enron = new EnronDbDataSet(dal, 1, 6);
		
		InstanceList il = new ParticipantsPreProcessor1(enron);
		il.save(new File("participants.il"));
		
		InstanceList il2 = InstanceList.load(new File("participants.il"));
		NaiveBayesTrainer nbtr = new NaiveBayesTrainer();
		InstanceList[] folds = il2.split(new Random(), new double[] { 0.5, 0.5 });
		Classifier nb = nbtr.train(folds[0]);
		
		Trial t = new Trial(nb, folds[1]);
		System.out.println(t.getAccuracy());
	}
	
}

