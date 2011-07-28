/* Copyright (C) 2006 University of Pennsylvania.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

package struct.alg;

import java.io.*;
import java.util.logging.Logger;

import struct.types.SLEvaluator;
import struct.types.Features;
import struct.types.SLInstance;

/** Learner to train the predictor with instances.
 * 
 * @version 08/15/2006
 */
public class OnlineLearner implements StructuredLearner {
	private static Logger logger = Logger.getLogger(OnlineLearner.class.getName());
	private boolean _averaging;

	/**
	 * Averaging on by default.
	 */
	public OnlineLearner() {
		this._averaging = true;
	}
	
	/**
	 * 
	 * @param averaging - Set averaging.
	 */
	public OnlineLearner(boolean averaging) {
		this._averaging = averaging;
	}
	
	/** Trains the predictor with the given parameters.
	 * 
	 * @throws IOException
	 */
	public void train(SLInstance[] training, OnlineUpdator updator,
			Predictor predictor, int numIters) throws IOException {
		
		for(int i = 0; i < numIters; i++) {
			
			logger.info("Training iteration " + i);
			
			long start = System.currentTimeMillis();
			
			
			for(int j = 0; j < training.length; j++) {
				SLInstance inst = training[j];
				Features feats = training[j].getFeatures();
				
				double avg_upd = (double)(numIters*training.length - (training.length*((i+1)-1)+(j+1)) + 1);
				
				updator.update(inst,feats,predictor,avg_upd);		
				
			}
			
			if(this._averaging) {
				predictor.averageWeights(numIters*training.length);
			}
			
			long end = System.currentTimeMillis();
			logger.info(" took: " + (end-start));			
		}		
	}
	
	/** Trains the predictor with the given parameters
	 * and evaluates its performance. Always averages
	 * parameters.
	 * 
	 * @throws IOException
	 */
	public void trainAndEvaluate(SLInstance[] training,
			SLInstance[] testing,
			OnlineUpdator update,
			Predictor predictor,
			int numIters,
			SLEvaluator eval) throws IOException {
		
		trainAndEvaluate(training,testing,update,predictor,numIters,
				eval,true);
		
	}
	
	/** Trains the predictor with the given parameters
	 * and evaluates its performance.
	 * 
	 * @throws IOException
	 */
	public void trainAndEvaluate(SLInstance[] training,
			SLInstance[] testing,
			OnlineUpdator update,
			Predictor predictor,
			int numIters,
			SLEvaluator eval,
			boolean avgParams) throws IOException {	
		
		for(int i = 0; i < numIters; i++) {
			logger.info(i + " ");
			logger.info("==========================");
			logger.info("Training iteration: " + i);
			logger.info("==========================");
			long start = System.currentTimeMillis();
			
			for(int j = 0; j < training.length; j++) {
				logger.info(".");
				
				SLInstance inst = training[j];
				Features feats = training[j].getFeatures();
				
				double avg_upd = (double)(numIters*training.length - (training.length*((i+1)-1)+(j+1)) + 1);
				
				update.update(inst,feats,predictor,avg_upd);				
			}
			
			logger.info("");
			
			long end = System.currentTimeMillis();
			logger.info("Training took: " + (end-start));
			logger.info("Training");
			eval.evaluate(training,predictor);
			logger.info("Testing");
			eval.evaluate(testing,predictor);
		}
		logger.info("");
		
		if(avgParams) {
			logger.info("Averaging parameters...");
			predictor.averageWeights(numIters*training.length);
		}
		logger.info("==========================");
		logger.info("Final Performance.");
		logger.info("==========================");
		logger.info("Training");
		eval.evaluate(training,predictor);
		logger.info("Testing");
		eval.evaluate(testing,predictor);	
	}	
}
