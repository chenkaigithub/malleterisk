import java.sql.SQLException;

import data.enron.db.Enron2Db;
import data.enron.db.EnronDbConnector;
import data.enron.db.EnronDbDataAccess;
import data.enron.utils.EnronUtils;

public class EnronPrepareStoreDb {
	public static void main(String[] args) throws SQLException {
		EnronDbConnector dbc = new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql");
		EnronDbDataAccess dal = new EnronDbDataAccess(dbc);
		
//		new Enron2Db(dal).processEnron(EnronUtils.ENRON_FLAT_PATH);
		new Enron2Db(dal).processEnron(EnronUtils.ENRON_TOPICAL_FOLDERS_PATH);
	}
}
