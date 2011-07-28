/* Copyright (C) 2006 University of Pennsylvania.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */

package struct.sequence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.logging.Logger;

import struct.types.DataManager;
import struct.types.SLFeatureVector;
import cc.mallet.types.Alphabet;

/** A manager to manage sequence data.
 * 
 * @version 07/15/2006
 */
public abstract class SequenceDataManager implements DataManager {
	private static Logger logger = Logger.getLogger(SequenceDataManager.class.getName());

	public Alphabet dataAlphabet;	
	public Alphabet tagAlphabet;	
	
	public SequenceDataManager()  {
		dataAlphabet = new Alphabet();
		tagAlphabet = new Alphabet();		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see struct.types.DataManager#getDataAlphabet()
	 */
	public Alphabet getDataAlphabet() { 
		return dataAlphabet; 
	}
	
	public Alphabet getTagAlphabet() { 
		return tagAlphabet; 
	}
	
	/*
	 *  (non-Javadoc)
	 * @see struct.types.DataManager#readData(java.lang.String)
	 */
	public SequenceInstance[] readData(String file) throws IOException {
		return readData(file,true);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see struct.types.DataManager#readData(java.lang.String, boolean)
	 */
	public SequenceInstance[] readData(String file, boolean createFeatureFile) throws IOException {
		
		BufferedReader in =
			new BufferedReader(new InputStreamReader(new FileInputStream(file),"8859_2"));
		String line = in.readLine();
		String pos_line = in.readLine();
		String tags_line = in.readLine();
		
		LinkedList lt = new LinkedList();
		
		ObjectOutputStream out = createFeatureFile ? new ObjectOutputStream(new FileOutputStream(file+".feats")) : null;
		
		//BufferedWriter crf = new BufferedWriter(new FileWriter(file+".crf"));
		
		//int num1 = 0;
		//System.out.println("Creating feature vectors and/or forests ...");
		while(line != null) {
			/*if(num1 > 0 && num1 % 500 == 0)
			 System.out.println("Creating Feature Vector Instance: " + num1
			 + ", Num Feats: " + dataAlphabet.size());*/
			
			String[] toks = line.split("\t");
			String[] pos = pos_line.split("\t");
			String[] tags = tags_line.split("\t");
			
			String[] toks_new = new String[toks.length+1];
			String[] pos_new = new String[pos.length+1];
			String[] tags_new = new String[tags.length+1];
			toks_new[0] = "<init>";
			pos_new[0] = "<init-POS>";
			tags_new[0] = "O";
			for(int i = 0; i < toks.length; i++) {
				toks_new[i+1] = normalize(toks[i]);
				pos_new[i+1] = pos[i];
				tags_new[i+1] = tags[i];
			}
			toks = toks_new;
			pos = pos_new;
			tags = tags_new;
			
			LinkedList[] predicates = getPredicates(toks,pos);
			SLFeatureVector fv = 
				createFeatureVector(predicates,tags,new SLFeatureVector(-1,-1.0,null));	
			fv.sort();
			
			SequenceInstance si = new SequenceInstance(toks,tags,fv,predicates);
			
			if(createFeatureFile)
				createForest(si,predicates,out);
			
			//createCRF(predicates,tags,crf);
			
			lt.add(si);
			
			line = in.readLine();
			pos_line = in.readLine();
			tags_line = in.readLine();
			//num1++;
		}
		
		SequenceInstance[] si = new SequenceInstance[lt.size()];
		for(int i = 0; i < si.length; i++) {
			si[i] = (SequenceInstance)lt.get(i);
		}
		
		if(createFeatureFile)
			out.close();
		
		//crf.close();
		return si;
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see struct.types.DataManager#createAlphabets(java.lang.String)
	 */
	public void createAlphabets(String file) throws IOException {
		createAlphabets(file,false);
	}
	
	/** Creates alphabets.
	 * @throws IOException
	 */
	public void createAlphabets(String file, boolean createUnsupported) throws IOException {
		logger.info("Creating Entity Alphabet ... ");
		createTagAlphabet(file);
		logger.info("done.");
		logger.info("Num Labels: " + tagAlphabet.size());
		for(int i = 0; i < tagAlphabet.size(); i++)
			logger.info((String)tagAlphabet.lookupObject(i));
		
		logger.info("Creating Data Alphabet ... ");
		
		BufferedReader in =
			new BufferedReader(new InputStreamReader(new FileInputStream(file),"8859_2"));
		String line = in.readLine();
		String pos_line = in.readLine();
		String tags_line = in.readLine();
		
		int cnt = 0;
		while(line != null) {
			
			String[] toks = line.split("\t");
			String[] pos = pos_line.split("\t");
			String[] tags = tags_line.split("\t");
			
			String[] toks_new = new String[toks.length+1];
			String[] pos_new = new String[pos.length+1];
			String[] tags_new = new String[tags.length+1];
			toks_new[0] = "<init>";
			pos_new[0] = "<init-POS>";
			tags_new[0] = "O";
			for(int i = 0; i < toks.length; i++) {
				toks_new[i+1] = normalize(toks[i]);
				pos_new[i+1] = pos[i];
				tags_new[i+1] = tags[i];
			}
			toks = toks_new;
			pos = pos_new;
			tags = tags_new;
			
			LinkedList[] predicates = getPredicates(toks,pos);
			SLFeatureVector fv = createFeatureVector(predicates,tags,new SLFeatureVector(-1,-1.0,null));
			
			if(createUnsupported)
				createU(predicates);
			
			line = in.readLine();
			pos_line = in.readLine();
			tags_line = in.readLine();
			//System.out.println(cnt + ": " + dataAlphabet.size());
			cnt++;
		}
		
		logger.info("done.");
	}
	
	protected void createU(LinkedList[] predicates) {
		//why the predicates loop?
		for(int k = 0; k < predicates.length; k++) {
			for(int i = 0; i < tagAlphabet.size(); i++) {
				for(int j = 0; j < tagAlphabet.size(); j++) {
					createFeatureVector(predicates[k],
							(String)tagAlphabet.lookupObject(i),
							(String)tagAlphabet.lookupObject(j),
							new SLFeatureVector(-1,-1.0,null));			
				}
			}
		}
		for(int k = 0; k < predicates.length; k++) {
			for(int j = 0; j < tagAlphabet.size(); j++) {
				createFeatureVector(predicates[k],
						(String)tagAlphabet.lookupObject(j),
						new SLFeatureVector(-1,-1.0,null));
				
			}
		}		
	}
	
	/**Writes the predicates and tags in the conll format 
	 * for easy comparison with a Mallet CRF.
	 * 
	 * @throws IOException
	 */
	public void createCRF(LinkedList[] predicates, String[] tags,
			BufferedWriter out) throws IOException {
		
		for(int k = 1; k < predicates.length; k++) {
			LinkedList preds = predicates[k];
			String res = "";
			for(int i = 0; i < preds.size(); i++)
				res += (String)preds.get(i) + " ";
			res += tags[k];
			out.write(res.trim() + "\n");
		}
		out.write("\n");
	}
	
	/** Creates tag alphabet.
	 * 
	 * @throws IOException
	 */
	private void createTagAlphabet(String file) throws IOException {
		
		BufferedReader in = 
			new BufferedReader(new InputStreamReader(new FileInputStream(file),"8859_2"));
		String line = in.readLine();
		String pos_line = in.readLine();
		String tags_line = in.readLine();
		
		while(line != null) {
			
			String[] tags = tags_line.split("\t");
			
			tagAlphabet.lookupIndex("O");
			for(int i = 0; i < tags.length; i++) {
				tagAlphabet.lookupIndex(tags[i]);
			}
			
			line = in.readLine();
			pos_line = in.readLine();
			tags_line = in.readLine();
		}
		
	}
	
	public SLFeatureVector createFeatureVector(LinkedList predicates,
			String prev, String next,
			SLFeatureVector fv) {
		
		String s1 = prev+"_"+next;
		fv = fv.add("TRANS="+s1,1.0,dataAlphabet);
		//for(int j = 0; j < predicates.size(); j++) {
		//    String pred = (String)predicates.get(j);
		//    fv = fv.add(s1+"_"+pred,1.0,dataAlphabet);
		//}
		
		return fv;
	}
	
	public SLFeatureVector createFeatureVector(LinkedList predicates,
			String next,
			SLFeatureVector fv) {		
		String s2 = next;
		for(int j = 0; j < predicates.size(); j++) {
			String pred = (String)predicates.get(j);
			fv = fv.add(s2+"_"+pred,1.0,dataAlphabet);
		}		
		return fv;
	}
	
	public SLFeatureVector createFeatureVector(LinkedList[] predicates,
			String[] tags,
			SLFeatureVector fv) {
		
		for(int i = 1; i < predicates.length; i++) {
			fv = createFeatureVector(predicates[i],(i == 0 ? "O" : tags[i-1]),tags[i],fv);
			fv = createFeatureVector(predicates[i],tags[i],fv);
		}
		
		return fv;
	}
	
	public abstract LinkedList[] getPredicates(String[] toks, String[] pos);
	
	/*
	 *  (non-Javadoc)
	 * @see struct.types.DataManager#closeAlphabets()
	 */
	public void closeAlphabets() {
		dataAlphabet.stopGrowth();
		tagAlphabet.stopGrowth();
		SequenceInstance.setTagAlphabet(tagAlphabet);
		SequenceInstance.setDataAlphabet(dataAlphabet);
	}
	
	public void createForest(SequenceInstance inst, LinkedList[] predicates, ObjectOutputStream out) {
		String[] toks = inst.getInput().sentence;
		
		try {
			
			for(int k = 0; k < predicates.length; k++) {
				for(int i = 0; i < tagAlphabet.size(); i++) {
					for(int j = 0; j < tagAlphabet.size(); j++) {
						SLFeatureVector nfv = 
							createFeatureVector(predicates[k],
									(String)tagAlphabet.lookupObject(i),
									(String)tagAlphabet.lookupObject(j),
									new SLFeatureVector(-1,-1.0,null));
						
						nfv.sort();
						
						for(SLFeatureVector curr = nfv; curr.next!= null; curr = curr.next)
							if(curr.index >= 0)
								out.writeInt(curr.index);
						out.writeInt(-1);
					}
				}
			}
			
			out.writeInt(-2);
			
			for(int k = 0; k < predicates.length; k++) {
				for(int j = 0; j < tagAlphabet.size(); j++) {
					SLFeatureVector nfv = 
						createFeatureVector(predicates[k],
								(String)tagAlphabet.lookupObject(j),
								new SLFeatureVector(-1,-1.0,null));
					
					nfv.sort();
					
					for(SLFeatureVector curr = nfv; curr.next!= null; curr = curr.next)
						if(curr.index >= 0)
							out.writeInt(curr.index);
					out.writeInt(-1);
				}
			}
			
			out.writeInt(-3);
			
			
		} catch (IOException e) {}
	}
	
	public String normalize(String s) {
		if(s.matches("[0-9]+|[0-9]+\\.[0-9]+|[0-9]+[0-9,]+"))
			return "<num>";
		
		return s;
	}	
}
