package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

public class DataConverterToLibSVM {
	public static final void main(String[] args) throws FileNotFoundException {
		ArrayList<String> files = new ArrayList<String>();
		files.add("instances+1+1+bodies");
		files.add("instances+1+2+bodies");
		files.add("instances+1+3+bodies");
		files.add("instances+1+4+bodies");
		files.add("instances+1+5+bodies");
		files.add("instances+1+6+bodies");
		files.add("instances+1+7+bodies");
		
		for (String file : files) {
			FileOutputStream out = new FileOutputStream(file+"+libsvm");
			PrintWriter pw = new PrintWriter(out);
			
			InstanceList instances = InstanceList.load(new File(file));

			for (Instance instance : instances) {
				// write label
				pw.write(((Label)instance.getTarget()).getIndex() + " ");
				
				// write index:value
				FeatureVector fv = (FeatureVector) instance.getData();
				int[] indices = fv.getIndices();
				int idx = 0;
				for (int i = 0; i < indices.length; i++) {
					idx = indices[i];
					pw.write(idx+1 + ":" + fv.value(idx) + " ");
				}
				pw.write('\n');
			}
			
			pw.close();
		}
	}
}


