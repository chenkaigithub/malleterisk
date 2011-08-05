package data.loader;

import java.util.Iterator;

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
	// TODO: implement
	
	public DbDataSetLoader(DbDataAccess dal, int collectionId, int userId) {
		
	}

	@Override
	public Iterator<Instance> iterator() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public Instance next() {
		return null;
	}

	@Override
	public void remove() {
		
	}

}
