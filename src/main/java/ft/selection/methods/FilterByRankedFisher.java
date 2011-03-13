package ft.selection.methods;

import java.util.Arrays;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InstanceList;
import cc.mallet.types.RankedFeatureVector;
import cc.mallet.types.SparseVector;
import ft.selection.Filter;
import ft.selection.functions.Functions;

public class FilterByRankedFisher extends Filter {
	public static final int MINIMUM_SCORE = 0;
	public static final int SUM_SCORE = 1;
	public static final int SUM_SQUARED_SCORE = 2;
	
	private final int scoringType;
	
	public FilterByRankedFisher(int scoringType) {
		this.scoringType = scoringType;
	}
		
	// apply the Fisher criterion to all features for all pairs of classes
	// by using the selected method for scoring the features
	@Override
	protected RankedFeatureVector calculate(InstanceList instances) {
		RankedFeatureVector r = null;
		
		switch(scoringType) {
			case MINIMUM_SCORE: r = minScore(this.instances); break;
			case SUM_SCORE: r = sumScore(this.instances); break;
			case SUM_SQUARED_SCORE: r = sumSquaredScore(this.instances); break;
		}
		
		return r;
	}
	
	@Override
	public String getDescription() {
		String st = "";
		
		switch(scoringType) {
		case MINIMUM_SCORE: st = "-Minimum-Score"; break;
		case SUM_SCORE: st = "-Sum-Score"; break;
		case SUM_SQUARED_SCORE: st = "-Sum-Squared-Score"; break;
		}
		
		return super.getDescription() + st;
	}
	
	//
	// Scoring Methods
	//
	
	// conservative method, keeping the lowest Fisher score as the feature's score
	private static final RankedFeatureVector minScore(InstanceList instances) {
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
	private static final RankedFeatureVector sumScore(InstanceList instances) {
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
	private static final RankedFeatureVector sumSquaredScore(InstanceList instances) {
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
