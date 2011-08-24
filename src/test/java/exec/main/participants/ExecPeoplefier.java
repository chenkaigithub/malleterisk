package exec.main.participants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.InstanceList;
import classifiers.PeoplefierTrainer;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class ExecPeoplefier {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+peoplefier"));
//		files.add(new File("instances+1+2+peoplefier"));
//		files.add(new File("instances+1+3+peoplefier"));
//		files.add(new File("instances+1+4+peoplefier"));
//		files.add(new File("instances+1+5+peoplefier"));
//		files.add(new File("instances+1+6+peoplefier"));
//		files.add(new File("instances+1+7+peoplefier"));
//		files.add(new File("instances+2+1+peoplefier"));

		for (File file : files)
			run("peoplefier", InstanceList.load(file), new PeoplefierTrainer(), 10);
	}

	public static final void run(
			String name, 
			InstanceList instances, 
			ClassifierTrainer<? extends Classifier> trainer,
			int folds
	) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ExecutionResult r = new ExecutionResult(name, null, null, ExecutionUtils.getClassifierDescription(trainer));
		r.trials.put(0, ExecutionUtils.crossValidate(instances, folds, trainer.getClass().newInstance()));

		r.outputTrials();
		r.outputAccuracies();
	}
}
