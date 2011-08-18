package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class JavaMailUtils {
	public static final String FROM = "From";
	public static final String TO = "To";
	public static final String CC = "Cc";
	public static final String BCC = "Bcc"; 
	public static final String X_FROM = "X-From";
	public static final String X_TO = "X-To";
	public static final String X_CC = "X-cc";
	public static final String X_BCC = "X-bcc"; 

	public static final Collection<String> parseAddresses(String header, MimeMessage msg) throws MessagingException {
		LinkedList<String> addresses = new LinkedList<String>();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> e = msg.getMatchingHeaderLines(new String[] { header });
		while(e.hasMoreElements()) {
			String s = e.nextElement().substring(header.length()+1); // "To/Cc/Bcc" + ":"
			String[] addrs = s.split(",");
			
			for (String address : addrs)
				addresses.add(address.trim());
		}
		
		return addresses;
	}
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static final String parseDateTime(Date dt) {
		if(dt!=null) return sdf.format(dt);
		else return "";
	}
	
	public static final Date parseDateTime(String dt) throws ParseException {
		return sdf.parse(dt);
	}
}
