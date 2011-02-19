package pp;

import java.util.ArrayList;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.types.InstanceList;
import data.IDataSet;

/*
 * Defines a sequence of pre-processing operations that a message goes through.
 */
public abstract class PreProcessor extends InstanceList {
	private static final long serialVersionUID = 8606199204474189155L;

	@SuppressWarnings("deprecation")
	public PreProcessor(IDataSet ds) {
		this.setPipe(new SerialPipes(getPipes()));
		this.addThruPipe(ds);
	}

	protected abstract ArrayList<Pipe> getPipes();
}
