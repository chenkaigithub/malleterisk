package main.enron;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;

import types.EmailMessage;
import utils.JavaMailUtils;
import cc.mallet.types.Instance;
import data.loader.IDataSetLoader;
import data.loader.db.DbDataAccess;

@Deprecated
//TODO: organize this
public class EnronDbDataSet implements IDataSetLoader, Iterator<Instance> {
	private final DbDataAccess dal;
	private final ResultSet messagesRS;
	
	public EnronDbDataSet(DbDataAccess dal, int collectionId, int userId) throws SQLException {
		this.dal = dal;
		messagesRS = dal.getEmailMessages(collectionId, userId);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		messagesRS.close();
	}
	
	//
	// Iterator
	//
	
	@Override
	public boolean hasNext() {
		boolean hasNext = false;
		
		try { hasNext = messagesRS.next(); } 
		catch(SQLException e) { e.printStackTrace(); } 
		
		return hasNext;
	}

	@Override
	public Instance next() {
		Object data = null;
		Object target = null;
		Object name = null;
		Object source = null;
		
		try {
			int emailId = messagesRS.getInt(DbDataAccess.DB_EMAIL_ID);
			
			name = messagesRS.getString(DbDataAccess.DB_EMAIL_NAME);
			target = messagesRS.getInt(DbDataAccess.DB_CLASS_ID);
			data = new EmailMessage(
				emailId,
				messagesRS.getInt(DbDataAccess.DB_COLLECTION_ID),
				messagesRS.getInt(DbDataAccess.DB_USER_ID),
				messagesRS.getInt(DbDataAccess.DB_CLASS_ID), // same as target
				JavaMailUtils.parseDateTime(messagesRS.getString(DbDataAccess.DB_EMAIL_DATE)),
				messagesRS.getString(DbDataAccess.DB_EMAIL_SUBJECT),
				messagesRS.getString(DbDataAccess.DB_EMAIL_BODY),
				dal.getEmailParticipants(emailId)
			);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return new Instance(data, target, name, source);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	//
	// Iterable
	//

	@Override
	public Iterator<Instance> iterator() {
		return this;
	}
}
