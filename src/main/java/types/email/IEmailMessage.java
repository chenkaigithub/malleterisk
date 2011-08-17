package types.email;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public interface IEmailMessage extends Serializable {
	int getCollectionId();
	int getUserId();
	int getEmailId();
	int getClassId();
	
	Collection<IEmailParticipant> getFrom();
	Collection<IEmailParticipant> getTo();
	Collection<IEmailParticipant> getCc();
	Collection<IEmailParticipant> getBcc();
	Collection<IEmailParticipant> getParticipants();
	
	Date getDate();
	
	String getSubject();
	String getBody();
	
}
