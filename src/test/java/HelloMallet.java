/*
import cc.mallet.util.Randoms
import cc.mallet.pipe._
import cc.mallet.types._
import cc.mallet.pipe.iterator.FileIterator
import cc.mallet.types.InstanceList
import cc.mallet.pipe.iterator._
import cc.mallet.classify._
import java.io._
import java.util.Iterator
import java.util.regex.Pattern
import java.util.ArrayList
import java.io.FileFilter
import java.io.File
	
class TxtFilter extends FileFilter {
    def accept(file) : Boolean = {
        return file.toString().endsWith(".txt");
    }
}

class ImportExample {
	var pipe : Pipe = buildPipe()
	
	def buildPipe() : Pipe = {
		var pipeList = new ArrayList[Pipe]()
		pipeList.add(new Input2CharSequence("UTF-8"))
		var tokenPattern = Pattern.compile("[\\p{L}\\p{N}_]+")
		pipeList.add(new CharSequence2TokenSequence(tokenPattern))             
		pipeList.add(new TokenSequenceLowercase())
		pipeList.add(new TokenSequenceRemoveStopwords(false, false))
		pipeList.add(new TokenSequence2FeatureSequence())          
		pipeList.add(new Target2Label())
		pipeList.add(new FeatureSequence2FeatureVector())  
		pipeList.add(new PrintInputAndTarget())	
		return new SerialPipes(pipeList)
	}

	def readDirectory(directory : File) : InstanceList = {
		return readDirectories(Array[File] {directory})
	}

	def readDirectories(directories : Array[File]) : InstanceList = {
		var iterator = new FileIterator(directories, new TxtFilter(), FileIterator.STARTING_DIRECTORIES)
		var instances = new InstanceList(pipe)
//		instances.addThruPipe(iterator)
		while(iterator.hasNext) {
			val instance = iterator.next
			instances.addThruPipe(instance)
		}
		
		return instances;
	}
}

object HelloMallet {

	def main(args : Array[String]) = {
		var importer = new ImportExample()
		var instances = importer.readDirectory(new File("../tests/mallet/sample-data/web/"))
								.split(new Randoms(), Array(0.8, 0.2, 0.0))
		
//		Labeling labeling =	classifier.classify(instance);					
//		Label l = labeling.getBestLabel();
//		System.out.print(instance + “\t”);
//		System.out.println(l);
//		ClassifierTrainer trainer = new MaxEntTrainer();
//		Classifier classifier = trainer.train(instances);

		val trainer = new MaxEntTrainer()
		val cl = trainer.train(instances(0))
		val trial = new Trial(cl, instances(1))
		println(trial.getF1(0))
		println(trial.getPrecision(0))
		println(trial.getRecall(0))
		println(trial.getAccuracy)
		println(trial.getAverageRank)
	}
}
*/