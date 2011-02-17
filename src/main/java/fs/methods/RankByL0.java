package fs.methods;

import cc.mallet.types.InstanceList;
import fs.IFeatureTransformer;

public class RankByL0 implements IFeatureTransformer {
	private final int numFeatures;
	
	public RankByL0(int l0) {
		this.numFeatures = l0;
	}

	@Override
	public InstanceList transform(InstanceList instances) {
		// TODO Auto-generated method stub
		System.out.println(numFeatures);
		
		return null;
	}

}
