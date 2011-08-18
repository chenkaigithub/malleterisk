package exec.enron;
import java.sql.SQLException;

import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;
import data.source.enron.EnronFileToDbConverter;
import data.source.enron.EnronConstants;

public class EnronStoreDbTopical {
	public static void main(String[] args) throws SQLException {
		DbConnector dbc = new DbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql");
		DbDataAccess dal = new DbDataAccess(dbc);
		
		new EnronFileToDbConverter(dal).processEnron(EnronConstants.ENRON_TOPICAL_FOLDERS_PATH);
	}
}
