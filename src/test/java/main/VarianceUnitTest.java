package main;

import java.io.File;
import java.util.Iterator;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.functions.Functions;

public class VarianceUnitTest {
	public static final void main(String[] args) {
		/*	manually generated term occurrences:
		duke: 8.0
		citru: 6.0
		glatzer: 1.0
		schedul: 6.0
		montauk: 9.0
		settlement: 7.0
		ecoga: 4.0
		legal: 5.0
		expens: 3.0
		8: 4.0
		17: 3.0
		00: 3.0
	 */
		InstanceList instances = InstanceList.load(new File("instances_0-0_tests"));
		for (Instance instance : instances) {
			System.out.println(instance.getData());
		}
		
		Alphabet alphabet = instances.getDataAlphabet();
		Iterator<?> it = alphabet.iterator();
		while(it.hasNext()) {
			int idx = alphabet.lookupIndex(it.next());
			
			double v = Functions.variance(idx, instances);
			if(v!=0.0) System.out.println("v: "+idx+"="+v);
		}
	}
}
