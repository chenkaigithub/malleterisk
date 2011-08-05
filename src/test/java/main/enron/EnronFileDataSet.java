package main.enron;

import java.util.Iterator;

import cc.mallet.types.Instance;
import data.loader.IDataSetLoader;

@Deprecated
// TODO: organize this
// or delete it, since it might be better to have everything centralized in the db
public class EnronFileDataSet implements IDataSetLoader, Iterator<Instance> {

	@Override
	public Iterator<Instance> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Instance next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
}
/*
import java.io._
import javax.mail._
import javax.mail.internet._
import scala.io._
import scala.collection.mutable._
import scala.dbc._
import scala.dbc.statement._
import scala.dbc.syntax._
import scala.dbc.value.ExactNumeric
import scala.dbc.value.CharacterVarying
import syntax.Statement._
import scala.dbc.statement.expression.Constant
import scala.dbc.Utilities.valueToConstant 
import seamc.utils._

//
// Iterator
//

// TODO: vers‹o ineficiente para um file iterator
// uma vers‹o mais eficiente dever‡ ser lazy
// e.g. atravŽs de continuations
// ou Streams? ser‡ poss’vel?
// http://stackoverflow.com/questions/1052476/can-someone-explain-scalas-yield
// http://stackoverflow.com/questions/2201882/implementing-yield-yield-return-using-scala-continuations/2215182
// http://blog.tmorris.net/understanding-monads-using-scala-part-1/
class EnronFileIterator(root : String) extends Iterator[cc.mallet.types.Instance] {
	val files = new MutableList[File]()
	process_folder(new File(root))
	val files_iterator = files.iterator
	
	def process_folder(entry : File) {
		if(entry.isDirectory) entry.listFiles.foreach(e => process_folder(e))
		else process_file(entry)
	}
	
	def process_file(file : File) {
		if(!file.isHidden) files += file
	}

	// Iterator
	
	def hasNext() : Boolean = {
		return files_iterator.hasNext
	}
	
	def next() : cc.mallet.types.Instance = {
		throw new UnsupportedOperationException
		
		val file = files_iterator.next
		// TODO: alterar MimeMessage para EmailMessage
		val data = new MimeMessage(null, new FileInputStream(file))
		val abs = file.getAbsolutePath()
		var end_index = abs.length - file.getName().length
		val target = abs.substring(0, end_index)
		val name = file.toURI
		return new cc.mallet.types.Instance(data, target, name, null)
	}
	
	def remove() = {
		throw new UnsupportedOperationException
	}
}
*/