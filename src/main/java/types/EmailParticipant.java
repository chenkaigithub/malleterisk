package types;

public class EmailParticipant implements IEmailParticipant {
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
}
