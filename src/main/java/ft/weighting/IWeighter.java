package ft.weighting;

import cc.mallet.types.InstanceList;

public interface IWeighter {
	InstanceList calculate(InstanceList instances);
	String getDescription();
}
