package data.enron.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.mail.MessagingException;

public class Enron2Db {
	private final EnronDbDataAccess dal;
	
	public Enron2Db(EnronDbDataAccess dal) {
		this.dal = dal;
	}

	// enron processing
	
	public void processEnron(String path) throws SQLException {
		int collectionId = dal.insertCollection(path);
		
		for (File f : new File(path).listFiles()) {
			if(f.isDirectory() || !f.getName().equalsIgnoreCase(".DS_Store")) {
				int userId = dal.insertUser(collectionId, f.getAbsolutePath());
				processEntry(f, collectionId, userId);
			}
		}
	}
	
	private void processEntry(File e, int collectionId, int userId) throws SQLException {
		if(e.isDirectory()) for (File f : e.listFiles()) processEntry(f, collectionId, userId);
		else processFile(e, collectionId, userId);
	}

	private void processFile(File e, int collectionId, int userId) throws SQLException {
		if(!e.getName().equalsIgnoreCase(".DS_Store")) {
			String fileName = e.getAbsolutePath();
			String folderName = fileName.substring(0, fileName.lastIndexOf(e.getName()));
			
			int classId = dal.storeClass(collectionId, userId, folderName);
			int emailId = -1;
			
			try { emailId = dal.storeEmail(fileName, collectionId, userId, classId); } 
			catch (MessagingException e1) { e1.printStackTrace(); } 
			catch (IOException e1) { e1.printStackTrace(); } 
			catch (SQLException e1) { e1.printStackTrace(); }
			
			System.out.println(collectionId + "|" + userId + "|" + classId + "|" + emailId + "|" + fileName);
		}
	}

}
