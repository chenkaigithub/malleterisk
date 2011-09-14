package exec.main.preprocessing;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import types.mallet.pipe.EmailBody2Input;
import types.mallet.pipe.EmailSubject2Input;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.types.InstanceList;
import data.loader.DbDataSetLoader;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;

public class ExecTopicsPreprocessing {
	public static void main(String[] args) throws SQLException {
		topicsBodyPreprocessing(1, 1);
		topicsBodyPreprocessing(2, 2);
		topicsBodyPreprocessing(2, 3);
		topicsBodyPreprocessing(2, 4);
		topicsBodyPreprocessing(2, 5);
		topicsBodyPreprocessing(2, 6);
		topicsBodyPreprocessing(2, 7);
		topicsBodyPreprocessing(2, 8);
		
		topicsSubjectPreprocessing(1, 1);
		topicsSubjectPreprocessing(2, 2);
		topicsSubjectPreprocessing(2, 3);
		topicsSubjectPreprocessing(2, 4);
		topicsSubjectPreprocessing(2, 5);
		topicsSubjectPreprocessing(2, 6);
		topicsSubjectPreprocessing(2, 7);
		topicsSubjectPreprocessing(2, 8);
	}
	
	public static void topicsBodyPreprocessing(int collectionId, int userId) throws SQLException {
		DbDataSetLoader loader = new DbDataSetLoader(new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql")), collectionId, userId);
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		pipes.add(new EmailBody2Input());
		pipes.add(new Input2CharSequence("UTF-8"));
		pipes.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));             
//		pipes.add(new TokenSequenceLowercase());
//		pipes.add(new TokenSequenceRemoveStopwords(false, false));
//		pipes.add(new PorterStemmerSequence());
		pipes.add(new TokenSequence2FeatureSequence());
		pipes.add(new Target2Label());
        InstanceList instances = new InstanceList (new SerialPipes(pipes));
		instances.addThruPipe(loader);
		instances.save(new File("instances+" + collectionId + "+" + userId + "+topics-body"));
	}

	public static void topicsSubjectPreprocessing(int collectionId, int userId) throws SQLException {
		DbDataSetLoader loader = new DbDataSetLoader(new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql")), collectionId, userId);
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		pipes.add(new EmailSubject2Input());
		pipes.add(new Input2CharSequence("UTF-8"));
		pipes.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));             
//		pipes.add(new TokenSequenceLowercase());
//		pipes.add(new TokenSequenceRemoveStopwords(false, false));
//		pipes.add(new PorterStemmerSequence());
		pipes.add(new TokenSequence2FeatureSequence());
		pipes.add(new Target2Label());
        InstanceList instances = new InstanceList (new SerialPipes(pipes));
		instances.addThruPipe(loader);
		instances.save(new File("instances+" + collectionId + "+" + userId + "+topics-subject"));
	}
}
