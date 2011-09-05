package exec.main.participants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import classifiers.PeoplefierTrainer;


import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.InstanceList;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class ExecPeoplefier {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+peoplefier"));
//		files.add(new File("instances+2+2+peoplefier"));
//		files.add(new File("instances+2+3+peoplefier"));
//		files.add(new File("instances+2+4+peoplefier"));
//		files.add(new File("instances+2+5+peoplefier"));
//		files.add(new File("instances+2+6+peoplefier"));
//		files.add(new File("instances+2+7+peoplefier"));
//		files.add(new File("instances+2+8+peoplefier"));

		for (File file : files)
			run(file.getName(), InstanceList.load(file), new PeoplefierTrainer(), 10);
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
