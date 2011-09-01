package exec.main.preprocessing;

import java.io.File;
import java.sql.SQLException;

import pp.email.participants.PeoplefierPreProcessor;
import cc.mallet.types.InstanceList;
import data.loader.DbDataSetLoader;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;

public class ExecPeoplefierPreprocessing {
	public static void main(String[] args) throws SQLException {
		peoplefierPreprocessing(1, 1);
		peoplefierPreprocessing(2, 2);
		peoplefierPreprocessing(2, 3);
		peoplefierPreprocessing(2, 4);
		peoplefierPreprocessing(2, 5);
		peoplefierPreprocessing(2, 6);
		peoplefierPreprocessing(2, 7);
		peoplefierPreprocessing(2, 8);
	}
	
	private static void peoplefierPreprocessing(int collectionId, int userId) throws SQLException {
		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql"));
		DbDataSetLoader enron = new DbDataSetLoader(dal, collectionId, userId);
		
		InstanceList il = new PeoplefierPreProcessor(enron);
		il.save(new File("instances+" + collectionId + "+" + userId + "+peoplefier"));
	}
}
