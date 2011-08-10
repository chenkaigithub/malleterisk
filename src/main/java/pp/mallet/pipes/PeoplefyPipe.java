package pp.mallet.pipes;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import types.email.IEmailMessage;
import types.email.IEmailParticipant;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

import com.google.common.collect.Sets;

public class PeoplefyPipe extends Pipe implements Serializable {
	private static final long serialVersionUID = 1L;

	public Instance pipe(Instance carrier) {
		IEmailMessage msg = (IEmailMessage) carrier.getData();
		carrier.setData(peoplefy(msg));
		
		return carrier;
	}
	
	public TokenSequence peoplefy(IEmailMessage msg) {
		Collection<IEmailParticipant> participants = msg.getParticipants();
		
		Set<Set<IEmailParticipant>> powerset = powerset(participants);
		powerset = removeEmptyOrSingleSets(powerset);
		
		return new TokenSequence(toTokens(powerset));
	}
	
	public static Set<Set<IEmailParticipant>> powerset(Collection<IEmailParticipant> participants) {
		return Sets.powerSet(new HashSet<IEmailParticipant>(participants));
	}
	
	public static Set<Set<IEmailParticipant>> removeEmptyOrSingleSets(Set<Set<IEmailParticipant>> powerset) {
		Set<Set<IEmailParticipant>> newSet = new HashSet<Set<IEmailParticipant>>();
		for (Set<IEmailParticipant> s : powerset) if(s.size() >= 2) newSet.add(s);
		
		return newSet;
	}
	
	public static Collection<Token> toTokens(Set<Set<IEmailParticipant>> participants) {
		LinkedList<Token> tokens = new LinkedList<Token>();
		
		for (Set<IEmailParticipant> ps : participants)
			tokens.add(new Token(generateUniqueParticipantsId(ps)));
				
		return tokens;
	}
	
	public static String generateUniqueParticipantsId(Set<IEmailParticipant> participants) {
		TreeSet<String> ts = new TreeSet<String>();
		
		for (IEmailParticipant p : participants) {
			ts.add(p.toString());
		}
		
		StringBuilder sb = new StringBuilder();
		for (String s : ts) {
			sb.append(s);
			sb.append("-");
		}
		
		return sb.toString();
	}
}
