using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using Microsoft.Office.Interop.Outlook;
using iwantedue;
using Npgsql;
using System.Data;

class Program
{
    public static void Main(string[] args)
    {
        String path = @"D:\work\data\insticc\categories";
        int collectionId = storeCollection(path);
        int userId = storeUser(collectionId, "INSTICC");

        processMessages(collectionId, userId, path);
    }

    private static void processMessages(int collectionId, int userId, string path)
    {
        string[] dirs = Directory.GetDirectories(path);
        foreach (string dir in dirs)
            processMessages(collectionId, userId, dir);

        string[] files = Directory.GetFiles(path);
        int classId = -1;

        if (files.Length > 0)
            classId = storeClass(collectionId, userId, path);

        foreach (string file in files)
        {
            int emailId = storeEmail(collectionId, userId, classId, file);

            Console.WriteLine(collectionId + "|" + userId + "|" + classId + "|" + emailId + "|" + file);
        }
    }

    // --------------------------------------------------------------------------------------------------------------

    private static NpgsqlConnection getConnection()
    {
        return new NpgsqlConnection("Server=127.0.0.1;Port=5432;User Id=postgres;Password=postgresql;Database=malleterisk;");
    }

    private static int getSerial(string sql, Dictionary<string, object> pms, NpgsqlConnection c)
    {
        NpgsqlCommand command = new NpgsqlCommand(sql, c);
        foreach (string k in pms.Keys)
            command.Parameters.Add(new NpgsqlParameter(k, pms[k]));

        return (int)command.ExecuteScalar();
    }

    private static Dictionary<string, int> collections = new Dictionary<string, int>();
    private static List<Dictionary<string, int>> users = new List<Dictionary<string, int>>();
    private static List<List<Dictionary<string, int>>> classes = new List<List<Dictionary<string, int>>>();
    private static Dictionary<string, int> participants = new Dictionary<string, int>();

    // Collection

    public static int storeCollection(string name)
    {
        int id;
        if (!collections.TryGetValue(name, out id))
        {
            id = insertCollection(name);
            collections.Add(name, id);
            users.Add(new Dictionary<string, int>());
            classes.Add(new List<Dictionary<string, int>>());
        }

        return id;
    }

    private static int insertCollection(string name)
    {
        NpgsqlConnection c = getConnection();
        c.Open();

        NpgsqlCommand command = new NpgsqlCommand("INSERT INTO email_collection(collection_name) VALUES(:value1)", c);
        command.Parameters.Add(new NpgsqlParameter(":value1", name));
        int id;

        try
        {
            command.ExecuteNonQuery();
            Dictionary<string, object> pms = new Dictionary<string, object>();
            pms.Add(":value1", name);
            id = getSerial("SELECT collection_id FROM email_collection WHERE collection_name = :value1", pms, c);
        }
        finally
        {
            c.Close();
        }

        return id;
    }

    // User

    public static int storeUser(int collectionId, string userName)
    {
        int id;
        Dictionary<string, int> us = users[collectionId-1];
        if (!us.TryGetValue(userName, out id))
        {
            id = insertUser(collectionId, userName);
            us.Add(userName, id);
            classes[collectionId-1].Add(new Dictionary<string, int>());
        }
        return id;
    }

    private static int insertUser(int collectionId, string userName)
    {
        NpgsqlConnection c = getConnection();
        c.Open();

        NpgsqlCommand command = new NpgsqlCommand("INSERT INTO email_user(collection_id, user_name) VALUES(:value1, :value2)", c);
        command.Parameters.Add(new NpgsqlParameter(":value1", collectionId));
        command.Parameters.Add(new NpgsqlParameter(":value2", userName));
        int id;

        try
        {
            command.ExecuteNonQuery();
            Dictionary<string, object> pms = new Dictionary<string, object>();
            pms.Add(":value1", collectionId);
            pms.Add(":value2", userName);

            id = getSerial("SELECT user_id FROM email_user WHERE collection_id = :value1 AND user_name = :value2", pms, c);
        }
        finally
        {
            c.Close();
        }

        return id;
    }

    // Class

    public static int storeClass(int collectionId, int userId, string className)
    {
        int id;
        Dictionary<string, int> cs = classes[collectionId-1][userId-1];
        if (!cs.TryGetValue(className, out id))
        {
            id = insertClass(collectionId, userId, className);
            cs.Add(className, id);
        }
        return id;
    }

