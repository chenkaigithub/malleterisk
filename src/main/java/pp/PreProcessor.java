package pp;

import java.util.ArrayList;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.types.InstanceList;
import data.loader.IDataSetLoader;

/*
 * Defines a sequence of pre-processing operations that a message goes through.
 */
public abstract class PreProcessor extends InstanceList {
	private static final long serialVersionUID = 8606199204474189155L;

	@SuppressWarnings("deprecation")
	public PreProcessor() {
		this.setPipe(new SerialPipes(getPipes()));
	}

	public PreProcessor(IDataSetLoader ds) {
		this();
		this.addThruPipe(ds);
	}
	
	protected abstract ArrayList<Pipe> getPipes();
	
	public String getDescription() {
		StringBuffer desc = new StringBuffer();
		
		Pipe pipe = this.getPipe();
		if(pipe!= null && pipe instanceof SerialPipes) {
			ArrayList<Pipe> pipes = ((SerialPipes) pipe).pipes();
			for (Pipe p : pipes) {
				desc.append(p.getClass().getSimpleName().replace("[", "").replace("]", ""));
				desc.append("+");
			}
		}
		
		return desc.toString();
	}
}
