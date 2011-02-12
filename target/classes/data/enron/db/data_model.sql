select * from email_collection;
select * from email_user;
select * from email_class;
select * from email_message;

---------------------------------------------------------------------------------------------------

/*
-- número de classes distintas
select count(class_id) from email_class;
*/

/*
-- número de utilizadores distintos
select count(user_id) from email_user;
*/

/*
-- número de classes por utilizador
select user_id, count(class_id)
from (select distinct user_id, class_id from email_message) AS t
group by user_id
order by user_id;
*/

/*
-- número de mensagens distintas
select count(email_id)
from email_message;
*/



/*
-- número de mensagens por classe
select class_id, count(email_id) AS num_msgs
from email_message
group by class_id
order by class_id;
*/

/*
-- número de mensagens por utilizador
select user_id, count(user_id) AS num_msgs
from email_message
group by user_id
order by user_id;
*/

/*
-- número de mensagens por data
select email_id, class_id, date
from email_message
order by date;
*/

/*
-- diferentes classes atribuídas aos participantes pelos utilizadores 
select distinct m.user_id, p.participant_id, m.class_id
from email_message m, email_participants p
where m.email_id = p.email_id
order by participant_id, user_id;
*/

---------------------------------------------------------------------------------------------------

/*
DROP TABLE email_collection;
DROP TABLE email_user;
DROP TABLE email_class;
DROP TABLE email_message;
DROP TABLE email_participant;
DROP TABLE email_participants;

CREATE TABLE email_collection (
	collection_id SERIAL,
	collection_name TEXT UNIQUE
);

CREATE TABLE email_user (
	user_id SERIAL,
	user_name TEXT UNIQUE
);

CREATE TABLE email_class (
	class_id SERIAL,
	class_name TEXT UNIQUE
);

CREATE TABLE email_message (
	email_id SERIAL,
	email_name TEXT UNIQUE,

	collection_id INTEGER,
	user_id INTEGER,
	class_id INTEGER,
	
	date TEXT,
	subject TEXT,
	body TEXT
);

CREATE TABLE email_participant (
	participant_id SERIAL,
	participant_address TEXT
);

CREATE TABLE email_participants (
	email_id INTEGER,
	participant_id INTEGER,
	participant_type TEXT
);

*/
