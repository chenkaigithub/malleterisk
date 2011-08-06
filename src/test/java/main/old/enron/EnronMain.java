package main.old.enron;
import java.sql.SQLException;

public class EnronMain {
	public static void main(String[] args) throws SQLException {
//		// data set
//		EnronDbDataAccess dal = new EnronDbDataAccess(new EnronDbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
//		
//		Collection<Integer> collections = dal.getCollections();
//		
//		for (Integer collectionId : collections) {
//			Collection<Integer> users = dal.getUsers(collectionId);
//			for (Integer userId : users) {
//				IDataSet ds = new EnronDbDataSet(dal, collectionId, userId);
//				
//				// pre processing
//				InstanceList ilSubject = new SubjectPreProcessor1(ds);
//				InstanceList ilBody = new BodyPreProcessor1(ds);
////				InstanceList ilDate = new DatePreProcessor1(ds);
////				InstanceList ilParticipants = new ParticipantsPreProcessor1(ds);		
//				
//				// feature selection
//				LinkedList<IFeatureTransformer> featureSelectors = new LinkedList<IFeatureTransformer>();
//				featureSelectors.add(new PruneByTF(5, 1000));
//				featureSelectors.add(new TFIDF());
//				FeatureTransformationPipeline fsPipe = new FeatureTransformationPipeline(featureSelectors);
//						
////				ilSubject = fsPipe.runThruPipe(ilSubject);
//				ilBody = fsPipe.runThruPipeline(ilBody);
////				ilParticipants = 
////				ilDate = 
//				
//				// magia! é necessário classificar e colar os resultados entre
//				// as várias partes das mensagens - cada mensagem é composta por
//				// uma instância de cada instance list. as instâncias possuem
//				// um campo que indica a que documento pertencem
//				
//				// como ter a certeza que o split sobre o subject
//				// é idêntico ao split no body, etc? i.e. garantir que os "pedaços" das
//				// mensagens são separados de forma idêntica pelas instance list
//				
//				InstanceList[] subjectSplit = ilSubject.split(new double[] {0.8, 0.2 });
//				ClassifierTrainer<?> nbSubjectTrainer = new NaiveBayesTrainer();
//				Classifier nbSubjectClassifier = nbSubjectTrainer.train(subjectSplit[0]);
//				System.out.println("subject training accuracy is " + nbSubjectClassifier.getAccuracy(subjectSplit[0]));
//				System.out.println("subject testing accuracy is " + nbSubjectClassifier.getAccuracy(subjectSplit[1]));
//				
//				InstanceList[] bodySplit = ilBody.split(new double[] { 0.8, 0.2 });
//				ClassifierTrainer<?> nbBodyTrainer = new NaiveBayesTrainer();
//				Classifier nbBodyClassifier = nbBodyTrainer.train(bodySplit[0]);
//				System.out.println("body training accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[0]));
//				System.out.println("body testing accuracy is " + nbBodyClassifier.getAccuracy(bodySplit[1]));
//			}
//			break;
//		}
		
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
}
