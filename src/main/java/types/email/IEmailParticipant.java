package types.email;

import java.io.Serializable;

public interface IEmailParticipant extends Serializable {
	int getEmailId();
	int getParticipantId();
	ParticipantType getParticipantType();
}
