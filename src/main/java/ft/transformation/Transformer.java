package ft.transformation;

import cc.mallet.types.InstanceList;

public abstract class Transformer implements ITransformer {
	@Override
	public abstract InstanceList calculate(InstanceList instances);

	@Override
	public String getDescription() {
		return this.getClass().getSimpleName();
	}
}
