package experiments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

public class RAKEExperiment {
	public static void main(String[] args) {
		String text = "Compatibility of systems of linear constraints over the set of natural numbers\nCriteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for constructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.";
		Collection<String> stoplist = loadStopList();
		Collection<String> phraseDelimiters = loadPhraseDelimiters();
		Collection<String> wordDelimiters = loadWordDelimiters();
		
		Collection<String> tokens = toTokens(text, wordDelimiters);
//		System.out.println("TOKENS: "); for (String t : tokens) System.out.println("- " + t);

		Collection<CandidateKeyword> candidateKeywords = toCandidateKeywords(tokens, stoplist, phraseDelimiters);
//		System.out.println("CANDIDATE KEYWORDS: "); for (CandidateKeyword ck : candidateKeywords) System.out.println("- " + ck);
				
		CoOccurrenceMatrix com = new CoOccurrenceMatrix(candidateKeywords);
		Map<String, Double> scores = degFreq(com);
//		for (Entry<String, Double> entry : scores.entrySet())
//			System.out.println(entry.getKey() + ": " + entry.getValue());
		
		Map<CandidateKeyword, Double> ckScores = candidateScores(candidateKeywords, scores);
//		System.out.println("SCORES: "); for (Entry<CandidateKeyword, Double> entry : ckScores.entrySet()) System.out.println(entry.getKey() + ": " + entry.getValue());
		
		Collection<CandidateKeyword> keywords = getTopCandidateKeywords(ckScores, 9);
		System.out.println("KEYWORDS: "); for (CandidateKeyword k : keywords) System.out.println(k);
	}
	
	private static Collection<String> loadPhraseDelimiters() {
		LinkedList<String> delimiters = new LinkedList<String>();
		delimiters.add(".");
		delimiters.add("?");
		delimiters.add("!");
		delimiters.add("\n");
		delimiters.add("\r");
		delimiters.add("\n\r");
		delimiters.add("\r\n");
		delimiters.add(",");
		
		return delimiters;
	}

	private static Collection<String> loadStopList() {
		ArrayList<String> stoplist = new ArrayList<String>();
		for (String s : stopwords)
			stoplist.add(s);
		
		return stoplist;
	}
	
