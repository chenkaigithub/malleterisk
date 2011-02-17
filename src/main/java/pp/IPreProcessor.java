package pp;

import cc.mallet.types.InstanceList;

/*
 * Defines a sequence of pre-processing operations that a message goes through.
 */
public interface IPreProcessor {
	InstanceList getInstanceList();
}
