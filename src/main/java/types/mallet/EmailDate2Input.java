package types.mallet;

import types.IEmailMessage;

public class EmailDate2Input extends EmailField2Input {
	private static final long serialVersionUID = 1L;

	@Override
	protected Object getEmailField(IEmailMessage msg) {
		return msg.getDate();
	}
}
