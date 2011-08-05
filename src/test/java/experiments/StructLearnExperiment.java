package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import struct.classification.KBestMiraClassifierTrainer;
import utils.IteratedExecution;
import cc.mallet.types.InstanceList;
import execution.ExecutionResult;
import execution.ExecutionUtils;
import ft.selection.IFilter;
import ft.selection.methods.FilterByRankedIG;
import ft.weighting.IWeighter;
import ft.weighting.methods.FeatureWeighting;

public class StructLearnExperiment {
	// http://www.seas.upenn.edu/~strctlrn/StructLearn/StructLearn.html
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("instances+1+1+bodies"));

		ArrayList<IWeighter> transformers = new ArrayList<IWeighter>();
		transformers.add(new FeatureWeighting(FeatureWeighting.TF_NONE, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_NONE));

		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		filters.add(new FilterByRankedIG());

		int step = 10;
		int folds = 10;

		for (File file : files) {
			System.out.println("+ processing " + file.getName());
			InstanceList instances = InstanceList.load(file);
			
			for (IWeighter transformer : transformers) {
				System.out.println("- transformer: " + transformer.getDescription());
				for (IFilter filter : filters) {
					System.out.println("- filter: " + filter.getDescription());
						
					ExecutionResult r = new ExecutionResult(file.getName(), transformer.getDescription(), 
							filter.getDescription(), "KBestMiraClassifierTrainer");

					InstanceList transformedInstances = transformer.calculate(instances);
					for (int n : new IteratedExecution(transformedInstances.getDataAlphabet().size(), step)) {
						InstanceList filteredInstances = filter.filter(n, transformedInstances);

						// classifier trainer must be a new instance since it might accumulate the previous alphabet
						// associate the trials to the run with 'n' features
						r.trials.put(n, ExecutionUtils.crossValidate(filteredInstances, folds, new KBestMiraClassifierTrainer(5)));
					}

					// write results
					r.outputTrials();
					r.outputAccuracies();
				}
			}
		}
	}
}
