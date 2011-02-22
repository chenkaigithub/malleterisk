package fs.methods;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;

public class TDI implements IFeatureTransformer {

	@Override
	public InstanceList transform(InstanceList instances) {
		InstanceList newInstances = new InstanceList(instances.getDataAlphabet(), instances.getTargetAlphabet());
		
		for (Instance instance : instances) {
			FeatureVector fv = (FeatureVector)instance.getData();
			double[] oldValues = fv.getValues();
			double[] newValues = new double[oldValues.length];
			
			for (int i=0; i < oldValues.length; ++i) newValues[i] = oldValues[i] > 0 ? 1 : 0;
			
			newInstances.add(new Instance(
				new FeatureVector(instance.getDataAlphabet(), fv.getIndices(), newValues), 
				instance.getTarget(), 
				instance.getName(), 
				instance.getSource()
			));
		}
		
		return newInstances;
	}
	
//	public void toBoolean(InstanceList instances) {
//		for (Instance instance : instances) {
//			FeatureVector fv = (FeatureVector)instance.getData();
//			for (int index : fv.getIndices()) {
//				if(fv.value(index) > 0) fv.setValue(index, 1);
//			}
//		}
//	}
}
