package exec.main.preprocessing;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import types.mallet.pipe.EmailBody2Input;
import types.mallet.pipe.PorterStemmerSequence;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.types.InstanceList;
import data.loader.DbDataSetLoader;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;

public class ExecTopicsPreprocessing {
	public static void main(String[] args) throws SQLException {
		topicsPreprocessing(1, 1);
		topicsPreprocessing(2, 2);
		topicsPreprocessing(2, 3);
		topicsPreprocessing(2, 4);
		topicsPreprocessing(2, 5);
		topicsPreprocessing(2, 6);
		topicsPreprocessing(2, 7);
		topicsPreprocessing(2, 8);
	}
	
	public static void topicsPreprocessing(int collectionId, int userId) throws SQLException {
		DbDataSetLoader loader = new DbDataSetLoader(new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql")), collectionId, userId);
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		pipes.add(new EmailBody2Input());
		pipes.add(new Input2CharSequence("UTF-8"));
		pipes.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));             
		pipes.add(new TokenSequenceLowercase());
		pipes.add(new TokenSequenceRemoveStopwords(false, false));
		pipes.add(new PorterStemmerSequence());
		pipes.add(new TokenSequence2FeatureSequence());
		pipes.add(new Target2Label());
        InstanceList instances = new InstanceList (new SerialPipes(pipes));
		instances.addThruPipe(loader);
		instances.save(new File("instances+" + collectionId + "+" + userId + "+topics"));
	}
}
