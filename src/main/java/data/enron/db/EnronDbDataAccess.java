package data.enron.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import types.EmailParticipant;
import types.IEmailParticipant;
import types.ParticipantType;
import utils.JavaMailUtils;

// TODO: general jdbc cleanup & refactor; alot of duplicated code
public class EnronDbDataAccess {
	public final EnronDbConnector db;
	
	public EnronDbDataAccess(EnronDbConnector db) {
		this.db = db;
	}

	// insert stuff
	
	public int storeCollection(String collectionName) throws SQLException {
		int id = getCollection(collectionName);
		if(id==-1) id = insertCollection(collectionName);
		
		return id;
	}
	
	private int getCollection(String collectionName) throws SQLException {
		int id = -1;
		
		Connection c = db.getDbConnection();
		
		final String sql = "SELECT collection_id FROM email_collection WHERE collection_name = ?";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setString(1, collectionName);
		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) id = rs.getInt(1);
		
		c.close();
		
		return id;
	}
	
	private int insertCollection(String collectionName) throws SQLException {
		int id = -1;
		
		Connection c = db.getDbConnection();
		
		final String sql = "INSERT INTO email_collection(collection_name) VALUES(?)";
		PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, collectionName);
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if(rs.next()) id = rs.getInt(1);
		
		c.close();
		
		return id;
	}
	
	
	public int storeUser(int collectionId, String userName) throws SQLException {
		int id = getUser(collectionId, userName);
		if(id==-1) id = insertUser(collectionId, userName);
		
		return id;
	}
	
	private int getUser(int collectionId, String userName) throws SQLException {
		int id = -1;
		
		Connection c = db.getDbConnection();
		
		final String sql = "SELECT user_id FROM email_user WHERE collection_id = ? AND user_name = ?";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, collectionId);
		ps.setString(2, userName);
		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) id = rs.getInt(1);
		
		c.close();
		
		return id;
	}
	
	private int insertUser(int collectionId, String userName) throws SQLException {
		int id = -1;
		
		Connection c = db.getDbConnection();
		
		final String sql = "INSERT INTO email_user(collection_id, user_name) VALUES(?, ?)";
		PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, collectionId);
		ps.setString(2, userName);
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if(rs.next()) id = rs.getInt(2);
		
		c.close();
		
		return id;
	}

	
	public int storeClass(int collectionId, int userId, String className) throws SQLException {
		int id = getClass(collectionId, userId, className);
		if(id==-1) id = insertClass(collectionId, userId, className);
		
		return id;
	}
	
	private int getClass(int collectionId, int userId, String className) throws SQLException {
		int id = -1;
		
		Connection c = db.getDbConnection();
		
		final String sql = "SELECT class_id FROM email_class WHERE collection_id = ? AND user_id = ? AND class_name = ?";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, collectionId);
		ps.setInt(2, userId);
		ps.setString(3, className);
		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) id = rs.getInt(1);
		
		c.close();
		
		return id;		
	}
	
	private int insertClass(int collectionId, int userId, String className) throws SQLException {
		int id = -1;
		
		Connection c = db.getDbConnection();
		final String sql = "INSERT INTO email_class(collection_id, user_id, class_name) VALUES(?, ?, ?)";
		PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, collectionId);
		ps.setInt(2, userId);
		ps.setString(3, className);
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if(rs.next()) id = rs.getInt(3);
		
		c.close();
		
		return id;
	}

	
	public int storeEmail(String emailName, int collectionId, int userId, int classId) throws MessagingException, IOException, SQLException {
		MimeMessage m = new MimeMessage(null, new FileInputStream(emailName));
		
		int emailId = insertEmail(
			emailName, 
			collectionId, 
			userId, 
			classId, 
			JavaMailUtils.parseDateTime(m.getSentDate()), 
			m.getSubject(), 
			m.getContent().toString()
		);
		
		for (String from : JavaMailUtils.parseAddresses(JavaMailUtils.FROM, m)) storeEmailParticipant(emailId, from, JavaMailUtils.FROM);
		for (String to : JavaMailUtils.parseAddresses(JavaMailUtils.TO, m)) storeEmailParticipant(emailId, to, JavaMailUtils.TO);
		for (String cc : JavaMailUtils.parseAddresses(JavaMailUtils.CC, m)) storeEmailParticipant(emailId, cc, JavaMailUtils.CC);
		for (String bcc : JavaMailUtils.parseAddresses(JavaMailUtils.BCC, m)) storeEmailParticipant(emailId, bcc, JavaMailUtils.BCC);
		
		return emailId;
	}
	
	private int insertEmail(String emailName, int collectionId, int userId, int classId, String date, String subject, String body) throws SQLException {
		int id = -1;
		Connection c = db.getDbConnection();
		
		final String sql = "INSERT INTO email_message(email_name, collection_id, user_id, class_id, date, subject, body) VALUES(?,?,?,?,?,?,?)";
		PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, emailName);
		ps.setInt(2, collectionId);
		ps.setInt(3, userId);
		ps.setInt(4, classId);
		ps.setString(5, date);
		ps.setString(6, subject);
		ps.setString(7, body);
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if(rs.next()) id = rs.getInt(1);
		
		c.close();

		return id;
	}
	
	private void storeEmailParticipant(int emailId, String participant, String participantType) throws SQLException {
		int participantId = storeEmailParticipant(participant);
		
		Connection c = db.getDbConnection();
		
		final String sql = "INSERT INTO email_participants(email_id, participant_id, participant_type) VALUES(?,?,?)";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, emailId);
		ps.setInt(2, participantId);
		ps.setString(3, participantType);
		
		ps.execute();
		
		c.close();
	}
	
	private int storeEmailParticipant(String participantAddress) throws SQLException {
		int participantId = getEmailParticipant(participantAddress);
		if(participantId==-1) participantId = insertEmailParticipant(participantAddress);
		
		return participantId;
	}
	
	private int getEmailParticipant(String participantAddress) throws SQLException {
		int id = -1;
		
		Connection c = db.getDbConnection();
		
		final String sql = "SELECT participant_id FROM email_participant WHERE participant_address = ?";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setString(1, participantAddress);
		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) id = rs.getInt(1);
		
		c.close();
		
		return id;
	}
	
	private int insertEmailParticipant(String participantAddress) throws SQLException {
		int id = -1;
		
		Connection c = db.getDbConnection();
		
		final String sql = "INSERT INTO email_participant(participant_address) VALUES(?)";
		PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, participantAddress);
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		if(rs.next()) id = rs.getInt(1);
		
		c.close();
		
		return id;
	}
	
	
	// dal stuff
	
	public Collection<Integer> getCollections() throws SQLException {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		
		Connection c = db.getDbConnection();
		
		final String sql = "SELECT collection_id FROM email_collection";
		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) ids.add(rs.getInt("collection_id"));
		
		c.close();
		
		return ids;
	}
	
	public Collection<Integer> getUsers(int collectionId) throws SQLException {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		
		Connection c = db.getDbConnection();
		
		final String sql = "SELECT user_id FROM email_user WHERE collection_id = ?";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, collectionId);
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) ids.add(rs.getInt("user_id"));
		
		c.close();
		
		return ids;
	}
	
	public Collection<Integer> getClasses(int collectionId, int userId) throws SQLException {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		
		Connection c = db.getDbConnection();
		
		final String sql = "SELECT class_id FROM email_class WHERE collection_id = ? AND user_id = ?";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, collectionId);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) ids.add(rs.getInt("class_id"));
		
		c.close();
		
		return ids;
	}
	
	public ResultSet getEmailMessages(int collectionId, int userId) throws SQLException {
		Connection c = db.getDbConnection();
		c.setAutoCommit(false);
		
		final String sql = 
			"SELECT email_id, email_name, collection_id, user_id, class_id, date, subject, body " +
			"FROM email_message " +
			"WHERE collection_id = ? AND user_id = ?" +
			"ORDER BY email_id";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, collectionId);
		ps.setInt(2, userId);
		ps.setFetchSize(10);	// force database cursor usage
		return ps.executeQuery();
		
		// TODO: best way to close the PreparedStatement and the Connection?
	}
	
	public Map<ParticipantType, Collection<IEmailParticipant>> getEmailParticipants(int emailId) throws SQLException {
		HashMap<ParticipantType, Collection<IEmailParticipant>> participants = new HashMap<ParticipantType, Collection<IEmailParticipant>>();
		LinkedList<IEmailParticipant> from = new LinkedList<IEmailParticipant>();
		LinkedList<IEmailParticipant> to = new LinkedList<IEmailParticipant>();
		LinkedList<IEmailParticipant> cc = new LinkedList<IEmailParticipant>();
		LinkedList<IEmailParticipant> bcc = new LinkedList<IEmailParticipant>();
		participants.put(ParticipantType.FROM, from);
		participants.put(ParticipantType.TO, to);
		participants.put(ParticipantType.CC, cc);
		participants.put(ParticipantType.BCC, bcc);
		
		Connection c = db.getDbConnection();
		
		final String sql = "SELECT participant_id, participant_type FROM email_participants WHERE email_id = ?";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, emailId);
		
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			String p = rs.getString("participant_type");
			if(p.equals("From")) from.add(new EmailParticipant(emailId, rs.getInt("participant_id"), ParticipantType.FROM));
			else if(p.equals("To")) to.add(new EmailParticipant(emailId, rs.getInt("participant_id"), ParticipantType.TO));
			else if(p.equals("Cc")) cc.add(new EmailParticipant(emailId, rs.getInt("participant_id"), ParticipantType.CC));
			else if(p.equals("Bcc")) bcc.add(new EmailParticipant(emailId, rs.getInt("participant_id"), ParticipantType.BCC));
		}
		
		c.close();
		
		return participants;
	}
}
