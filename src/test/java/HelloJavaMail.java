/*
object HelloJavaMail {
	import scala.io._
	import java.io._
	import javax.mail._
	import javax.mail.internet._
	
	def process_mail_folder(o : File) : Unit = {
		if(o.isDirectory) o.listFiles.foreach(entry =>process_mail_folder(entry))
		else process_mail_file(o)
	}
	
	def process_mail_file(file : File) = {
		if(!file.isHidden) print_mail(new MimeMessage(null, new FileInputStream(file)))
	}

	def print_address(addr : Address) = {
		println(addr.asInstanceOf[InternetAddress].getAddress)
	}
	
	def print_mail(msg : MimeMessage) = {
		println("-------------------------------------------------------------------------------------")
		println("From: ")
		val froms = msg.getFrom()
		if(froms!=null) froms.foreach(print_address)
		println("To: ")
		val tos = msg.getRecipients(Message.RecipientType.TO)
		if(tos!=null) tos.foreach(print_address)
		println("CC: ")
		val ccs = msg.getRecipients(Message.RecipientType.CC)
		if(ccs!=null) ccs.foreach(print_address)
		println("BCC: ")
		val bccs = msg.getRecipients(Message.RecipientType.BCC)
		if(bccs!=null) bccs.foreach(print_address)
		println("Received On: ")
		println(msg.getReceivedDate)
		println("Sent On: ")
		println(msg.getSentDate)
		println("Subject: ")
		println(msg.getSubject)
		println("Content-Type: ")
		println(msg.getContentType)
		println("Content: ")
		println(msg.getContent)
	}
	
	
	
	def main(args : Array[String]) = {
		val accounts = new File("../data/enron_flat").listFiles
		accounts.foreach(process_mail_folder)
	}
}
*/