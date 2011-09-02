package data.analysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import types.email.IEmailMessage;
import types.email.IEmailParticipant;
import types.mallet.LabeledInstancesList;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.SparseVector;

public class DataAnalysis {
	
	// features
	
	public static int numTerms(InstanceList il) {
		return il.getDataAlphabet().size();
	}
	
	public static SparseVector termOccurrences(InstanceList il) {
		SparseVector sv = new SparseVector(new double[il.getDataAlphabet().size()]);

		for (Instance i : il)
			sv.plusEqualsSparse((FeatureVector) i.getData());
		
		return sv;
	}	
	
	public static double averageTermsPerDocument(InstanceList il) {
		double i = 0;
		
		for (Instance instance : il)
			i +=  ((FeatureVector)instance.getData()).getIndices().length;
		
		return i / (double)il.size();
	}
	
	public static int minNumTermsInDocuments(InstanceList il) {
		int m = -1;
		
		for (Instance instance : il) {
			int i = ((FeatureVector) instance.getData()).getIndices().length;
			if(m == -1 || m > i) m = i;
		}
		
		return m;
	}
	
	public static int maxNumTermsInDocument(InstanceList il) {
		int m = 0;
		
		for (Instance instance : il) {
			int i = ((FeatureVector) instance.getData()).getIndices().length;
			if(i > m) m = i;
		}
		
		return m;
	}
	
	public static Map<Object, Double> averageTermsPerClass(LabeledInstancesList lil) {
		Map<Object, Double> avg = new HashMap<Object, Double>();
		
		// average length of messages per class
		for(int i=0; i<lil.getNumLabels(); ++i)
			avg.put(lil.getLabel(i), averageTermsPerDocument(lil.getLabelInstances(i)));
		
		return avg;
	}

	// documents / classes
	
	public static int numClasses(InstanceList il) {
		return il.getTargetAlphabet().size();
	}
	
	public static int numDocuments(InstanceList il) {
		return il.size();
	}

	public static double averageDocumentsPerClass(InstanceList il) {
		return (double)il.size()/(double)il.getTargetAlphabet().size();
	}
	
	public static int minNumDocumentsInClass(LabeledInstancesList lil) {
		int c = -1;

		for (InstanceList instances : lil.getInstanceLists()) {
			int i = instances.size();
			if(c==-1 || c > i) c = i;
		}

		return c;
	}
	
	public static int maxNumDocumentsInClass(LabeledInstancesList lil) {
		int c = 0;

		for (InstanceList instances : lil.getInstanceLists()) {
			int i = instances.size();
			if(i > c) c = i;
		}

		return c;
	}
	
	public static Map<Object, Integer> docClassDistribution(LabeledInstancesList lil) {
		Map<Object, Integer> hg = new HashMap<Object, Integer>();
		
		// number of documents per class
		for(int i=0; i<lil.getNumLabels(); ++i)
			hg.put(lil.getLabel(i), lil.getNumLabelInstances(i));
		
		return hg;
	}
	
	// participants
	
	public static Map<Object, Integer> totalNumParticipantsInClass(LabeledInstancesList lil) {
		// the instancelist must be of participants
		Map<Object, Integer> hg = new HashMap<Object, Integer>();
		
		// number of participants per class
		int numParticipants = 0;
		for(int i=0; i<lil.getNumLabels(); ++i) {
			InstanceList il = lil.getLabelInstances(i);
			for (Instance instance : il) {
				Object o = instance.getData();
				if(o instanceof IEmailMessage) {
					IEmailMessage m = (IEmailMessage) o;
					numParticipants += m.getParticipants().size();
				}
				else if(o instanceof FeatureVector) {
					FeatureVector fv = (FeatureVector) o;
					numParticipants += fv.numLocations();
				}
			}
			
			hg.put(lil.getLabel(i), numParticipants);
		}
		
		return hg;
	}
		
	public static void participantsClassesCorrelation(InstanceList pil) {
		// find a correlation between participants and labels
		// participants are treated as groups (unique set of participants)
		
		Map<Set<Integer>, Set<String>> groupsLabels = new HashMap<Set<Integer>, Set<String>>();
		
		// iterate all instances and build the map<groups, labels> that associates
		// the unique groups to a set of classes
		for (Instance inst : pil) {
			IEmailMessage m = (IEmailMessage) inst.getData();
			
			// get the group of the message
			Set<Integer> g = new HashSet<Integer>();
			for (IEmailParticipant p : m.getParticipants()) // I could/should implement equals/hashcode on
				g.add(p.getParticipantId());				// EmailParticipant to avoid doing this
			
			// create and add if not exists
			Set<String> l;
			if((l = groupsLabels.get(g)) == null) {
				l = new HashSet<String>();
				groupsLabels.put(g, l);
			}
			
			// associate this class to the group
			l.add(inst.getTarget().toString());
		}
		
		// print to system.out
		int i = 1;
		for (Entry<Set<Integer>, Set<String>> entry : groupsLabels.entrySet()) {
			System.out.print((i++) + ": [ ");
			for (Integer p : entry.getKey())
				System.out.print(p + " ");
			System.out.print("]");

			System.out.print("\t\t\t");
			
			System.out.print("[ ");
			for (String l : entry.getValue())
				System.out.print(l + " ");
			System.out.print("]");
			
			System.out.println();
		}
		System.out.println();
		System.out.println();
		System.out.println("#unique entries: " + groupsLabels.size()); // number of unique groups
	}
	
	// time
	
	public static Map<Object, TimeInterval> time(LabeledInstancesList lil) {
		// instance's data must be of date type
		
		// time period for classes
		Map<Object, TimeInterval> hg = new HashMap<Object, TimeInterval>();
		
		for(int i=0; i<lil.getNumLabels(); ++i) {
			InstanceList il = lil.getLabelInstances(i);
			
			Date earliest = null, latest = null;
			for (Instance instance : il) {
				Date d = (Date) instance.getData();
				
				if(earliest == null && latest == null) earliest = latest = d;
				
				else if(d.compareTo(latest) == 1) latest = d;
				else if(d.compareTo(earliest) == -1) earliest = d;
			}
			
			hg.put(lil.getLabel(i), new TimeInterval(earliest, latest));
		}
		
		return hg;
	}

	// utils
	
	public static void toCSV(String filename, Map<?, ?> data) throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(filename);
		PrintWriter pw = new PrintWriter(out);
		
		for (Entry<?, ?> entry : data.entrySet()) {
			pw.write(entry.getKey().toString());
			pw.write(", ");
			pw.write(entry.getValue().toString());
			pw.write('\n');
		}

		pw.flush();
		pw.close();
	}
}

class TimeInterval {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	
	public final Date startDate;
	public final Date endDate;
	
	public TimeInterval(Date sd, Date ed) {
		this.startDate = sd;
		this.endDate = ed;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(SDF.format(startDate));
		sb.append(", ");
		sb.append(SDF.format(endDate));
		
		return sb.toString();
	}
}
