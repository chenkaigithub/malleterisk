package analysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Trial;
import cc.mallet.types.Instance;
import cc.mallet.types.Labeling;

public 	class Result {
	public final String name;
	public final String transformer;
	public final String filter;
	public final String classifier;
	
	public Map<Integer, Collection<Trial>> trials;
	
	public Result(String n, String t, String f, String c) {
		this.name = n;
		this.transformer = t;
		this.filter = f;
		this.classifier = c;
		this.trials = new HashMap<Integer, Collection<Trial>>();
	}
	
	/**
	 * Writes the results of the classification into the output stream in a formatted manner.
	 * (instance, real_class_idx, real_class, class_n1_idx, class_n1, class_n1_val, ..., class_nK_idx, class_nK, class_nK_val)
	 */
	public void trial2out() throws FileNotFoundException {
		Instance instance;
		Labeling labeling;
		
		for (Integer n : this.trials.keySet()) {
			for (Trial trial : this.trials.get(n)) {
				FileOutputStream out = new FileOutputStream(getTrialOutName());
				PrintWriter pw = new PrintWriter(out);
				
				for (Classification classification : trial) {
					// write out results in the form of:
					// instance, real_class_idx, real_class, class_n1_idx, class_n1, val_n1, ..., class_nK_idx, class_nK, val_nK
					// class_nN = class classified at position N
					// val_nN = value of class at position N
					
					// instance
					instance = classification.getInstance();
					pw.write(instance.getName() + ", ");
					
					// real class
					pw.write(instance.getLabeling().getBestIndex() + ", ");
					pw.write(instance.getLabeling().getBestLabel() + ", ");
					
					// pairs of class_nN, val_nN
					labeling = classification.getLabeling();
					int nl = labeling.numLocations();
					for(int i=0; i < nl; ++i) {
						pw.write(labeling.indexAtLocation(i) + ", ");
						pw.write(labeling.labelAtLocation(i) + ", ");
						pw.write(String.valueOf(labeling.valueAtLocation(i)));
						
						if(i+1 < nl) pw.write(", ");
					}
					pw.write('\n');
				}
				
				pw.flush();
				pw.close();
			}
		}
	}
	
	/**
	 * Writes the accuracies of the trials into a file, in a formatted manner.
	 * (number_of_features, trial1_accuracy, trial2_accuracy, ..., trialN_accuracy)
	 * 
	 * Used to plot feature selection/classification accuracy graph.
	 * 
	 */
	public void accuracies2out() throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(getAccuraciesOutName());
		PrintWriter pw = new PrintWriter(out);
		
		for (Integer n : this.trials.keySet()) {
			pw.write(n.toString());
			pw.write(", ");
			int i = 0;
			for (Trial trial : this.trials.get(n)) {
				pw.write(String.valueOf(trial.getAccuracy()));
				if(i++ < trials.size()) pw.write(", ");
			}
			pw.write('\n');
		}
		
		pw.flush();
		pw.close();
	}
	
	private String getTrialOutName() {
		StringBuffer sb = new StringBuffer();
		sb.append("trial");
		sb.append("+");
		sb.append(this.name);
		sb.append("+");
		sb.append(this.transformer);
		sb.append("+");
		sb.append(this.filter);
		sb.append("+");
		sb.append(this.classifier);
		sb.append("+");
		sb.append(new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date()));
		
		return sb.toString();
	}
	
	private String getAccuraciesOutName() {
		StringBuffer sb = new StringBuffer();
		sb.append("accuracies");
		sb.append("+");
		sb.append(this.name);
		sb.append("+");
		sb.append(this.transformer);
		sb.append("+");
		sb.append(this.filter);
		sb.append("+");
		sb.append(this.classifier);
		sb.append("+");
		sb.append(new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date()));
		
		return sb.toString();
	}
}
