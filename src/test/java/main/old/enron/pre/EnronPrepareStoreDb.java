package main.old.enron.pre;
import java.sql.SQLException;

import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;
import data.source.enron.EnronConverter;
import data.source.enron.EnronConstants;

public class EnronPrepareStoreDb {
	public static void main(String[] args) throws SQLException {
		DbConnector dbc = new DbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql");
		DbDataAccess dal = new DbDataAccess(dbc);
		
//		new Enron2Db(dal).processEnron(EnronUtils.ENRON_FLAT_PATH);
		new EnronConverter(dal).processEnron(EnronConstants.ENRON_TOPICAL_FOLDERS_PATH);
	}
}
