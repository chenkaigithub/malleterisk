package exec.main.participants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class ExecParticipantClassifier {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+participants"));
		files.add(new File("instances+2+2+participants"));
		files.add(new File("instances+2+3+participants"));
		files.add(new File("instances+2+4+participants"));
		files.add(new File("instances+2+5+participants"));
		files.add(new File("instances+2+6+participants"));
		files.add(new File("instances+2+7+participants"));
		files.add(new File("instances+2+8+participants"));

		for (File file : files)
			run(file.getName(), InstanceList.load(file), new NaiveBayesTrainer(), 10);
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