	public static final String[] stopwords =
	{
		"a",
		"able",
		"about",
		"above",
		"according",
		"accordingly",
		"across",
		"actually",
		"after",
		"afterwards",
		"again",
		"against",
		"all",
		"allow",
		"allows",
		"almost",
		"alone",
		"along",
		"already",
		"also",
		"although",
		"always",
		"am",
		"among",
		"amongst",
		"an",
		"and",
		"another",
		"any",
		"anybody",
		"anyhow",
		"anyone",
		"anything",
		"anyway",
		"anyways",
		"anywhere",
		"apart",
		"appear",
		"appreciate",
		"appropriate",
		"are",
		"around",
		"as",
		"aside",
		"ask",
		"asking",
		"associated",
		"at",
		"available",
		"away",
		"awfully",
		"b",
		"be",
		"became",
		"because",
		"become",
		"becomes",
		"becoming",
		"been",
		"before",
		"beforehand",
		"behind",
		"being",
		"believe",
		"below",
		"beside",
		"besides",
		"best",
		"better",
		"between",
		"beyond",
		"both",
		"brief",
		"but",
		"by",
		"c",
		"came",
		"can",
		"cannot",
		"cant",
		"cause",
		"causes",
		"certain",
		"certainly",
		"changes",
		"clearly",
		"co",
		"com",
		"come",
		"comes",
		"concerning",
		"consequently",
		"consider",
		"considering",
		"contain",
		"containing",
		"contains",
		"corresponding",
		"could",
		"course",
		"currently",
		"d",
		"definitely",
		"described",
		"despite",
		"did",
		"different",
		"do",
		"does",
		"doing",
		"done",
		"down",
		"downwards",
		"during",
		"e",
		"each",
		"edu",
		"eg",
		"eight",
		"either",
		"else",
		"elsewhere",
		"enough",
		"entirely",
		"especially",
		"et",
		"etc",
		"even",
		"ever",
		"every",
		"everybody",
		"everyone",
		"everything",
		"everywhere",
		"ex",
		"exactly",
		"example",
		"except",
		"f",
		"far",
		"few",
		"fifth",
		"first",
		"five",
		"followed",
		"following",
		"follows",
		"for",
		"former",
		"formerly",
		"forth",
		"four",
		"from",
		"further",
		"furthermore",
		"g",
		"get",
		"gets",
		"getting",
		"given",
		"gives",
		"go",
		"goes",
		"going",
		"gone",
		"got",
		"gotten",
		"greetings",
		"h",
		"had",
		"happens",
		"hardly",
		"has",
		"have",
		"having",
		"he",
		"hello",
		"help",
		"hence",
		"her",
		"here",
		"hereafter",
		"hereby",
		"herein",
		"hereupon",
		"hers",
		"herself",
		"hi",
		"him",
		"himself",
		"his",
		"hither",
		"hopefully",
		"how",
		"howbeit",
		"however",
		"i",
		"ie",
		"if",
		"ignored",
		"immediate",
		"in",
		"inasmuch",
		"inc",
		"indeed",
		"indicate",
		"indicated",
		"indicates",
		"inner",
		"insofar",
		"instead",
		"into",
		"inward",
		"is",
		"it",
		"its",
		"itself",
		"j",
		"just",
		"k",
		"keep",
		"keeps",
		"kept",
		"know",
		"knows",
		"known",
		"l",
		"last",
		"lately",
		"later",
		"latter",
		"latterly",
		"least",
		"less",
		"lest",
		"let",
		"like",
		"liked",
		"likely",
		"little",
		"look",
		"looking",
		"looks",
		"ltd",
		"m",
		"mainly",
		"many",
		"may",
		"maybe",
		"me",
		"mean",
		"meanwhile",
		"merely",
		"might",
		"more",
		"moreover",
		"most",
		"mostly",
		"much",
		"must",
		"my",
		"myself",
		"n",
		"name",
		"namely",
		"nd",
		"near",
		"nearly",
		"necessary",
		"need",
		"needs",
		"neither",
		"never",
		"nevertheless",
		"new",
		"next",
		"nine",
		"no",
		"nobody",
		"non",
		"none",
		"noone",
		"nor",
		"normally",
		"not",
		"nothing",
		"novel",
		"now",
		"nowhere",
		"o",
		"obviously",
		"of",
		"off",
		"often",
		"oh",
		"ok",
		"okay",
		"old",
		"on",
		"once",
		"one",
		"ones",
		"only",
		"onto",
		"or",
		"other",
		"others",
		"otherwise",
		"ought",
		"our",
		"ours",
		"ourselves",
		"out",
		"outside",
		"over",
		"overall",
		"own",
		"p",
		"particular",
		"particularly",
		"per",
		"perhaps",
		"placed",
		"please",
		"plus",
		"possible",
		"presumably",
		"probably",
		"provides",
		"q",
		"que",
		"quite",
		"qv",
		"r",
		"rather",
		"rd",
		"re",
		"really",
		"reasonably",
		"regarding",
		"regardless",
		"regards",
		"relatively",
		"respectively",
		"right",
		"s",
		"said",
		"same",
		"saw",
		"say",
		"saying",
		"says",
		"second",
		"secondly",
		"see",
		"seeing",
		"seem",
		"seemed",
		"seeming",
		"seems",
		"seen",
		"self",
		"selves",
		"sensible",
		"sent",
		"serious",
		"seriously",
		"seven",
		"several",
		"shall",
		"she",
		"should",
		"since",
		"six",
		"so",
		"some",
		"somebody",
		"somehow",
		"someone",
		"something",
		"sometime",
		"sometimes",
		"somewhat",
		"somewhere",
		"soon",
		"sorry",
		"specified",
		"specify",
		"specifying",
		"still",
		"sub",
		"such",
		"sup",
		"sure",
		"t",
		"take",
		"taken",
		"tell",
		"tends",
		"th",
		"than",
		"thank",
		"thanks",
		"thanx",
		"that",
		"thats",
		"the",
		"their",
		"theirs",
		"them",
		"themselves",
		"then",
		"thence",
		"there",
		"thereafter",
		"thereby",
		"therefore",
		"therein",
		"theres",
		"thereupon",
		"these",
		"they",
		"think",
		"third",
		"this",
		"thorough",
		"thoroughly",
		"those",
		"though",
		"three",
		"through",
		"throughout",
		"thru",
		"thus",
		"to",
		"together",
		"too",
		"took",
		"toward",
		"towards",
		"tried",
		"tries",
		"truly",
		"try",
		"trying",
		"twice",
		"two",
		"u",
		"un",
		"under",
		"unfortunately",
		"unless",
		"unlikely",
		"until",
		"unto",
		"up",
		"upon",
		"us",
		"use",
		"used",
		"useful",
		"uses",
		"using",
		"usually",
		"uucp",
		"v",
		"value",
		"various",
		"very",
		"via",
		"viz",
		"vs",
		"w",
		"want",
		"wants",
		"was",
		"way",
		"we",
		"welcome",
		"well",
		"went",
		"were",
		"what",
		"whatever",
		"when",
		"whence",
		"whenever",
		"where",
		"whereafter",
		"whereas",
		"whereby",
		"wherein",
		"whereupon",
		"wherever",
		"whether",
		"which",
		"while",
		"whither",
		"who",
		"whoever",
		"whole",
		"whom",
		"whose",
		"why",
		"will",
		"willing",
		"wish",
		"with",
		"within",
		"without",
		"wonder",
		"would",
		"would",
		"x",
		"y",
		"yes",
		"yet",
		"you",
		"your",
		"yours",
		"yourself",
		"yourselves",
		"z",
		"zero"
	};	
	
