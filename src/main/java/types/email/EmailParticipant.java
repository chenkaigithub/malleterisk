package types.email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class EmailParticipant implements IEmailParticipant {
	private static final long serialVersionUID = 1;

	private int emailId;
	private int participantId;
	private ParticipantType type;
	
	public EmailParticipant(int eId, int pId, ParticipantType pt) {
		emailId = eId;
		participantId = pId;
		type = pt;
	}
	
	@Override
	public int getEmailId() {
		return emailId;
	}
	
	@Override
	public int getParticipantId() {
		return participantId;
	}
		
	@Override
	public ParticipantType getParticipantType() {
		return type;
	}
	
	// Serializable
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt(emailId);
		out.writeInt(participantId);
		out.writeObject(type);
	}

	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		emailId = in.readInt();
		participantId = in.readInt();
		type = (ParticipantType)in.readObject();
	}
	
	// toString
	
	@Override
	public String toString() {
		return String.valueOf(participantId);
	}
}
