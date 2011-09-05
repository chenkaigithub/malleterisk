package classifiers;

import java.util.Iterator;
import java.util.Map;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;

/**
 * Part of the TFIDF classifier's classification scheme, as described in
 * David J. Ittner, David D. Lewis, and David D. Ahn - Text Categorization of Low Quality Images
 * 
 * ATTN: This is not the full technique described in the paper.
 * This implementation stops at the similarity computation. Logistic Regression is not done.
 * 
 * @author tt
 *
 */
public class TFIDFClassifier extends Classifier {
	private static final long serialVersionUID = 1L;
	private Map<Object, double[]> prototypes;
	
	public TFIDFClassifier(Map<Object, double[]> ps) {
		this.prototypes = ps;
	}
	
	@Override
	public Classification classify(Instance instance) {
		LabelAlphabet ta = (LabelAlphabet)instance.getTargetAlphabet();
		double[] values = new double[ta.size()];

		Iterator<?> it = ta.iterator();
		while(it.hasNext()) {
			Integer labelIdx = (Integer) it.next();
			Label label = ta.lookupLabel(labelIdx);
			
			values[labelIdx-1] = dotProduct((FeatureVector) instance.getData(), prototypes.get(label));
		}
		
		return new Classification(instance, this, new LabelVector(ta, values));
	}
	
	private static double dotProduct(FeatureVector fv1, double[] fv2) {
		double d = 0;
		
		for (int i = 0; i < fv1.numLocations(); i++)
			d += fv1.valueAtLocation(i) * fv2[fv1.indexAtLocation(i)];
		
		return d;
	}

}
