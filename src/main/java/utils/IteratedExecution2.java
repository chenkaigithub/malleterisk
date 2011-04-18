package utils;

import java.security.InvalidParameterException;
import java.util.Iterator;

/**
 * Helper class for iterating a numeric array through pre-defined percentages.
 * Order of iteration is the same as the array.
 * 
 * Usage:
 * int[] percentages = IteratedExecution2.generatePercentages(step);
 * IteratedExecution2 ie = new IteratedExecution2(alphabetSize, percentages);
 * while(ie.hasNext()) {
 * 		int n = ie.next();
 * 		// do stuff with n
 * }
 * 
 * @author tt
 *
 */
public class IteratedExecution2 implements Iterator<Integer>, Iterable<Integer> {
	public static final int[] generatePercentages(int step) {
		if(step > 100) throw new InvalidParameterException("Step cannot be over 100.");
		int counter = 0;
		int length = Math.round(100/step);
		int[] percentages = new int[length];
		
		percentages[0] = (counter += step);
		for (int i = 1; i < percentages.length; i++)
			percentages[i] = (counter += step);
		
		return percentages;
	}
	
	private final int alphabetSize;
	private final int[] percentages;
	private int currentPercentageIndex;
	
	public IteratedExecution2(int alphabetSize, int[] percentages) {
		this.alphabetSize = alphabetSize;
		this.percentages = percentages;
		this.currentPercentageIndex = 0;
	}
	
	@Override
	public boolean hasNext() {
		return (currentPercentageIndex < percentages.length);
	}

	@Override
	public Integer next() {
		return (int) Math.ceil(percentages[currentPercentageIndex++]*alphabetSize/100);
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
