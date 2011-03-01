package utils;

import java.util.Iterator;

public class IteratedExecution implements Iterator<Integer>, Iterable<Integer> {
	private final int percentageStep;
	private final double stepSize;
	private int currentStep;
	
	// TODO: another option would be to receive an array of percentages and iterate them
	// e.g. [100, 97, 95, 90, 80, 70, 60, 50] and create a generator method
	// that returns an array of that type with given params
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
		currentStep -= percentageStep;
		 return (int) Math.ceil(currentStep*stepSize);
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
