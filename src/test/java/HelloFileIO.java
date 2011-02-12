/*

object HelloFileIO {
	import java.io._
	import scala.io.Source
	
	def processMailFolder(o : File) : Unit = {
		if(o.isDirectory) {
			println("   \\"+o.getName)
			o.listFiles.foreach(entry =>processMailFolder(entry))
		}
		else {
			processMailFile(o)
		}
	}
	
	def processMailFile(file : File) = {
		if(!file.isHidden) {
			println("      " + file.getName)
			println(Source.fromFile(file).mkString)
		}
	}
	
	def main(args : Array[String]) = {
		val accounts = new File("../data/enron_flat").listFiles
		accounts.foreach(processMailFolder)
	}
}

*/