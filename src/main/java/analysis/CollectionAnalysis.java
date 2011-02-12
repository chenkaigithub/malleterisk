package analysis;

public class CollectionAnalysis {
	/*
	 * getNumUsers()
	 * 
	 * getNumDocuments()
	 * getNumClasses()
	 * 
	 * getNumDocumentsPerUser()
	 * getNumClassesPerUser()
	 * 
	 * getNumDocumentsPerClass()
	 * 
	 * 
	 */
	
	/*
	-- nœmero de classes distintas
	select count(class_id) from email_class;
	*/

	/*
	-- nœmero de utilizadores distintos
	select count(user_id) from email_user;
	*/

	/*
	-- nœmero de classes por utilizador
	select user_id, count(class_id)
	from (select distinct user_id, class_id from email_message) AS t
	group by user_id
	order by user_id;
	*/

	/*
	-- nœmero de mensagens distintas
	select count(email_id)
	from email_message;
	*/
	
	/*
	-- nœmero de mensagens por classe
	select class_id, count(email_id) AS num_msgs
	from email_message
	group by class_id
	order by class_id;
	*/
	
	/*
	-- nœmero de mensagens por utilizador
	select user_id, count(user_id) AS num_msgs
	from email_message
	group by user_id
	order by user_id;
	*/

	/*
	-- nœmero de mensagens por data
	select email_id, class_id, date
	from email_message
	order by date;
	*/
	
	/*
	-- diferentes classes atribu’das aos participantes pelos utilizadores 
	select distinct m.user_id, p.participant_id, m.class_id
	from email_message m, email_participants p
	where m.email_id = p.email_id
	order by participant_id, user_id;
	*/
}
