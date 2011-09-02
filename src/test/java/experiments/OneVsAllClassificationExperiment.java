package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cc.mallet.types.InstanceList;
import classifiers.LibLinearTrainer;
import data.classification.OneVersusAll;
import execution.ExecutionResult;
import execution.ExecutionUtils;

public class OneVsAllClassificationExperiment {
	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<File> files = new ArrayList<File>();
//		files.add(new File("instances+1+1+body"));
//		files.add(new File("instances+2+2+body"));
//		files.add(new File("instances+2+3+body"));
//		files.add(new File("instances+2+4+body"));
//		files.add(new File("instances+2+5+body"));
//		files.add(new File("instances+2+6+body"));
//		files.add(new File("instances+2+7+body"));
//		files.add(new File("instances+2+8+body"));
		
		for (File file : files) {
			InstanceList instancelist = InstanceList.load(file);
			OneVersusAll ova = new OneVersusAll(instancelist);
			
			while(ova.hasNext()) {
				ExecutionResult r = new ExecutionResult(file.getName()+"+"+ova.getCurrentOneClassIndex(), null, null, "");
				r.trials.put(0, ExecutionUtils.crossValidate(ova.next(), 10, new LibLinearTrainer()));

				r.outputTrials();
				r.outputAccuracies();
			}
		}
	}
}
