package experiments;

import java.sql.SQLException;
import java.util.Collection;

import rake.RAKE;
import types.email.IEmailMessage;
import cc.mallet.types.Instance;
import data.loader.DbDataSetLoader;
import data.loader.db.DbConnector;
import data.loader.db.DbDataAccess;


public class RAKEExperiment {
	public static void main(String[] args) throws SQLException {
//		String text = "Compatibility of systems of linear constraints over the set of natural numbers\nCriteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for constructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.";
////		String text = "\"Dear Bruno Encarnação:\n\nFirst of all, thank you so much for the acceptance of the submitted paper. \nRight now I'm preparing the documentation for sending to the National Agency of Quality Evaluation (known as ANECA in Spain), and I'd be very pleased if you could send me via e-mail some kind of certificate indicating the acceptance of the paper for the Conference, because I have to send it in the following two days and it can be decisive for the positive evaluation (if it were negative I'd be fired because my contract ends in February).\n\nThanks in advance for your attention.Best regards,\n Daniel García\n\nDaniel García Fernández-Pacheco\nDepartamento de Expresión Gráfica\nUniversidad Politécnica de Cartagena\nCampus Muralla del Mar, s/n ,30202 Cartagena (Murcia)\nTlf:  968 32 64 84  - Fax:  968 32 64 74\nE-mail:  daniel.garcia@upct.es\n";
//		
//		for(String s : RAKE.rawe(text, 10, RAKE.getStopList(), RAKE.getPhraseDelimiters(), RAKE.getWordDelimiters()))
//			System.out.println(s);
//		System.out.println("----------------------------------------------------------------------------------------");
//		for(String s : RAKE.rake(text, 10, RAKE.getStopList(), RAKE.getPhraseDelimiters(), RAKE.getWordDelimiters()))
//			System.out.println(s);

		int n = 10;
		Collection<String> stoplist = RAKE.getStopList();
		Collection<String> phraseDelimiters = RAKE.getPhraseDelimiters();
		Collection<String> wordDelimiters = RAKE.getWordDelimiters();
		
		DbDataSetLoader loader = new DbDataSetLoader(new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/malleterisk", "postgres", "postgresql")), 1, 1);
		for (Instance instance : loader) {
			IEmailMessage msg = (IEmailMessage) instance.getData();
			System.out.println("--- " + msg.getSubject());
			for (String k : RAKE.rake(msg.getBody(), n, stoplist, phraseDelimiters, wordDelimiters))
				System.out.println(k);
			System.out.println("-----");
		}
	}
}
