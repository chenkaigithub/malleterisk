package exec.main.analysis;

import java.io.File;

import cc.mallet.types.InstanceList;
import data.analysis.DataAnalysis;

public class ExecAnalysis {
	public static void main(String[] args) {
		InstanceList instances = InstanceList.load(new File("instances+1+1+peoplefier"));
//		LabeledInstancesList lil = new LabeledInstancesList(instances);
		DataAnalysis.participantsClassesCorrelation(instances);
		
	}
}
