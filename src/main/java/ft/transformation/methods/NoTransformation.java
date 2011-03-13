package ft.transformation.methods;

import cc.mallet.types.InstanceList;
import ft.transformation.Transformer;

public class NoTransformation extends Transformer {
	@Override
	public InstanceList calculate(InstanceList instances) {
		return instances;
	}
}
