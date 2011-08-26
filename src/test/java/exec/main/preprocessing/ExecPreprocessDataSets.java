package exec.main.preprocessing;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import pp.PreProcessor;
import pp.email.body.BodyPreProcessor1;
import pp.email.date.DatePreProcessor1;
import pp.email.participants.ParticipantsPreProcessor;
import pp.email.participants.PeoplefierPreProcessor;
import pp.email.subject.SubjectPreProcessor1;
import data.loader.DbDataSetLoader;
import data.loader.IDataSetLoader;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;
import execution.ExecutionUtils;

public class ExecPreprocessDataSets {
	public static void main(String[] args) throws SQLException {
		// stores email data from database to preprocessed files
		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql"));
		for (int collectionId : dal.getCollections()) {
			System.out.println("collection: " + collectionId);
			for (int userId : dal.getUsers(collectionId)) {
				System.out.println("user: " + userId);
				
				// get dataset of specified collection and user
				IDataSetLoader ds = new DbDataSetLoader(dal, collectionId, userId);
				
				// preprocess data from the dataset into instance lists (aka preprocessors)
				ArrayList<PreProcessor> preprocessors = new ArrayList<PreProcessor>();
				preprocessors.add(new SubjectPreProcessor1());
				preprocessors.add(new BodyPreProcessor1());
				preprocessors.add(new DatePreProcessor1());
				preprocessors.add(new ParticipantsPreProcessor());
				preprocessors.add(new PeoplefierPreProcessor());
				Collection<PreProcessor> instanceLists = ExecutionUtils.preprocess(ds, preprocessors);
				
				// save into files of the following format: instances+collection+user+field+preprocessing
				for (PreProcessor instanceList : instanceLists)
					instanceList.save(new File(String.format("instances+%d+%d+%s", collectionId, userId, instanceList.getDescription())));
			}
		}
	}
}
