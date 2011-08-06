package main.old.insticc;

import java.util.ArrayList;
import java.util.regex.Pattern;

import pp.PreProcessor;
import pp.mallet.pipes.PorterStemmerSequence;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import data.loader.IDataSetLoader;


public class BodyPreProcessor extends PreProcessor {
	private static final long serialVersionUID = -3365120647901882307L;

	public BodyPreProcessor() {
	}

	public BodyPreProcessor(IDataSetLoader ds) {
		super(ds);
	}
	
	@Override
	protected ArrayList<Pipe> getPipes() {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		
		pipes.add(new Input2CharSequence("UTF-8"));
		pipes.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));             
		pipes.add(new TokenSequenceLowercase());
		pipes.add(new TokenSequenceRemoveStopwords(false, false));
		pipes.add(new PorterStemmerSequence());
		pipes.add(new TokenSequence2FeatureSequence());
		pipes.add(new Target2Label());
		pipes.add(new FeatureSequence2FeatureVector());
		
		return pipes;
	}
}

