package pp.pipeline;

import java.util.ArrayList;
import java.util.regex.Pattern;

import pp.IPreProcessor;
import pp.mallet.pipes.PorterStemmerSequence;
import types.mallet.EmailBody2Input;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.types.InstanceList;

public class BodyPreProcessor implements IPreProcessor {
	private final InstanceList instanceList;
	
	public BodyPreProcessor() {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		pipes.add(new EmailBody2Input());
		pipes.add(new Input2CharSequence("UTF-8"));
		pipes.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));             
		pipes.add(new TokenSequenceLowercase());
		pipes.add(new TokenSequenceRemoveStopwords(false, false));
		pipes.add(new PorterStemmerSequence());
		pipes.add(new TokenSequence2FeatureSequence());
		pipes.add(new Target2Label());
		pipes.add(new FeatureSequence2FeatureVector());
		
		instanceList = new InstanceList(new SerialPipes(pipes));
	}
	
	@Override
	public InstanceList getInstanceList() {
		return instanceList;
	}
}
