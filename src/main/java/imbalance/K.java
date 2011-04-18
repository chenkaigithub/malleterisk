package imbalance;

import cc.mallet.cluster.KMeans;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Metric;

public class K {
	/**
	 * Magnitude = ||x|| = sqrt(sum(x^2))
	 * @param instance
	 * @return
	 */
	public static double magnitude(FeatureVector instance) {
		double squaredSum = 0;
		
		for (double d : instance.getValues())
			squaredSum += d * d;
		
		return Math.sqrt(squaredSum);
	}
	
	/**
	 * cosine(v1, v2) = <v1, v2> / (||v1||*||v2||)
	 * @param fv1
	 * @param fv2
	 * @return
	 */
	public static double cosine(FeatureVector fv1, FeatureVector fv2) {
		double dot = fv1.dotProduct(fv2);
		double mag1 = magnitude(fv1);
		double mag2 = magnitude(fv2);
		
		// 0/x = 0; 
		// x/0 = infin (avoid infinity)
		// 0/0 = indet (avoid indetermination)
		
		double cosine = 0;
		if(dot!=0 && mag1!=0 && mag2!=0)
			cosine = dot / (mag1*mag2);

		return cosine;
	}

	public static Instance[] knn(Instance instance, int k, InstanceList instances) {
		Instance[] nearestNeighbours = new Instance[k];
		
		
		return nearestNeighbours;
	}
}
