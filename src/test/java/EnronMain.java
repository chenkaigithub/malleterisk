import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import pp.BodyPreProcessor;
import pp.IPreProcessor;
import pp.SubjectPreProcessor;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import data.IDataSet;
import data.enron.EnronDbDataSet;
import data.enron.db.EnronDbConnector;
import data.enron.db.EnronDbDataAccess;
import fs.FeatureSelectionPipe;
import fs.IFeatureSelector;
import fs.Pruner;
import fs.TFIDF;

public class EnronMain {
	public static void main(String[] args) throws SQLException {
		// data set
		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
		
		Collection<Integer> collections = dal.getCollections();
		
		for (Integer collectionId : collections) {
			Collection<Integer> users = dal.getUsers(collectionId);
			for (Integer userId : users) {
				IDataSet ds = new EnronDbDataSet(dal, collectionId, userId);
				
				// pre processing
				IPreProcessor subjectPreProcessor = new SubjectPreProcessor();
				IPreProcessor bodyPreProcessor = new BodyPreProcessor();
//				IPreProcessor datePreProcessor = new DatePreProcessor();
//				IPreProcessor participantsPreProcessor = new ParticipantsPreProcessor();		
				
				InstanceList ilSubject = subjectPreProcessor.getInstanceList();  
				InstanceList ilBody = bodyPreProcessor.getInstanceList();
//				InstanceList ilDate = datePreProcessor.getInstanceList();
//				InstanceList ilParticipants = participantsPreProcessor.getInstanceList();
				
				for (Instance msgInstance : ds) {
					System.out.println(msgInstance.getName());
					ilSubject.addThruPipe(msgInstance);
					ilBody.addThruPipe(msgInstance);
//					ilDate.addThruPipe(msgInstance);
//					ilParticipants.addThruPipe(msgInstance);
				}
				
				// feature selection
				LinkedList<IFeatureSelector> featureSelectors = new LinkedList<IFeatureSelector>();
				featureSelectors.add(new Pruner(5, 1000));
				featureSelectors.add(new TFIDF());
				FeatureSelectionPipe fsPipe = new FeatureSelectionPipe(featureSelectors);
						
//				ilSubject = fsPipe.runThruPipe(ilSubject);
				ilBody = fsPipe.runThruPipe(ilBody);
//				ilParticipants = 
//				ilDate = 
				
				// magia! é necessário classificar e colar os resultados entre
				// as várias partes das mensagens - cada mensagem é composta por
				// uma instância de cada instance list. as instâncias possuem
				// um campo que indica a que documento pertencem
				
				// como ter a certeza que o split sobre o subject
				// é idêntico ao split no body, etc? i.e. garantir que os "pedaços" das
				// mensagens são separados de forma idêntica pelas instance list
				
				InstanceList[] subjectSplit = ilSubject.split(new double[] {0.8, 0.2 });
				ClassifierTrainer<?> nbSubjectTrainer = new NaiveBayesTrainer();
				Classifier nbSubjectClassifier = nbSubjectTrainer.train(subjectSplit[0]);
				System.out.println("subject training accuracy is " + nbSubjectClassifier.getAccuracy(subjectSplit[0]));
				System.out.println("subject testing accuracy is " + nbSubjectClassifier.getAccuracy(subjectSplit[1]));
				
				InstanceList[] bodySplit = ilBody.split(new double[] { 0.8, 0.2 });
				ClassifierTrainer<?> nbBodyTrainer = new NaiveBayesTrainer();
				Classifier nbBodyClassifier = nbBodyTrainer.train(bodySplit[0]);
				System.out.println("body training accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[0]));
				System.out.println("body testing accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[1]));
			}
			break;
		}
		
		// ------------------------------------------------------------------
		// ------------------------------------------------------------------
		// ------------------------------------------------------------------
		
//		val sums = FeatureSelector.sum_feature_vectors(instances).asInstanceOf[Array[Object]]
//		println("alphabet size: " + alphabet.size)		
//		OutputUtils.to_csv("xxx", sums)
		
//		val vals = alphabet.toArray(Array[Object]()).sortBy {o => o.toString }
//		vals.foreach(v => {
//			val idx = alphabet.lookupIndex(v)
//			println(v + "\t" + sums(idx))
//		})
	}
	
	public static final void classifyUser(int userId) {
		
	}
	
	
}
