package experiments;

import java.io.File;
import java.io.FileNotFoundException;

import utils.classification.CombinationUtils;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.InstanceList;
import classifiers.MajorityVotingClassifier;
import execution.ExecutionResult;

public class CombinationExperiment {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		InstanceList[] instancelists = new InstanceList[2];
		instancelists[0] = InstanceList.load(new File("instances+1+1+body"));
		instancelists[1] = InstanceList.load(new File("instances+1+1+subject"));
		
		@SuppressWarnings("rawtypes")
		ClassifierTrainer[] classifiers = new ClassifierTrainer[2];
		classifiers[0] = new MaxEntTrainer();
		classifiers[1] = new MaxEntTrainer();
		
		ExecutionResult r = new ExecutionResult("instances+1+1", null, null, MajorityVotingClassifier.class.getSimpleName());
		r.trials.put(0, CombinationUtils.xvClassify(instancelists, classifiers, 10));
		r.outputTrials();
		r.outputAccuracies();
	}
}
