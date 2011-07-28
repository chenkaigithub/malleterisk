/* Copyright (C) 2006 University of Pennsylvania.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */

package struct.alg;

import java.util.*;

import struct.solver.*;
import struct.types.*;

/** THJAUpdator.
 * 
 * @version 08/15/2006
 */
public class THJAUpdator implements OnlineUpdator {
	
	private int K = 2;
	private ArrayList distances;
	private ArrayList margins;
	
	public THJAUpdator() {
		this.distances = new ArrayList();
		this.margins = new ArrayList();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see struct.alg.OnlineUpdator#update(struct.types.Instance, struct.types.Features, struct.alg.Predictor, double)
	 */
	public void update(SLInstance inst, Features feats, Predictor predictor, double avg_upd) {
		
		Prediction pred = predictor.decode(inst,feats,K);
		SLLabel[] labels = new SLLabel[K];
		int k_new = 0;
		for(int k = 0; k < K; k++) {
			labels[k] = pred.getLabelByRank(k);
			if(labels[k] == null)
				break;
			k_new = k+1;
		}
		
		SLFeatureVector corr_fv = inst.getLabel().getFeatureVectorRepresentation();
		
		SLFeatureVector[] guessed_fvs = new SLFeatureVector[k_new];
		double[] b = new double[k_new];
		double[] lam_dist = new double[k_new];
		SLFeatureVector[] dist = new SLFeatureVector[k_new];
		
		for(int k = 0; k < k_new; k++) {
			guessed_fvs[k] = labels[k].getFeatureVectorRepresentation();
			b[k] = inst.getLabel().loss(labels[k]);
			lam_dist[k] = predictor.score(corr_fv) - predictor.score(guessed_fvs[k]);
			b[k] -= lam_dist[k];
			dist[k] = SLFeatureVector.getDistVector(corr_fv,guessed_fvs[k]);
		}
		
		if(dist[0] != null && b[0] > 0.0) { distances.add(dist[0]); margins.add(new Double(b[0])); }
		else if(dist[1] != null) { distances.add(dist[1]); margins.add(new Double(b[1])); }
		if(dist[0] == null && dist[1] == null) return;
		dist = new SLFeatureVector[distances.size()];
		b = new double[dist.length];
		for(int i = 0; i < distances.size(); i++) {
			dist[i] = (SLFeatureVector)distances.get(i);
			b[i] = ((Double)margins.get(i)).doubleValue();
		}
		
		double[] alpha = QPSolver.hildreth(dist,b);
		
		double[] weights = predictor.weights;
		double[] avg_weights = predictor.avg_weights;
		
		SLFeatureVector fv  = null;
		for(int k = 0; k < k_new; k++) {
			fv = dist[k];				
			for(SLFeatureVector curr = fv; curr != null; curr = curr.next) {
				if(curr.index < 0)
					continue;
				weights[curr.index] += alpha[k]*curr.value;
				avg_weights[curr.index] += avg_upd*alpha[k]*curr.value;
			}
		}		
	}	
}