    private static int insertClass(int collectionId, int userId, string className)
    {
        NpgsqlConnection c = getConnection();
        c.Open();

        NpgsqlCommand command = new NpgsqlCommand("INSERT INTO email_class(collection_id, user_id, class_name) VALUES(:value1, :value2, :value3)", c);
        command.Parameters.Add(new NpgsqlParameter(":value1", collectionId));
        command.Parameters.Add(new NpgsqlParameter(":value2", userId));
        command.Parameters.Add(new NpgsqlParameter(":value3", className));
        int id;

        try
        {
            command.ExecuteNonQuery();
            Dictionary<string, object> pms = new Dictionary<string, object>();
            pms.Add(":value1", collectionId);
            pms.Add(":value2", userId);
            pms.Add(":value3", className);

            id = getSerial("SELECT class_id FROM email_class WHERE collection_id = :value1 AND user_id = :value2 AND class_name = :value3", pms, c);
        }
        finally
        {
            c.Close();
        }

        return id;
    }

    // Email

    public static int storeEmail(int collectionId, int userId, int classId, string emailName)
    {
        Stream messageStream = File.Open(emailName, FileMode.Open, FileAccess.Read);
        OutlookStorage.Message message = new OutlookStorage.Message(messageStream);
        messageStream.Close();

        int id = insertEmail(
            emailName, 
            collectionId, 
            userId, 
            classId, 
            message.ReceivedOrSentTime.ToString(), 
            message.Subject, 
            message.BodyText
        );

        storeEmailParticipant(id, message.FromAddress, "From");
        foreach (string p in parseAddresses(message.Recipients, OutlookStorage.RecipientType.To)) storeEmailParticipant(id, p, "To");
        foreach (string p in parseAddresses(message.Recipients, OutlookStorage.RecipientType.CC)) storeEmailParticipant(id, p, "Cc");
        
        return id;
    }

    private static List<string> parseAddresses(List<OutlookStorage.Recipient> recipients, OutlookStorage.RecipientType type)
    {
        List<string> addresses = new List<string>();

        foreach(OutlookStorage.Recipient r in recipients)
            if (r.Type == type) addresses.Add(r.Address);

        return addresses;
    }

    private static int insertEmail(String emailName, int collectionId, int userId, int classId, String date, String subject, String body)
    {
        NpgsqlConnection c = getConnection();
        c.Open();

        NpgsqlCommand command = new NpgsqlCommand("INSERT INTO email_message(email_name, collection_id, user_id, class_id, date, subject, body) VALUES(:value1, :value2, :value3, :value4, :value5, :value6, :value7)", c);
        command.Parameters.Add(new NpgsqlParameter(":value1", emailName));
        command.Parameters.Add(new NpgsqlParameter(":value2", collectionId));
        command.Parameters.Add(new NpgsqlParameter(":value3", userId));
        command.Parameters.Add(new NpgsqlParameter(":value4", classId));
        command.Parameters.Add(new NpgsqlParameter(":value5", date));
        command.Parameters.Add(new NpgsqlParameter(":value6", subject));
        command.Parameters.Add(new NpgsqlParameter(":value7", body));
        int id;

        try
        {
            command.ExecuteNonQuery();
            Dictionary<string, object> pms = new Dictionary<string, object>();
            pms.Add(":value1", collectionId);
            pms.Add(":value2", userId);
            pms.Add(":value3", classId);
            pms.Add(":value4", emailName);

            id = getSerial("SELECT email_id FROM email_message WHERE collection_id = :value1 AND user_id = :value2 AND class_id = :value3 AND email_name = :value4", pms, c);
        }
        finally
        {
            c.Close();
        }

        return id;
    }

    private static void storeEmailParticipant(int emailId, string participant, string participantType)
    {
        int participantId = storeParticipant(participant);
        
        NpgsqlConnection c = getConnection();
        c.Open();

        NpgsqlCommand command = new NpgsqlCommand("INSERT INTO email_participants(email_id, participant_id, participant_type) VALUES(:value1, :value2, :value3)", c);
        command.Parameters.Add(new NpgsqlParameter(":value1", emailId));
        command.Parameters.Add(new NpgsqlParameter(":value2", participantId));
        command.Parameters.Add(new NpgsqlParameter(":value3", participantType));

        try { command.ExecuteNonQuery(); }
        finally { c.Close(); }
    }

    // Participants

    public static int storeParticipant(string address)
    {
        int id;
        if (!participants.TryGetValue(address, out id))
        {
            id = insertParticipant(address);
            participants.Add(address, id);
        }

        return id;
    }

    private static int insertParticipant(string address)
    {
        NpgsqlConnection c = getConnection();
        c.Open();

        NpgsqlCommand command = new NpgsqlCommand("INSERT INTO email_participant(participant_address) VALUES(:value1)", c);
        command.Parameters.Add(new NpgsqlParameter(":value1", address));
        int id;

        try
        {
            command.ExecuteNonQuery();
            Dictionary<string, object> pms = new Dictionary<string, object>();
            pms.Add(":value1", address);
            id = getSerial("SELECT participant_id FROM email_participant WHERE participant_address = :value1", pms, c);
        }
        finally
        {
            c.Close();
        }

        return id;
    }
}
