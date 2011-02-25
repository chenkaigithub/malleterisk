package types.mallet.pipe;

import java.io.Serializable;

import types.IEmailMessage;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;

public abstract class EmailField2Input extends Pipe implements Serializable {
	private static final long serialVersionUID = 1L;

	public Instance pipe(Instance carrier) {
		Instance instance = (Instance)carrier.clone();
		
		IEmailMessage msg = (IEmailMessage) carrier.getData();
		instance.setData(getEmailField(msg));
		
		return instance;
	}

	protected abstract Object getEmailField(IEmailMessage msg);
}
