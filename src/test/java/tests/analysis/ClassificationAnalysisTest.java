package tests.analysis;

import java.io.File;
import java.io.FileNotFoundException;

import analysis.CollectionAnalysis;
import cc.mallet.types.InstanceList;

public class ClassificationAnalysisTest {
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		InstanceList instances = InstanceList.load(new File("instances+1+1+bodies"));
		CollectionAnalysis ca = new CollectionAnalysis(instances);
		System.out.println(ca.toString());
	}
}
