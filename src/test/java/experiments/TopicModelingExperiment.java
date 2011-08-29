package experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Pattern;

import types.mallet.pipe.EmailBody2Input;
import types.mallet.pipe.PorterStemmerSequence;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;
import data.loader.DbDataSetLoader;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;

public class TopicModelingExperiment {
	public static void main(String[] args) throws IOException, SQLException {
//		Vector2Topics
//		x();
//		y();
		toTopicsFile();
	}
	
	public static void modelAssociatedPress() throws IOException {
		// Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords() );
        pipeList.add( new TokenSequence2FeatureSequence() );
        InstanceList instances = new InstanceList (new SerialPipes(pipeList));
        
        Reader fileReader = new InputStreamReader(new FileInputStream(new File("ap.txt")), "UTF-8");
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data, label, name fields
        System.out.println("xxxxxxxxxxxxxxx " + instances.size());
        
        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        int numTopics = 100;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop (this is for testing only, 
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(50);
        model.estimate();

        // Show the words and topics in the first instance

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        System.out.println("xxxxxxxxxxxxxxx " + model.getData().size());
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++)
            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
        System.out.println(out);
        
        // Show top 5 words in topics with proportions for the first document
        for(Object[] topWords : model.getTopWords(5)) {
        	for (Object w : topWords) {
				System.out.print(w + " ");
			}
            System.out.println();
        }		

//        // Create a new instance with high probability of topic 0
//        StringBuilder topicZeroText = new StringBuilder();
//        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();
//
//        int rank = 0;
//        while (iterator.hasNext() && rank < 5) {
//        	IDSorter idCountPair = iterator.next();
//        	topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
//        	rank++;
//        }
//
//        // Create a new instance named "test instance" with empty target and source fields.
//        InstanceList testing = new InstanceList(instances.getPipe());
//        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));
//
//        TopicInferencer inferencer = model.getInferencer();
//        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
//        System.out.println("0\t" + testProbabilities[0]);
	}
	
	private static void toTopicsFile() throws SQLException {
		DbDataSetLoader loader = new DbDataSetLoader(new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql")), 1, 1);
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
		instances.save(new File("instances+1+1+topics"));
	}
	
	public static void y() throws IOException, SQLException {
        InstanceList instances = InstanceList.load(new File("instances+1+1+topics"));

		int numTopics = 33;
		InstanceList[] split = instances.split(new double[] {0.8, 0.2});
		ParallelTopicModel lda = new ParallelTopicModel(numTopics); 
		lda.addInstances(split[0]); 
		lda.estimate();
		
//		MarginalProbEstimator evaluator = lda.getProbEstimator(); 
//		double logLikelihood = evaluator.evaluateLeftToRight(split[1], 10, false, null);
//		System.out.println("log likelihood: " + logLikelihood);
		
//		Formatter out = new Formatter(new StringBuilder(), Locale.US);
//		for (int position = 0; position < tokens.getLength(); position++)
//			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
//		System.out.println(out);

		// Show top 5 words in topics
		for(Object[] topWords : lda.getTopWords(5)) {
			for (Object w : topWords) {
				System.out.print(w + " ");
			}
			System.out.println();
		}

        TopicInferencer inferencer = lda.getInferencer(); 
		for (Instance instance : split[1]) {
			double[] topicProbs = inferencer.getSampledDistribution(instance, 100, 10, 10);
			for (double d : topicProbs) System.out.print(d + "\t");
			System.out.println();
		}
	}
}
