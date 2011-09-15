package experiments;

import java.io.File;
import java.io.FileNotFoundException;

import utils.classification.CombinationUtils;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.InstanceList;
import classifiers.MajorityVotingClassifier;
import classifiers.PeoplefierTrainer;
import classifiers.TopicModelTrainer;
import execution.ExecutionResult;

public class CombinationExperiment {
	public static void main(String[] args) throws FileNotFoundException {
		InstanceList[][] instancelists = new InstanceList[][] {
			new InstanceList[] { InstanceList.load(new File("instances+1+1+body")),	InstanceList.load(new File("instances+1+1+subject")), InstanceList.load(new File("instances+1+1+topics-body")), InstanceList.load(new File("instances+1+1+topics-subject")), InstanceList.load(new File("instances+1+1+peoplefier")) },
			new InstanceList[] { InstanceList.load(new File("instances+2+2+body")),	InstanceList.load(new File("instances+2+2+subject")), InstanceList.load(new File("instances+2+2+topics-body")), InstanceList.load(new File("instances+2+2+topics-subject")), InstanceList.load(new File("instances+2+2+peoplefier")) },
			new InstanceList[] { InstanceList.load(new File("instances+2+3+body")),	InstanceList.load(new File("instances+2+3+subject")), InstanceList.load(new File("instances+2+3+topics-body")), InstanceList.load(new File("instances+2+3+topics-subject")), InstanceList.load(new File("instances+2+3+peoplefier")) },
			new InstanceList[] { InstanceList.load(new File("instances+2+4+body")),	InstanceList.load(new File("instances+2+4+subject")), InstanceList.load(new File("instances+2+4+topics-body")), InstanceList.load(new File("instances+2+4+topics-subject")), InstanceList.load(new File("instances+2+4+peoplefier")) },
			new InstanceList[] { InstanceList.load(new File("instances+2+5+body")),	InstanceList.load(new File("instances+2+5+subject")), InstanceList.load(new File("instances+2+5+topics-body")), InstanceList.load(new File("instances+2+5+topics-subject")), InstanceList.load(new File("instances+2+5+peoplefier")) },
			new InstanceList[] { InstanceList.load(new File("instances+2+6+body")),	InstanceList.load(new File("instances+2+6+subject")), InstanceList.load(new File("instances+2+6+topics-body")), InstanceList.load(new File("instances+2+6+topics-subject")), InstanceList.load(new File("instances+2+6+peoplefier")) },
			new InstanceList[] { InstanceList.load(new File("instances+2+7+body")),	InstanceList.load(new File("instances+2+7+subject")), InstanceList.load(new File("instances+2+7+topics-body")), InstanceList.load(new File("instances+2+7+topics-subject")), InstanceList.load(new File("instances+2+7+peoplefier")) },
			new InstanceList[] { InstanceList.load(new File("instances+2+8+body")),	InstanceList.load(new File("instances+2+8+subject")), InstanceList.load(new File("instances+2+8+topics-body")), InstanceList.load(new File("instances+2+8+topics-subject")), InstanceList.load(new File("instances+2+8+peoplefier")) }
		};
		
		for (int i = 0; i < instancelists.length; i++) {
			@SuppressWarnings("rawtypes")
			ClassifierTrainer[] classifiers = new ClassifierTrainer[] {
				new MaxEntTrainer(),
				new MaxEntTrainer(),
				new TopicModelTrainer(),
				new TopicModelTrainer(),
				new PeoplefierTrainer()
			};
			
			x(i, i, instancelists[i], classifiers);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void x(int i, int j, InstanceList[] instancelists, ClassifierTrainer[] classifiers) throws FileNotFoundException {
		ExecutionResult r = new ExecutionResult("instances+"+i+"+"+j, null, null, MajorityVotingClassifier.class.getSimpleName());
		r.trials.put(0, CombinationUtils.xvClassify(instancelists, classifiers, 10));
		r.outputTrials();
		r.outputAccuracies();
	}
}
