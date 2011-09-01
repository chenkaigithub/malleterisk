package exec.main.preprocessing;

import java.io.File;
import java.sql.SQLException;

import pp.email.participants.ParticipantsPreProcessor;
import cc.mallet.types.InstanceList;
import data.loader.DbDataSetLoader;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;

public class ExecParticipantPreprocessing {
	public static void main(String[] args) throws SQLException {
		participantsPreprocessing(1, 1);
		participantsPreprocessing(2, 2);
		participantsPreprocessing(2, 3);
		participantsPreprocessing(2, 4);
		participantsPreprocessing(2, 5);
		participantsPreprocessing(2, 6);
		participantsPreprocessing(2, 7);
		participantsPreprocessing(2, 8);
	}
	
	private static void participantsPreprocessing(int collectionId, int userId) throws SQLException {
		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql"));
		DbDataSetLoader enron = new DbDataSetLoader(dal, collectionId, userId);
		
		InstanceList il = new ParticipantsPreProcessor(enron);
		il.save(new File("instances+" + collectionId + "+" + userId + "+participants"));
	}
}
