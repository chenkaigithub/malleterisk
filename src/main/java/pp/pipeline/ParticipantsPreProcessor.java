package pp.pipeline;

import java.util.ArrayList;

import pp.IPreProcessor;

import types.mallet.EmailParticipants2Input;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.types.InstanceList;

public class ParticipantsPreProcessor implements IPreProcessor {
	private final InstanceList instanceList;
	
	public ParticipantsPreProcessor() {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		pipes.add(new EmailParticipants2Input());
		// ... ?
				
		instanceList = new InstanceList(new SerialPipes(pipes));
	}
	
	@Override
	public InstanceList getInstanceList() {
		return instanceList;
	}
}
