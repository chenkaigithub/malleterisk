package main.insticc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import pp.PreProcessor;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import data.db.DbConnector;
import data.db.DbDataAccess;
import ft.selection.methods.FilterByRankedIG;
import ft.weighting.methods.FeatureWeighting;

public class Emails2Classifier {
	public static void main(String[] args) throws SQLException {
		// data set
		System.out.println("--------------- load data from database");
		DbDataAccess dal = new DbDataAccess(new DbConnector("jdbc:postgresql://localhost/seamce", "postgres", "postgresql"));
		INSTICCDataSet ds = new INSTICCDataSet(dal);
		
		System.out.println("--------------- preprocess");
		PreProcessor preprocessor = new BodyPreProcessor();
		while(ds.hasNext()) preprocessor.addThruPipe(ds.next());

		// transform and apply filter selection
		System.out.println("--------------- feature weight & select");
		InstanceList instances = transformAndFilter(preprocessor);
		
		instances.save(new File(INSTANCELIST_PATH));
		
    	// train
		System.out.println("--------------- train classifier");
		Classifier classifier = new NaiveBayesTrainer().train(instances);
		
    	// serialize classifier
		System.out.println("--------------- serialize classifier");
		ObjectOutputStream ois = null;
		try {
			ois = new ObjectOutputStream (new FileOutputStream (CLASSIFIER_PATH));
			ois.writeObject(classifier);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(ois!=null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

    private static final String CLASSIFIER_PATH = "classifier.cls";
    private static final String INSTANCELIST_PATH = "instances.mallet";

    public static InstanceList transformAndFilter(InstanceList instances) {
    	// transform
    	instances = new FeatureWeighting(FeatureWeighting.TF_BOOLEAN, FeatureWeighting.IDF_IDF, FeatureWeighting.NORMALIZATION_COSINE).calculate(instances);
    	
    	// apply feature selection
    	instances = new FilterByRankedIG().filter((int) (instances.getDataAlphabet().size()*0.1), instances);
    	
    	return instances;
    }
}
