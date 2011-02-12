package types;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

public class EmailMessage implements IEmailMessage {
	private final int emailId;
	private final int collectionId;
	private final int userId;
	private final int classId;
	private final Date date;
	private final String subject;
	private final String body;
	private final Map<ParticipantType, Collection<IEmailParticipant>> participants;
	
	public EmailMessage(int emailId, int collectionId, int userId, 
		int classId, Date date, String subject, String body, 
		Map<ParticipantType, Collection<IEmailParticipant>> participants
	) {
		this.emailId = emailId;
		this.collectionId = collectionId;
		this.userId = userId;
		this.classId = classId;
		this.date = date;
		this.subject = subject;
		this.body = body;
		this.participants = participants;
	}

	@Override
	public int getEmailId() {
		return emailId;
	}

	@Override
	public int getCollectionId() {
		return collectionId;
	}

	@Override
	public int getUserId() {
		return userId;
	}

	@Override
	public int getClassId() {
		return classId;
	}
	
	@Override
	public Collection<IEmailParticipant> getFrom() {
		return participants.get(ParticipantType.FROM);
	}

	@Override
	public Collection<IEmailParticipant> getTo() {
		return participants.get(ParticipantType.TO);
	}

	@Override
	public Collection<IEmailParticipant> getCc() {
		return participants.get(ParticipantType.CC);
	}

	@Override
	public Collection<IEmailParticipant> getBcc() {
		return participants.get(ParticipantType.BCC);
	}

	@Override
	public Collection<IEmailParticipant> getParticipants() {
		LinkedList<IEmailParticipant> ps = new LinkedList<IEmailParticipant>();
		for (Collection<IEmailParticipant> p : this.participants.values()) ps.addAll(p);
		
		return ps;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public String getBody() {
		return body;
	}
}
