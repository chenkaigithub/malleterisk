package fs.methods;

import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;

public class TDI implements IFeatureTransformer {

	@Override
	public InstanceList transform(InstanceList instances) {
		// TODO Auto-generated method stub
		
		// todo: toBoolean(instances)
		/*
		 * for(Instance instance : instances)
		 *     FeatureVector fv = ...
		 *     for(feature in instance.getDataAlphabet())
		 *         if(fv.value(...) > 0) fv.setValue(x, 1)
		 *         else fv.setValue(x, 0);
		 * 
		 * 
		 */
		
		return null;
	}

}
