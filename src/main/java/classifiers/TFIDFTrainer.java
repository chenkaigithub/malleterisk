package classifiers;

import java.util.HashMap;
import java.util.Map;

import types.mallet.LabeledInstancesList;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

/**
 * The TFIDF classifier's training scheme, as described in
 * David J. Ittner, David D. Lewis, and David D. Ahn - Text Categorization of Low Quality Images
 * 
 * This is a IR classifier, based on TFIDF and Rocchio's algorithm.
 * 
 * @author tt
 *
 */
public class TFIDFTrainer extends ClassifierTrainer<Classifier> {
	private Classifier classifier;
	private double beta;
	private double gamma;
	private Map<Object, double[]> prototypes;
	
	public TFIDFTrainer() {
		this(16, 4);
	}
	
	public TFIDFTrainer(double b, double g) {
		this.beta = b;
		this.gamma = g;
		this.prototypes = new HashMap<Object, double[]>();
	}
	
	@Override
	public Classifier getClassifier() {
		return classifier;
	}

	@Override
	public Classifier train(InstanceList trainingSet) {
		Alphabet dataAlphabet = trainingSet.getDataAlphabet();
		LabeledInstancesList lil = new LabeledInstancesList(trainingSet);
		double nc = trainingSet.size();
		
		for (Instance instance : trainingSet) {
			// obtain instance's features
			FeatureVector fv = (FeatureVector) instance.getData();
			
			// retrieve prototype for the class this instance belongs to
			Label l = (Label) instance.getTarget();
			double[] p = prototypes.get(l);
			if(p == null)
				prototypes.put(instance.getTarget(), (p = new double[dataAlphabet.size()]));
			
			// update prototype weights
			double Rc = lil.getNumLabelInstances(l.getIndex());
			for (int i = 0; i < fv.numLocations(); i++) {
				double w = fv.valueAtLocation(i);
				p[fv.indexAtLocation(i)] += (beta * (1.0/Rc) * w) - (gamma * (1.0/(nc-Rc)) * w); 
			}
		}
		
		return (classifier = new TFIDFClassifier(prototypes));
	}

}
