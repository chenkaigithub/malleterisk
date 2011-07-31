package classifiers;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.SolverType;

public class LibLinear extends Classifier {
	private static final long serialVersionUID = 1L;
	private LibLinearTrainer trainer;
	
	public LibLinear(LibLinearTrainer t) {
		this.trainer = t;
	}
	
	@Override
	public Classification classify(Instance instance) {
		LabelAlphabet ta = (LabelAlphabet)instance.getTargetAlphabet();
		
		FeatureNode[] x = LibLinearTrainer.instanceToFeatureNode(instance, trainer.getBias());
		double[] values = new double[ta.size()];
		
		Model m = trainer.getModel();
		if(trainer.normalize()) {
			SolverType st = trainer.getSolverType();
            if (st != SolverType.L2R_LR && st != SolverType.L2R_LR_DUAL && st != SolverType.L1R_LR)
                throw new RuntimeException("probability estimation is currently only " + "supported for L2-regularized logistic regression");
            
            Linear.predictProbability(m, x, values);
		}
		else {
			int r = Linear.predict(m, x);
			values[r] = 1;
		}
		
		return new Classification(instance, this, new LabelVector(ta, values));
	}

}
