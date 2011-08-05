package pp.email.participants;

import java.util.ArrayList;

import pp.PreProcessor;
import types.mallet.pipe.EmailParticipants2Input;
import cc.mallet.pipe.Pipe;
import data.loader.IDataSetLoader;

public class ParticipantsPreProcessor1 extends PreProcessor {
	private static final long serialVersionUID = -3365120647901882307L;

	public ParticipantsPreProcessor1() {
	}

	public ParticipantsPreProcessor1(IDataSetLoader ds) {
	}
	
	@Override
	protected ArrayList<Pipe> getPipes() {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		pipes.add(new EmailParticipants2Input());
		// ... ?
		
		return pipes;
	}
}
