select * from email_collection;
select * from email_user;
select * from email_class;
select * from email_message;
select * from email_participants;
select * from email_participant;

select collection_id, count(email_id) from email_message group by collection_id;
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
	collection_id INTEGER,
	user_id SERIAL,
	user_name TEXT UNIQUE
);

CREATE TABLE email_class (
	collection_id INTEGER,
	user_id INTEGER,
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


