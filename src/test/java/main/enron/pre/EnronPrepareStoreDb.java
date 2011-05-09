package main.enron.pre;
import java.sql.SQLException;

import data.db.DbConnector;
import data.db.DbDataAccess;
import data.enron.Enron2Db;
import data.enron.utils.EnronConstants;

public class EnronPrepareStoreDb {
	public static void main(String[] args) throws SQLException {
		DbConnector dbc = new DbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql");
		DbDataAccess dal = new DbDataAccess(dbc);
		
//		new Enron2Db(dal).processEnron(EnronUtils.ENRON_FLAT_PATH);
		new Enron2Db(dal).processEnron(EnronConstants.ENRON_TOPICAL_FOLDERS_PATH);
	}
}
