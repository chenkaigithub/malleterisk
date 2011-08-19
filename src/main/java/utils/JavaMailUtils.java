package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	// ATTN: this does not satisfy all possible email addresses
	public static final String regex = "([\\w\\-\\+\\']+(\\.[\\w\\-\\+\\']+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4})";
	public static final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	public static final Collection<String> parseParticipants(String header, MimeMessage msg) throws MessagingException {
		LinkedList<String> addresses = new LinkedList<String>();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> e = msg.getMatchingHeaderLines(new String[] { header });
		if(e.hasMoreElements()) {
			final String s = e.nextElement().substring(header.length()+1).trim(); // "To/Cc/Bcc/X-To/X-cc/X-bcc" + ":"
			
			// first, extract addresses using the regex
			Matcher m = pattern.matcher(s);
			while(m.find()) addresses.add(m.group());
			
			// in case no addresses were extracted, extract other strings (e.g. names) 
			if(addresses.size() == 0) {
				for(String str : s.split(",")) {
					str = str.trim();
					if(str.length() > 0) addresses.add(str);
				}
			}
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
