package classifiers;

import java.util.Arrays;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

/**
 * LibLinear wrapper for Mallet. Inspired by WEKA's wrapper.
 * Uses bwaldvogel's liblinear-java.
 * 
 * @author tt
 *
 */
public class LibLinearTrainer extends ClassifierTrainer<Classifier> {
	private LibLinear classifier;
	
	private Model model;
	
	private SolverType solverType = SolverType.L2R_L2LOSS_SVC_DUAL;
	private double eps = 0.01;
	private double cost = 1;
	private double bias = 1;
    
	private boolean normalize = false;
	private boolean estimateProbability = true;
	
    private int[] weightLabel = new int[0];
    private double[] weight = new double[0];
    
	public LibLinearTrainer() {
		
	}
	
	// setters
	
	public void setSolverType(SolverType st) {
		this.solverType = st;
	}
	
	public void setEps(double eps) {
		this.eps = eps;
	}
	
	public void setCost(double c) {
		this.cost = c;
	}
	
	public void setBias(double b) {
		this.bias = b;
	}
	
	public void normalize(boolean n) {
		this.normalize = n;
	}
	
	public void estimateProbability(boolean e) {
		this.estimateProbability = e;
	}
	
	public void setWeight(int[] labels, double[] values) {
		this.weightLabel = labels;
		this.weight = values;
	}
	
	// getters
	
    public Model getModel() {
		return model;
	}

	public SolverType getSolverType() {
		return solverType;
	}

	public double getBias() {
		return bias;
	}

	public boolean normalize() {
		return normalize;
	}

	public boolean estimateProbability() {
		return estimateProbability;
	}
	
	//
	// ClassifierTrainer
	//

	@Override
	public Classifier getClassifier() {
		return classifier;
	}

	@Override
	public Classifier train(InstanceList trainingSet) {
		if(normalize) normalizeInstances(trainingSet);
		
		int nInsts = trainingSet.size();
		
	    FeatureNode[][] vx = new FeatureNode[nInsts][];
	    int[] vy = new int[nInsts];
        int max_index = 0;

	    for (int d = 0; d < nInsts; d++) {
	        Instance inst = trainingSet.get(d);
	        
	        FeatureNode[] x = instanceToFeatureNode(inst, bias);
	        if (x.length > 0) max_index = Math.max(max_index, x[x.length - 1].index);
	        
	        vx[d] = x;
	        vy[d] = ((Label)inst.getTarget()).getIndex();
	    }
        
        // reset the PRNG for regression-stable results
        Linear.resetRandom();
        
		this.model = Linear.train(getProblem(vx, vy, max_index), getParameter());
		this.classifier = new LibLinear(this);
		
		return this.classifier;
	}

	private void normalizeInstances(InstanceList trainingSet) {
		int n = trainingSet.getDataAlphabet().size();
		double[] mins = new double[n];
		double[] maxs = new double[n];
		Arrays.fill(mins, Double.NaN);
		Arrays.fill(maxs, Double.NaN);
		
		for (Instance instance : trainingSet) {
			FeatureVector fv = (FeatureVector) instance.getData();
			for(int i : fv.getIndices()) {
				double v = fv.value(i);
				
				if(Double.isNaN(mins[i])) mins[i] = maxs[i] = v;
				else if(v < mins[i]) mins[i] = v;
				else if(v > maxs[i]) maxs[i] = v;
			}
		}
		
		for (Instance instance : trainingSet) {
			FeatureVector fv = (FeatureVector) instance.getData();
			for(int i : fv.getIndices())
				fv.setValue(i, (fv.value(i)-mins[i]) / (maxs[i]-mins[i]));
		}
	}

	// helpers
	
	private Parameter getParameter() {
       Parameter parameter = new Parameter(solverType, cost, eps);
   	
        if (weight.length > 0)
            parameter.setWeights(weight, weightLabel);
	
        return parameter;
	}
	
	private Problem getProblem(FeatureNode[][] vx, int[] vy, int max_index) {
        if (vx.length != vy.length) 
        	throw new IllegalArgumentException("vx and vy must have same size");
		
        Problem problem = new Problem();
		
        problem.l = vy.length;
        problem.n = max_index;
        problem.bias = this.bias;
        problem.x = vx;
        problem.y = vy;
		
        return problem;
    }

    public static FeatureNode[] instanceToFeatureNode(Instance instance, double bias) {
        // determine size of vx (non-zero attributes + bias)
        FeatureVector fv = (FeatureVector) instance.getData();
        int count = fv.numLocations();
        if (bias >= 0) count++;
        
        // fill array
        FeatureNode[] nodes = new FeatureNode[count];
        
        int numLoc = fv.numLocations();
        int idx = 0;
        for (int i = 0; i < numLoc; i++) {
        	int currentIndex = fv.indexAtLocation(i);
        	idx = Math.max(idx, currentIndex); // ATTN: idx should always be the highest
			nodes[i] = new FeatureNode(currentIndex+1, fv.valueAtLocation(i)); // feature index must start from 1 (and not from 0)
		}

        // add bias term
        if (bias > 0) nodes[count-1] = new FeatureNode(idx + 2, bias);

        return nodes;
    }
}
