package ft.transformation;

import cc.mallet.types.InstanceList;

public interface ITransformer {
	InstanceList calculate(InstanceList instances);
	String getDescription();
}
