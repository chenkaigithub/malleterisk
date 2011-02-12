/*
// PostgreSQL Example using Scala DBC
import scala.dbc._
import scala.dbc.Syntax._
import syntax.Statement._
	
import scala.dbc.statement.InsertionData
import scala.dbc.statement.Insert
import scala.dbc.syntax.DataTypeUtil
import scala.dbc.value.ExactNumeric
import scala.dbc.statement.expression.Constant
import scala.dbc.Utilities.valueToConstant 

object PostgresVendor extends Vendor {
	val nativeDriverClass = Class.forName("org.postgresql.Driver")
	val uri = new java.net.URI("jdbc:postgresql://localhost/seat") 
	val user = "postgres" 
	val pass = "postgresql" 
	val retainedConnections = 1
	val urlProtocolString = "jdbc:postgresql:" 
}

object PostgresDBCTest {
	def main(args: Array[String]): Unit = {
		val db = new Database(PostgresVendor)
		
		val i1 : Constant = new ExactNumeric[Int] {
			val dataType = DataTypeUtil.integer
			val nativeValue = 0
		}
		val i2 : Constant = new ExactNumeric[Int] {
			val dataType = DataTypeUtil.integer
			val nativeValue = 142412
		}
		val i3 : Constant = new ExactNumeric[Int] {
			val dataType = DataTypeUtil.integer
			val nativeValue = 141241
		}
		
		val res0 = db.executeStatement {
			new Insert("test", InsertionData.Constructor(None,List(i1, i2, i3)));
		}
		
		val rows = db.executeStatement {
			(select fields (("c1" of integer)
					and ("c2" of integer)
					and ("c3" of integer))
			from "test")
		}

		for(i <- rows) {
			for(f <- i.fields) {
				val r = f.content.sqlString
				print(r + "      ".dropRight(r.length))
			}
			println()
		}
	}
}
*/