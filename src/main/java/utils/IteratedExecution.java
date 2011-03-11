package utils;

import java.util.Iterator;

/**
 * Helper class for iterating a numeric array. Calculates the percentage internally
 * through the given step.
 * 
 * @author tt
 *
 */
public class IteratedExecution implements Iterator<Integer>, Iterable<Integer> {
	private final int percentageStep;
	private final double stepSize;
	private int currentStep;
	
	public IteratedExecution(int alphabetSize, int percentageStep) {
		this.percentageStep = percentageStep;
		this.stepSize = (double)alphabetSize / 100.0;
		this.currentStep = 100;
	}
	
	@Override
	public boolean hasNext() {
		return ((currentStep - percentageStep) > 0);
	}

	@Override
	public Integer next() {
		// get current value
		int i = (int)Math.ceil(currentStep*stepSize);
		
		// advance
		currentStep -= percentageStep;
		
		return i;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return this;
	}	
}