	private static Collection<String> loadWordDelimiters() {
		LinkedList<String> delimiters = new LinkedList<String>();
		delimiters.add("?");
		delimiters.add("!");
		delimiters.add("\n");
		delimiters.add("\r");
		delimiters.add("\n\r");
		delimiters.add("\r\n");
		delimiters.add(",");
		delimiters.add(";");
		delimiters.add(".");
		delimiters.add(":");
		delimiters.add(" ");
		
		return delimiters;
	}
	
	
	private static Collection<String> toTokens(String text, Collection<String> wordDelimiters) {
		LinkedList<String> tokens = new LinkedList<String>();
		
		int h = 0;
		for (int i = 0; i < text.length(); i++) {
			if(wordDelimiters.contains(text.substring(i, i+1))) {
				// found a delimiter
				tokens.add(text.substring(h, i));
				tokens.add(text.substring(i, i+1));
				h = i+1; // delimiter + 1
			}
		}
		
		return tokens;
	}
	
	private static Collection<CandidateKeyword> toCandidateKeywords(Collection<String> tokens, Collection<String> stoplist, Collection<String> phraseDelimiters) {
		LinkedList<CandidateKeyword> cks = new LinkedList<CandidateKeyword>();
		
		CandidateKeyword ck = new CandidateKeyword();
		for (String t : tokens) {
			if(stoplist.contains(t.toLowerCase()) || phraseDelimiters.contains(t)) {
				if(ck.keywords.size() > 0) {
					cks.add(ck);
					ck = new CandidateKeyword();
				}
			}
			else {
				if(t.trim().length() > 0)
					ck.keywords.add(t);
			}
		}
		
		return cks;
	}
	
	public static Map<String, Double> degFreq(CoOccurrenceMatrix com) {
		Map<String, Double> scores = new HashMap<String, Double>();
		
		for(int i=0; i < com.matrix.length; i++) {
			double degree = 0, freq = 0;
			for (int j = 0; j < com.matrix.length; j++) {
				degree += com.matrix[i][j];
				freq = com.matrix[i][j] > freq ? com.matrix[i][j] : freq;
			}
			scores.put(com.entries.get(i), degree/freq);
		}
		
		return scores;
	}

	public static Map<CandidateKeyword, Double> candidateScores(Collection<CandidateKeyword> candidateKeywords, Map<String, Double> scores) {
		Map<CandidateKeyword, Double> cs = new HashMap<CandidateKeyword, Double>();
		
		for (CandidateKeyword ck : candidateKeywords) {
			double ckScore = 0;
			
			for (String k : ck.keywords)
				ckScore += scores.get(k.toLowerCase().trim());
			
			cs.put(ck, ckScore);
		}
		
		return cs;
	}

	@SuppressWarnings("unchecked")
	public static Collection<CandidateKeyword> getTopCandidateKeywords(Map<CandidateKeyword, Double> ckScores, int n) {
		if(n >= ckScores.size()) return ckScores.keySet();
		
		SortedSet<Entry<CandidateKeyword, Double>> ss = new TreeSet<Entry<CandidateKeyword,Double>>(new Comparator<Entry<CandidateKeyword, Double>>() {
			@Override
			public int compare(Entry<CandidateKeyword, Double> o1, Entry<CandidateKeyword, Double> o2) {
				Double d1 = o1.getValue(), d2 = o2.getValue();
				
				return d1 > d2 ? 1 : d1 == d2 ? 0 : -1;
			}
		});
		
		ss.addAll(ckScores.entrySet());
		
		ArrayList<CandidateKeyword> topKeywords = new ArrayList<CandidateKeyword>(n);
		int i = ss.size() - n;
		int j = 0;
		for (Entry<CandidateKeyword, Double> entry : ss)
			if(j++ >= i) topKeywords.add(entry.getKey());
		
		return topKeywords;
	}
}



class CandidateKeyword {
	public final List<String> keywords;
	
	public CandidateKeyword() {
		this.keywords = new LinkedList<String>();
	}
	
	public boolean contains(String s1, String s2) {
		boolean containsS1, containsS2;
		containsS1 = containsS2 = false;
		
		for (String k : keywords) {
			if(k.equalsIgnoreCase(s1)) containsS1 = true;
			if(k.equalsIgnoreCase(s2)) containsS2 = true;
			
			if(containsS1 && containsS2) return true;
		}
		
		return false;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for (String s : keywords) {
			sb.append(s);
			sb.append(" ");
		}
		
		return sb.toString();
	}
}



class CoOccurrenceMatrix {
	public final List<String> entries;
	public final int[][] matrix;
	
	public CoOccurrenceMatrix(Collection<CandidateKeyword> cks) {
		entries = new ArrayList<String>();

		for (CandidateKeyword ck : cks)
			for (String k : ck.keywords) {
				String s = k.trim().toLowerCase();
				if(!entries.contains(s))
					entries.add(s);
			}

		int n = entries.size();
		matrix = new int[n][n];
		
		// this can be optimized, matrix is symmetric
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				matrix[i][j] = coOccurrences(cks, entries.get(i), entries.get(j));
	}

	private int coOccurrences(Collection<CandidateKeyword> cks, String s1, String s2) {
		int i = 0;
		for (CandidateKeyword ck : cks) {
			if(ck.contains(s1, s2)) {
				for (String k : ck.keywords)
					if(k.equalsIgnoreCase(s1))
						i++;
			}
		}
		
		return i;
	}
}
