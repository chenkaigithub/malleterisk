package pp.pipeline;

import java.util.ArrayList;

import pp.IPreProcessor;

import types.mallet.EmailDate2Input;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.types.InstanceList;

public class DatePreProcessor implements IPreProcessor {
	private final InstanceList instanceList;
	
	public DatePreProcessor() {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		pipes.add(new EmailDate2Input());
		// ... ?
				
		instanceList = new InstanceList(new SerialPipes(pipes));
	}
	
	@Override
	public InstanceList getInstanceList() {
		return instanceList;
	}
}


