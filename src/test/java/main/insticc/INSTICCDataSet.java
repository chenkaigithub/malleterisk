package main.insticc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import cc.mallet.types.Instance;
import data.loader.IDataSetLoader;
import data.loader.db.DbDataAccess;

@Deprecated
public class INSTICCDataSet implements IDataSetLoader, Iterator<Instance> {
	private final ResultSet messagesRS;
	
	public INSTICCDataSet(DbDataAccess dal) throws SQLException {
		messagesRS = dal.getEmailMessages(2, 1);
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
			name = messagesRS.getString(DbDataAccess.DB_EMAIL_NAME);
			target = messagesRS.getInt(DbDataAccess.DB_CLASS_ID);
			data = messagesRS.getString(DbDataAccess.DB_EMAIL_BODY);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(data==null) data = "";
		
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