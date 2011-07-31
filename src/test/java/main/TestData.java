package main;

import java.io.File;
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
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

/**
 * Generate mock data.
 */
public class TestData {
	public static void main(String[] args) {
//		generateTestMiniData();
		printTestData();
	}

	public static void generateTestMiniData() {
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
	
	
	
	public static void printTestData() {
		InstanceList il = InstanceList.load(new File("instances+0+0+tests"));
		
		Alphabet da = il.getDataAlphabet();
		Alphabet ta = il.getTargetAlphabet();
		
		System.out.println("----------------------- Data Alphabet -----------------------");
		System.out.println("# features: " + da.toArray().length);
		for(Object o : da.toArray()) System.out.println(o);
		System.out.println();
		
		System.out.println("----------------------- Target Alphabet -----------------------");
		System.out.println("# classes: " + ta.toArray().length);
		for(Object o : ta.toArray()) System.out.println(o);
		System.out.println();
		
		System.out.println("----------------------- Instances -----------------------");
		for (Instance instance : il) {
			System.out.println(instance.getName());
			
			System.out.println("class index: " + ((Label)instance.getTarget()).getIndex());
			
			FeatureVector fv = (FeatureVector) instance.getData();
			
			System.out.println("# non-zero features: " + fv.numLocations());
			for (int idx : fv.getIndices()) {
				System.out.println("index: " + idx + "\tvalue: " + fv.value(idx));
			}
			
			System.out.println();
		}
	}
}
