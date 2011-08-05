package types.mallet.pipe;

import types.email.IEmailMessage;

public class EmailDate2Input extends EmailField2Input {
	private static final long serialVersionUID = 1L;

	@Override
	protected Object getEmailField(IEmailMessage msg) {
		return msg.getDate();
	}
}
