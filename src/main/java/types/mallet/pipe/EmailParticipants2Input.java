package types.mallet.pipe;

import java.util.Collection;
import java.util.LinkedList;

import types.email.IEmailMessage;
import types.email.IEmailParticipant;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class EmailParticipants2Input extends EmailField2Input {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected Object getEmailField(IEmailMessage msg) {
		return new TokenSequence(toTokens(msg.getParticipants()));
	}
	
	public static TokenSequence toTokens(Collection<IEmailParticipant> participants) {
		LinkedList<Token> tokens = new LinkedList<Token>();
		for (IEmailParticipant p : participants) tokens.add(new Token(p.toString()));
				
		return new TokenSequence(tokens);
	}
}
