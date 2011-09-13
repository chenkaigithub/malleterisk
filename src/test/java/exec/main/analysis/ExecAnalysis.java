package exec.main.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import types.mallet.LabeledInstancesList;
import cc.mallet.types.InstanceList;
import data.analysis.DataAnalysis;

public class ExecAnalysis {
	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<File> files = new ArrayList<File>();
		
//		files.add(new File("instances+1+1+peoplefier"));
//		files.add(new File("instances+2+2+peoplefier"));
//		files.add(new File("instances+2+3+peoplefier"));
//		files.add(new File("instances+2+4+peoplefier"));
//		files.add(new File("instances+2+5+peoplefier"));
//		files.add(new File("instances+2+6+peoplefier"));
//		files.add(new File("instances+2+7+peoplefier"));
//		files.add(new File("instances+2+8+peoplefier"));
		
//		files.add(new File("instances+1+1+body"));
//		files.add(new File("instances+2+2+body"));
//		files.add(new File("instances+2+3+body"));
//		files.add(new File("instances+2+4+body"));
//		files.add(new File("instances+2+5+body"));
//		files.add(new File("instances+2+6+body"));
//		files.add(new File("instances+2+7+body"));
//		files.add(new File("instances+2+8+body"));
		
		files.add(new File("instances+1+1+date"));
		files.add(new File("instances+2+2+date"));
		files.add(new File("instances+2+3+date"));
		files.add(new File("instances+2+4+date"));
		files.add(new File("instances+2+5+date"));
		files.add(new File("instances+2+6+date"));
		files.add(new File("instances+2+7+date"));
		files.add(new File("instances+2+8+date"));

		for (File file : files) {
			InstanceList instances = InstanceList.load(file);
			LabeledInstancesList lil = new LabeledInstancesList(instances);
			
			// CLASSES - INSTANCES
//			DataAnalysis.toCSV(file.getName()+"-doc-class-dist", DataAnalysis.docClassDistribution(lil));
//			System.out.println(DataAnalysis.averageDocumentsPerClass(instances));

			// FEATURES
//			Map<?, ?> tcf = DataAnalysis.termClassFrequencies(lil);
			
			
			// DATE
			System.out.println("----------------------------------------------------");
//			Map<?, ?> timeClass = DataAnalysis.timeClassIntervals(lil);
//			for (Entry<?, ?> e : timeClass.entrySet())
//				System.out.println(e.getKey() + ": " + e.getValue());
			
			DataAnalysis.mapMapToCSV("stats-"+file.getName(), DataAnalysis.timeClassFrequencies(lil));
			
			// PARTICIPANTS
//			DataAnalysis.participantsClassesCorrelation(instances);
		}
	}
	
	
	
}
