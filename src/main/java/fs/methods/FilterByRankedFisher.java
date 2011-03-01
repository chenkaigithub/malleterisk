package fs.methods;

import java.util.Arrays;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import cc.mallet.types.SparseVector;
import fs.IFeatureTransformer;
import fs.functions.Functions;

// TODO: holly molly, this is really SLOW!
public class FilterByRankedFisher implements IFeatureTransformer {
	public static final int MINIMUM_SCORE = 0;
	public static final int SUM_SCORE = 1;
	public static final int SUM_SQUARED_SCORE = 2;
	
	private final int numFeatures;
	private final int scoringType;
	
	public FilterByRankedFisher(int n, int scoringType) {
		this.numFeatures = n;
		this.scoringType = scoringType;
	}
	
	@Override
	public InstanceList transform(InstanceList instances) {
		RankedFeatureVector rfv = null;
		
		// apply the fisher criterion to all features for all pairs of classes
		// by using one of the methods for scoring the features
		switch(scoringType) {
			case MINIMUM_SCORE: rfv = minScore(instances); break;
			case SUM_SCORE: rfv = sumScore(instances); break;
			case SUM_SQUARED_SCORE: rfv = sumSquaredScore(instances); break;
			default: return null;
		}
		
		// return the _HIGHEST_ ranked _numFeatures_ features
		return Functions.fs(instances, new FeatureSelection(rfv, numFeatures));
	}
	
	// conservative method, keeping the lowest Fisher score as the feature's score
	private RankedFeatureVector minScore(InstanceList instances) {
		Alphabet features = instances.getDataAlphabet();
		Alphabet labels = instances.getTargetAlphabet();
		int K = labels.size();
		double[] values = new double[features.size()];
		Arrays.fill(values, -1);
		
		for (int i = 0; i < K; i++) {
			for (int j = i+1; j < K; j++) {
				FeatureVector fv = Functions.fisher(instances, i, j);
				
				for (int l = 0; l < values.length; l++) {
					double v = fv.valueAtLocation(l);
					if(values[l] == -1 || values[l] > v) values[l] = v;
				}
			}
		}
		
		return new RankedFeatureVector(features, values);
	}
	
	// naive method, suming all values of the class pairs
	private RankedFeatureVector sumScore(InstanceList instances) {
		Alphabet features = instances.getDataAlphabet();
		Alphabet labels = instances.getTargetAlphabet();
		int K = labels.size();
		SparseVector sv = new SparseVector(new double[features.size()]);
		
		for (int i = 0; i < K; i++) {
			for (int j = i+1; j < K; j++) {
				FeatureVector fv = Functions.fisher(instances, i, j);
				sv.plusEqualsSparse(fv);
			}
		}
		
		return new RankedFeatureVector(features, sv);
	}
	
	// similar to sumScore, but adding the squared values of the Fisher score
	private RankedFeatureVector sumSquaredScore(InstanceList instances) {
		Alphabet features = instances.getDataAlphabet();
		Alphabet labels = instances.getTargetAlphabet();
		int K = labels.size();
		SparseVector sv = new SparseVector(new double[features.size()]);
		
		for (int i = 0; i < K; i++) {
			for (int j = i+1; j < K; j++) {
				FeatureVector fv = Functions.fisher(instances, i, j);
				fv.timesEqualsSparse(fv);
				sv.plusEqualsSparse(fv);
			}
		}
		
		return new RankedFeatureVector(features, sv);
	}
}
