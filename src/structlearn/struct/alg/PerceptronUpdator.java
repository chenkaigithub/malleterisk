/* Copyright (C) 2006 University of Pennsylvania.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

package struct.alg;

import struct.types.*;

/** The perceptron updator.
 * 
 * @version 08/15/2006
 */
public class PerceptronUpdator implements OnlineUpdator {
		
	/*
	 *  (non-Javadoc)
	 * @see struct.alg.OnlineUpdator#update(struct.types.Instance, struct.types.Features, struct.alg.Predictor, double)
	 */
	public void update(SLInstance inst, Features feats, Predictor predictor, double avg_upd) {
		
		Prediction pred = predictor.decode(inst,feats);
		SLLabel label = pred.getBestLabel();
		SLFeatureVector guessed_fv = label.getFeatureVectorRepresentation();
		SLFeatureVector corr_fv = inst.getLabel().getFeatureVectorRepresentation();
		
		double[] weights = predictor.weights;
		double[] avg_weights = predictor.avg_weights;
		
		for(SLFeatureVector curr = corr_fv; curr.next != null; curr = curr.next) {
			if(curr.index >= 0) {
				weights[curr.index] += curr.value;
				avg_weights[curr.index] += avg_upd*curr.value;
			}
		}
		
		for(SLFeatureVector curr = guessed_fv; curr.next != null; curr = curr.next) {
			if(curr.index >= 0) {
				weights[curr.index] -= curr.value;
				avg_weights[curr.index] -= avg_upd*curr.value;
			}
		}		
	}	
}
