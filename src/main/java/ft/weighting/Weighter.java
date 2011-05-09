package ft.weighting;

import cc.mallet.types.InstanceList;

public abstract class Weighter implements IWeighter {
	@Override
	public abstract InstanceList calculate(InstanceList instances);

	@Override
	public String getDescription() {
		return this.getClass().getSimpleName();
	}
}
