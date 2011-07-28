/* Copyright (C) 2006 University of Pennsylvania.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */

package struct.sequence;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import struct.alg.Predictor;
import struct.types.Features;
import struct.types.SLFeatureVector;
import struct.types.SLInstance;
import cc.mallet.types.Alphabet;

/** A predictor for sequences.
 * 
 * @version 07/15/2006
 */
public class SequencePredictor extends Predictor {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(SequencePredictor.class.getName());

	private boolean disalowIntoStart;
	
	/**
	 * 
	 * @param dimensions The number of features
	 */
	public SequencePredictor(int dimensions) {
		super(dimensions);
		disalowIntoStart = false;
	}
	
	/**
	 * 
	 * @param dimensions The number of features
	 */
	public SequencePredictor(int dimensions, boolean disalowIntoStart) {
		super(dimensions);
		this.disalowIntoStart = disalowIntoStart;
	}
	
	public void saveModel(String file) throws Exception {
		logger.info("Saving model ... ");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		out.writeObject(weights);
		out.writeObject(SequenceInstance.getDataAlphabet());
		out.writeObject(SequenceInstance.getTagAlphabet());
		out.close();
		logger.info("done.");
	}
	
	public void loadModel(String file) throws Exception {
		logger.info("Loading model ... ");
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		weights = (double[])in.readObject();
		SequenceInstance.setDataAlphabet((Alphabet)in.readObject());
		SequenceInstance.setTagAlphabet((Alphabet)in.readObject());
		in.close();
		logger.info("done.");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see struct.alg.Predictor#decode(struct.types.Instance, struct.types.Features)
	 */
	public SequencePrediction decode(SLInstance inst, Features feats) {
		return decode(inst,feats,1);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see struct.alg.Predictor#decode(struct.types.Instance, struct.types.Features, int)
	 */
	public SequencePrediction decode(SLInstance inst, Features feats, int K) {
		SequenceInstance sinst = (SequenceInstance)inst;
		
		Alphabet tagAlphabet = SequenceInstance.getTagAlphabet();
		
		String[] toks = sinst.getInput().sentence;
		
		KBestSequence bs = new KBestSequence(tagAlphabet,sinst,K);
		
		
		SequenceFeatures sf = (SequenceFeatures)feats;
		
		int startLab = tagAlphabet.lookupIndex("O");
		//System.out.println("startLab="+startLab);
		bs.add(0,startLab,0.0,new SLFeatureVector(-1,-1.0,null),null,0);
		for(int i = 0; i < tagAlphabet.size(); i++)
			bs.add(0,i,Double.NEGATIVE_INFINITY,new SLFeatureVector(-1,-1.0,null),null,0);
		
		for(int n = 1; n < toks.length; n++) {
			for(int e = 0; e < tagAlphabet.size(); e++) {			
				for(int prev_e = 0; prev_e < tagAlphabet.size(); prev_e++) {
					double addV = disalowIntoStart && e == startLab ? Double.NEGATIVE_INFINITY : 0.0;
					SLFeatureVector fv_ij = sf.getFeatureVector(n,prev_e,e);
					double prob_ij = score(fv_ij);
					SLFeatureVector fv_i = sf.getFeatureVector(n,e);
					double prob_i = score(fv_i);
					SequenceItem[] items = bs.getItems(n-1,prev_e);
					
					if(items == null)
						continue;
					
					int strt = 0;
					for(int i = 0; i < items.length; i++) {
						if(items[i].prob == Double.NEGATIVE_INFINITY)
							continue;
						strt = bs.add(n,e,prob_ij+prob_i+items[i].prob+addV,
								SLFeatureVector.cat(fv_ij,fv_i),items[i],strt);
						if(strt < 0)
							break;
					}		    
				}		
			}
		}	
		return bs.getBestSequences();
	}
	
	/** KBestSequence. 
	 */
	private class KBestSequence {
		
		private SequenceItem[][][] chart;
		private int K;
		private int num_tags;
		private Alphabet tagAlphabet;
		
		private KBestSequence(Alphabet tagAlphabet, SequenceInstance inst, int K) {
			this.K = K;
			chart = new SequenceItem[inst.getInput().sentence.length][tagAlphabet.size()][K];
			num_tags = tagAlphabet.size();
			this.tagAlphabet = tagAlphabet;
		}
		
		private int add(int n, int e, double prob, SLFeatureVector fv, SequenceItem prev, int strt) {
			
			if(chart[n][e][0] == null) {
				for(int i = 0; i < K; i++)
					chart[n][e][i] = new SequenceItem(n,e,Double.NEGATIVE_INFINITY,null,null);
			}
			
			if(chart[n][e][K-1].prob > prob)
				return -1;
			
			for(int i = strt; i < K; i++) {
				if(chart[n][e][i].prob < prob) {
					SequenceItem tmp = chart[n][e][i];
					chart[n][e][i] = new SequenceItem(n,e,prob,fv,prev);
					for(int j = i+1; j < K && tmp.prob != Double.NEGATIVE_INFINITY; j++) {
						SequenceItem tmp1 = chart[n][e][j];
						chart[n][e][j] = tmp;
						tmp = tmp1;
					}
					return i+1 >= K ? -1 : i+1;
				}    			
			}
			return -1;
		}
		
		private double getProb(int n, int e) {
			return getProb(n,e,0);
		}
		
		private double getProb(int n, int e, int i) {
			if(chart[n][e][i] != null)
				return chart[n][e][i].prob;
			return Double.NEGATIVE_INFINITY;
		}
		
		private double[] getProbs(int n, int e) {
			double[] result = new double[K];
			for(int i = 0; i < K; i++)
				result[i] =
					chart[n][e][i] != null ? chart[n][e][i].prob : Double.NEGATIVE_INFINITY;
					return result;
		}
		
		private SequenceItem getItem(int n, int e) {
			return getItem(n,e,0);
		}
		
		private SequenceItem getItem(int n, int e, int i) {
			if(chart[n][e][i] != null)
				return chart[n][e][i];
			return null;
		}
		
		private SequenceItem[] getItems(int n, int e) {
			if(chart[n][e][0] != null)
				return chart[n][e];
			return null;
		}
		
		private SequencePrediction getBestSequences() {
			
			int n = chart.length-1;
			
			SequenceItem[] best = new SequenceItem[K];
			for(int i = 0; i < K; i++) {
				best[i] = new SequenceItem(-1,-1,Double.NEGATIVE_INFINITY,null,null);
			}
			
			for(int e = 0; e < num_tags; e++) {
				for(int k = 0; k < K; k++) {
					SequenceItem cand = chart[n][e][k];
					
					for(int i = 0; i < K; i++) {
						if(best[i].prob < cand.prob) {
							SequenceItem tmp = best[i];
							best[i] = cand;
							for(int j = i+1; j < K && tmp.prob != Double.NEGATIVE_INFINITY; j++) {
								SequenceItem tmp1 = best[j];
								best[j] = tmp;
								tmp = tmp1;
							}
							break;
						}
					}
					
				}
			}			
			
			SequenceLabel[] d = new SequenceLabel[K];
			for(int k = 0; k < K; k++) {
				if(best[k].prob != Double.NEGATIVE_INFINITY) {
					d[k] = new SequenceLabel(getEntString(best[k]).split(" "),getFeatureVector(best[k]));
				}
				else {
					d[k] = null;
				}
			}
			return new SequencePrediction(d);
		}
		
		private SLFeatureVector getFeatureVector(SequenceItem si) {
			if(si.prev == null)
				return si.fv;
			
			return SLFeatureVector.cat(getFeatureVector(si.prev),si.fv);
		}
		
		private String getEntString(SequenceItem si) {
			if(si.prev == null)
				return ((String)tagAlphabet.lookupObject(si.e)).trim();
			
			return (getEntString(si.prev) + " " + (String)tagAlphabet.lookupObject(si.e)).trim();
		}    	
	}
	
	/** Item for a sequence.
	 */
	private class SequenceItem {
		
		private int n,e;
		private double prob;
		private SLFeatureVector fv;
		private SequenceItem prev;
		
		private SequenceItem(int n, int e,
				double prob, SLFeatureVector fv,
				SequenceItem prev) {
			
			this.n = n;
			this.e = e;
			this.prob = prob;
			this.fv = fv;
			this.prev = prev;
		}		
		
		private SequenceItem() {}		
	}
	
	/* 
	 *  (non-Javadoc)
	 * @see struct.alg.Predictor#grow(int)
	 */
	public void grow(int newSize) {
		double[] newWeights = new double[newSize];
		double[] newAvg_weights = new double[newSize];
		for(int i=0; i<weights.length; i++) {
			newWeights[i] = weights[i];
			newAvg_weights[i] = avg_weights[i];
		}
		weights = newWeights;
		avg_weights = newAvg_weights;				
	}
}
