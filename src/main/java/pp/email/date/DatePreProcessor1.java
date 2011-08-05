package pp.email.date;

import java.util.ArrayList;

import pp.PreProcessor;
import types.mallet.pipe.EmailDate2Input;
import cc.mallet.pipe.Pipe;
import data.loader.IDataSetLoader;

public class DatePreProcessor1 extends PreProcessor {
	private static final long serialVersionUID = -3365120647901882307L;

	public DatePreProcessor1() {
	}
	
	public DatePreProcessor1(IDataSetLoader ds) {
		super(ds);
	}
	
	@Override
	protected ArrayList<Pipe> getPipes() {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		pipes.add(new EmailDate2Input());
		// ... ?
		
		return pipes;
	}
}
