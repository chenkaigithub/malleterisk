package exec.main.participants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import classifiers.LibLinearTrainer;
import classifiers.PeoplefierTrainerV2;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class ExecPeoplefierV2 {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+peoplefier"));
		files.add(new File("instances+2+2+peoplefier"));
		files.add(new File("instances+2+3+peoplefier"));
		files.add(new File("instances+2+4+peoplefier"));
		files.add(new File("instances+2+5+peoplefier"));
		files.add(new File("instances+2+6+peoplefier"));
		files.add(new File("instances+2+7+peoplefier"));
		files.add(new File("instances+2+8+peoplefier"));

		for (File file : files) {
			InstanceList il = PeoplefierTrainerV2.peoplefyInstances(InstanceList.load(file));
			
			Alphabet da = il.getDataAlphabet();
			for (Instance instance : il) {
				System.out.println(instance.getName());
				FeatureVector fv = (FeatureVector) instance.getData();
				int nl = fv.numLocations();
				for (int i = 0; i < nl; i++)
					System.out.println(da.lookupObject(fv.indexAtLocation(i)) + ": " + fv.valueAtLocation(i));
			}
			
			run(file.getName(), il, new LibLinearTrainer(), 10);
		}
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
