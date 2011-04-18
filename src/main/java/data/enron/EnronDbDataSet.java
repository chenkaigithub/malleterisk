package data.enron;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;

import types.EmailMessage;
import utils.JavaMailUtils;
import cc.mallet.types.Instance;
import data.IDataSet;
import data.enron.db.EnronDbDataAccess;
import data.enron.utils.EnronConstants;

public class EnronDbDataSet implements IDataSet, Iterator<Instance> {
	private final EnronDbDataAccess dal;
	private final ResultSet messagesRS;
	
	public EnronDbDataSet(EnronDbDataAccess dal, int collectionId, int userId) throws SQLException {
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
			int emailId = messagesRS.getInt(EnronConstants.ENRON_DB_EMAIL_ID);
			
			name = messagesRS.getString(EnronConstants.ENRON_DB_EMAIL_NAME);
			target = messagesRS.getInt(EnronConstants.ENRON_DB_CLASS_ID);
			data = new EmailMessage(
				emailId,
				messagesRS.getInt(EnronConstants.ENRON_DB_COLLECTION_ID),
				messagesRS.getInt(EnronConstants.ENRON_DB_USER_ID),
				messagesRS.getInt(EnronConstants.ENRON_DB_CLASS_ID),
				JavaMailUtils.parseDateTime(messagesRS.getString(EnronConstants.ENRON_DB_EMAIL_DATE)),
				messagesRS.getString(EnronConstants.ENRON_DB_EMAIL_SUBJECT),
				messagesRS.getString(EnronConstants.ENRON_DB_EMAIL_BODY),
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
