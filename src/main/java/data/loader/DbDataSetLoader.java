package data.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;

import types.email.EmailMessage;
import utils.JavaMailUtils;
import cc.mallet.types.Instance;
import data.loader.db.DbDataAccess;

/**
 * Generic loader of datasets. Constructor receives the information regarding 
 * the specific dataset to retrieve.
 * 
 * @author tt
 *
 */
public class DbDataSetLoader implements IDataSetLoader, Iterator<Instance> {
	private final DbDataAccess dal;
	private final ResultSet mrs;
	
	public DbDataSetLoader(DbDataAccess dal, int collectionId, int userId) throws SQLException {
		this.dal = dal;
		this.mrs = dal.getEmailMessages(collectionId, userId);
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.mrs.close();
	}

	//
	// Iterator
	//

	@Override
	public boolean hasNext() {
		boolean hasNext = false;
		
		try { hasNext = this.mrs.next(); } 
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
			int emailId = this.mrs.getInt(DbDataAccess.DB_EMAIL_ID);
			
			name = this.mrs.getString(DbDataAccess.DB_EMAIL_NAME);
			target = this.mrs.getInt(DbDataAccess.DB_CLASS_ID);
			data = new EmailMessage(
				emailId,
				this.mrs.getInt(DbDataAccess.DB_COLLECTION_ID),
				this.mrs.getInt(DbDataAccess.DB_USER_ID),
				this.mrs.getInt(DbDataAccess.DB_CLASS_ID), // same as target
				JavaMailUtils.parseDateTime(this.mrs.getString(DbDataAccess.DB_EMAIL_DATE)),
				this.mrs.getString(DbDataAccess.DB_EMAIL_SUBJECT),
				this.mrs.getString(DbDataAccess.DB_EMAIL_BODY),
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