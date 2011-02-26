package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Filename2CharSequence;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class TestData {
	public static void main(String[] args) throws SQLException, FileNotFoundException {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		pipes.add(new Filename2CharSequence());
		pipes.add(new Input2CharSequence("UTF-8"));
		pipes.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));             
		pipes.add(new TokenSequence2FeatureSequence());
		pipes.add(new Target2Label());
		pipes.add(new FeatureSequence2FeatureVector());
		InstanceList instances = new InstanceList (new SerialPipes(pipes));
		
		instances.addThruPipe(new Instance("data/duke/10.", "duke", "duke/10.", null));
		instances.addThruPipe(new Instance("data/duke/18.", "duke", "duke/18.", null));
		instances.addThruPipe(new Instance("data/duke/34.", "duke", "duke/34.", null));
		instances.addThruPipe(new Instance("data/ecogas/10.", "ecogas", "ecogas/10.", null));
		instances.addThruPipe(new Instance("data/ecogas/18.", "ecogas", "ecogas/18.", null));
		
		for (Instance instance : instances) {
			System.out.println(instance.getData());
		}
		
		instances.save(new File("instances_0-0_tests"));
	}
}
