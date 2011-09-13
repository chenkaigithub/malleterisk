package utils.classification;

import java.util.Collection;
import java.util.LinkedList;

import types.mallet.classify.ExtendedTrial;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import classifiers.MajorityVotingClassifier;

public class CombinationUtils {
	
	public static Collection<ExtendedTrial> xvClassify(InstanceList[] instancelists, ClassifierTrainer<Classifier>[] classifiers, int numFolds) {
		LinkedList<ExtendedTrial> trials = new LinkedList<ExtendedTrial>();
		int n = instancelists.length;
		
		// do the cross validation split on the first instancelist
		// this cross validation will be applied to all of the other instancelists
		InstanceList instances = instancelists[0];
		CrossValidationIterator cvi = instances.crossValidationIterator(numFolds);
		
		InstanceList[] split = null;
		int foldCounter = 0;
		while(cvi.hasNext()) {
			split = cvi.next();
			
			// this will do the following:
			// - apply the previous split to all of the other instancelists
			// - pass the splits and the respective classifiers into a meta-classifier
			// - add results into the trials
			trials.add(new ExtendedTrial(
				new MajorityVotingClassifier(
					split(n, instancelists, split), 
					classifiers
				), 
				split[0], 
				split[1], 
				foldCounter++
			));
		}
		
		return trials;
	}

	public static InstanceList[][] split(int n, InstanceList[] instancelists, InstanceList[] split) {
		InstanceList[][] splits = new InstanceList[n][2];
		
		for (int i = 0; i < n; i++)
			splits[i] = mapSplit(instancelists[i], split);

		return splits;
	}
	
	public static InstanceList[] mapSplit(InstanceList src, InstanceList[] match) {
		InstanceList[] dst = new InstanceList[2];
		dst[0] = dst[1] = new InstanceList(src.getDataAlphabet(), src.getTargetAlphabet());;
		
		for (Instance instance : src) {
			if(exists(instance.getName(), match[1]))
				dst[1].add(instance);
			else
				dst[0].add(instance);
		}
		
		return dst;
	}
	
	public static boolean exists(Object name, InstanceList il) {
		for (Instance instance : il)
			if(instance.getName().equals(name))
				return true;
		
		return false;
	}
	
	public static Instance get(Object name, InstanceList il) {
		Instance i = null;
		
		for (Instance instance : il) {
			if(instance.getName().equals(name)) {
				i = instance;
				break;
			}
		}
		
		return i;
	}
}
