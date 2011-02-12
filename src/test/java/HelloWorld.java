/*
class Person(val name : String, val female : Boolean)

object Greeting {
	def main(args : Array[String]) = {
		println("Hello World")
		
		val people = List(
				new Person("J. R. Ewing", false),
				new Person("Sue Ellen Ewing", true),
				new Person("Ellie Ewing", true),
				new Person("Bobby Ewing", false),
				new Person("Donna Culver Krebbs", true)
		)

		// conditions in the for loop are 'and'
		for(person <- people;
		if !person.female || person.name == "Ellie Ewing")
			println(person.name)

			var capital = Map("US" -> "Washington DC", "France" -> "Paris")
			capital += ("Japan" -> "Tokyo", "Portugal" -> "Lisbon")
			println(capital("France"))
		
			var i=0
			while(i < args.length) {
				println(args(i))
				i+=1
			}
		
		imperativePrintArgs(args)
		println("--")
		functionalPrintArgs(args)
	}
	
	def imperativePrintArgs(args : Array[String]) : Unit = {
		var i = 0
		while(i < args.length) {
			println(args(i))
			i += 1
		}
	}

	def functionalPrintArgs(args : Array[String]) : Unit = {
		args.foreach(arg => println(arg))
	}
}
*/