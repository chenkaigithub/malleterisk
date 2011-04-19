package imbalance;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.SparseVector;

/**
 * Implements SMOTE algorithm for class imbalance learning.
 * Chawla, Bowyer, Hall, Kegelmeyer - SMOTE : Synthetic Minority Over-sampling Technique
 * 
 * @author tt
 */
public class SMOTE {
	private static final Random random = new Random();
	
	public static final InstanceList smote(InstanceList instances, int n, int k) {
		Alphabet dataAlphabet = instances.getDataAlphabet();
		Alphabet targetAlphabet = instances.getTargetAlphabet();
		InstanceList newInstances = new InstanceList (new Noop(dataAlphabet, targetAlphabet));
		
		for (Instance instance : instances) {
			Instance[] knn = KNN.knn(k, instance, instances);
			newInstances.addAll(populate(dataAlphabet, targetAlphabet, n, instance, knn));
		}
		
		return newInstances;
	}
	
	private static final InstanceList populate(Alphabet dataAlphabet, Alphabet targetAlphabet, int n, Instance instance, Instance[] knn) {
		InstanceList instances = new InstanceList (new Noop(dataAlphabet, targetAlphabet));
		
		while(n>0) {
			// randomly select one of the knn instances'
			Instance selectedInstance = knn[random.nextInt(knn.length)];
			
			SparseVector sv1 = (SparseVector)instance.getData();
			SparseVector sv2 = (SparseVector)selectedInstance.getData();
			
			// sv = minus(instance, selectedInstance) | calculate the distance between vectors
			FeatureVector sv = (FeatureVector) sv1.cloneMatrix();
			sv.plusEqualsSparse(sv2, -1);
			// sv += selectedInstance | extend the vector over the border of the class
			sv.plusEqualsSparse(sv2);
			// randomly select attributes for the synthetic instance
			sv = randomizeAttributes(sv);

			// TODO: not taking care of possibly empty instances! should I worry?
			instances.addThruPipe(new Instance(sv, instance.getTarget(), UUID.randomUUID(), instance.getSource()));
			
			n--; // do this 'n' times
		}
		
		return instances;
	}
	
	private static final FeatureVector randomizeAttributes(FeatureVector sv) {
		int[] indices = sv.getIndices();
		double[] values = sv.getValues();
		
		int[] selectedIndices = new int[indices.length];
		double[] selectedValues = new double[values.length];
		int selectedCounter = 0;
		
		// iterate through all the features and randomly select them
		for(int idx : sv.getIndices()) {
			if(random.nextInt(2)==1) {
				selectedIndices[selectedCounter] = idx;
				selectedValues[selectedCounter++] = sv.value(idx);
			}
		}
		
		// if "for some reason" (like... probabilities) the number of selected features is
		// smaller than the original number of features, resize the arrays
		if(selectedCounter < indices.length) {
			Arrays.copyOf(selectedIndices, selectedCounter);
			Arrays.copyOf(selectedValues, selectedCounter);
		}
		
		return new FeatureVector(sv.getAlphabet(), selectedIndices, selectedValues);
	}
}
