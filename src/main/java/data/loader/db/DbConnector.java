package data.loader.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {
	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private final String url;
	private final String user;
	private final String password;	
	
	public DbConnector(String url, String user, String pwd) {
		this.url = url;
		this.user = user;
		this.password = pwd;
	}
	
	public final Connection getDbConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
}
