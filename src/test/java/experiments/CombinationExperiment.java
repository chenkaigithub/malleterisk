package experiments;

import java.io.File;
import java.io.FileNotFoundException;

import utils.classification.CombinationUtils;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.InstanceList;
import classifiers.MajorityVotingClassifier;
import classifiers.PeoplefierTrainer;
import execution.ExecutionResult;

public class CombinationExperiment {
	public static void main(String[] args) throws FileNotFoundException {
		
		InstanceList[] instancelists = new InstanceList[] {
			InstanceList.load(new File("instances+1+1+body")),
			InstanceList.load(new File("instances+1+1+subject")),
			InstanceList.load(new File("instances+1+1+topics-body")),
			InstanceList.load(new File("instances+1+1+topics-subject")),
			InstanceList.load(new File("instances+1+1+peoplefier"))
		};
		
		@SuppressWarnings("rawtypes")
		ClassifierTrainer[] classifiers = new ClassifierTrainer[] {
			new MaxEntTrainer(),
			new MaxEntTrainer(),
			new PeoplefierTrainer()
		};
		
		x(instancelists, classifiers);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void x(InstanceList[] instancelists, ClassifierTrainer[] classifiers) throws FileNotFoundException {
		ExecutionResult r = new ExecutionResult("instances+1+1", null, null, MajorityVotingClassifier.class.getSimpleName());
		r.trials.put(0, CombinationUtils.xvClassify(instancelists, classifiers, 10));
		r.outputTrials();
		r.outputAccuracies();
	}
}
