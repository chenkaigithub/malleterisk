package pp.email.participants;

import java.util.ArrayList;

import pp.PreProcessor;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.Target2Label;
import data.loader.IDataSetLoader;

public class PeoplefierPreProcessor extends PreProcessor {
	private static final long serialVersionUID = -3365120647901882307L;

	public PeoplefierPreProcessor() {
	}

	public PeoplefierPreProcessor(IDataSetLoader ds) {
		super(ds);
	}
	
	@Override
	protected ArrayList<Pipe> getPipes() {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		pipes.add(new Target2Label());
		
		return pipes;
	}
}
