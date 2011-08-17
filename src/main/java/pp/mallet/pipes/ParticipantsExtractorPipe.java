package pp.mallet.pipes;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import types.email.IEmailMessage;
import types.email.IEmailParticipant;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class ParticipantsExtractorPipe extends Pipe implements Serializable {
	private static final long serialVersionUID = 1L;

	public Instance pipe(Instance carrier) {
		IEmailMessage msg = (IEmailMessage) carrier.getData();
		carrier.setData(new TokenSequence(toTokens(msg.getParticipants())));

		return carrier;
	}
	
	public static TokenSequence toTokens(Collection<IEmailParticipant> participants) {
		LinkedList<Token> tokens = new LinkedList<Token>();
		for (IEmailParticipant p : participants) tokens.add(new Token(p.toString()));
				
		return new TokenSequence(tokens);
	}
}
