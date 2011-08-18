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
		int collectionId = 1;
		int userId = 1;
		for (int i = 1; i <= 7; i++) {
			userId = i;
			System.out.println("preprocessing collection " + collectionId + ", user " + userId);
			preprocess(collectionId, userId);
		}
		
		collectionId = 2;
		userId = 1;
		System.out.println("preprocessing collection " + collectionId + ", user " + userId);
		preprocess(collectionId, userId);
		
	}
	
	private static void preprocess(int collectionId, int userId) throws SQLException {
		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql"));
		DbDataSetLoader enron = new DbDataSetLoader(dal, collectionId, userId);
		
		InstanceList il = new PeoplefierPreProcessor(enron);
		il.save(new File("instances+" + collectionId + "+" + userId + "+peoplefier"));
	}
}
