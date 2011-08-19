package experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import utils.JavaMailUtils;
import data.source.enron.EnronConstants;

public class AddressParserExperiment {
	public static void main(String[] args) {
		processEnron(EnronConstants.ENRON_FLAT_PATH);
	}
	
	public static void processEnron(String path) {
		for (File f : new File(path).listFiles()) {
			if(f.isDirectory() || !f.getName().equalsIgnoreCase(".DS_Store")) {
				processEntry(f);
			}
		}
	}
	
	private static void processEntry(File e) {
		if(e.isDirectory()) for (File f : e.listFiles()) processEntry(f);
		else processFile(e);
	}

	@SuppressWarnings("unused")
	private static void processFile(File e) {
		if(!e.getName().equalsIgnoreCase(".DS_Store")) {
			String fileName = e.getAbsolutePath();
			
			try {  
				MimeMessage m = new MimeMessage(null, new FileInputStream(fileName));
				int i = 0;
				for (String s : JavaMailUtils.parseParticipants(JavaMailUtils.FROM, m))
					i++;// System.out.println(s);
				for (String s : JavaMailUtils.parseParticipants(JavaMailUtils.TO, m)) 
					i++;// System.out.println(s);
				for (String s : JavaMailUtils.parseParticipants(JavaMailUtils.CC, m)) 
					i++;// System.out.println(s);
				for (String s : JavaMailUtils.parseParticipants(JavaMailUtils.BCC, m)) 
					i++;// System.out.println(s);
				for (String s : JavaMailUtils.parseParticipants("X-From", m)) 
					i++;// System.out.println(s);
				for (String s : JavaMailUtils.parseParticipants("X-To", m)) 
					i++;// System.out.println(s);
				for (String s : JavaMailUtils.parseParticipants("X-cc", m)) 
					i++;// System.out.println(s);
				for (String s : JavaMailUtils.parseParticipants("X-bcc", m)) 
					i++;// System.out.println(s);
				
				if(i < 2) System.out.println(fileName);
			} 
			catch (MessagingException e1) { e1.printStackTrace(); } 
			catch (IOException e1) { e1.printStackTrace(); } 
		}
	}
}
