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
				for (String from : JavaMailUtils.parseAddresses(JavaMailUtils.FROM, m))
					i++;
				for (String to : JavaMailUtils.parseAddresses(JavaMailUtils.TO, m)) 
					i++;
				for (String cc : JavaMailUtils.parseAddresses(JavaMailUtils.CC, m)) 
					i++;
				for (String bcc : JavaMailUtils.parseAddresses(JavaMailUtils.BCC, m)) 
					i++;
				for (String from : JavaMailUtils.parseAddresses("X-From", m)) 
					i++;
				for (String to : JavaMailUtils.parseAddresses("X-To", m)) 
					i++;
				for (String cc : JavaMailUtils.parseAddresses("X-cc", m)) 
					i++;
				for (String bcc : JavaMailUtils.parseAddresses("X-bcc", m)) 
					i++;
				
				if(i < 2) System.out.println(fileName);
			} 
			catch (MessagingException e1) { e1.printStackTrace(); } 
			catch (IOException e1) { e1.printStackTrace(); } 
			
		}
	}

}
