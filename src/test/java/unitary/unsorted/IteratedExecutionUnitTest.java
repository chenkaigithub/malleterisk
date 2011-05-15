package unitary.unsorted;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import utils.IteratedExecution;
import utils.IteratedExecution2;


public class IteratedExecutionUnitTest {
	@Test
	public void testIE1() {
		IteratedExecution ie = new IteratedExecution(1000, 10);
		int i = 0;
		while(ie.hasNext()) {
			double v = ie.next().doubleValue();
			System.out.println(i + ": " + v);
			Assert.assertEquals(1000.0-(100*i++), v);
		}		
	}
	
	@Test
	public void testIE2() {
		int[] percentages = IteratedExecution2.generatePercentages(10);
		IteratedExecution2 ie = new IteratedExecution2(1000, percentages);
		int i = 1;
		while(ie.hasNext()) {
			double v = ie.next().doubleValue();
			System.out.println(i + ": " + v);
			Assert.assertEquals(100.0*i++, v);
		}		
	}
	
	
	
	
	
	
	@Test
	public void test3() {
		int[] percentages = IteratedExecution2.generatePercentages(10);
		int[] sub = Arrays.copyOfRange(percentages, 7, percentages.length);
		int i = 0;
		IteratedExecution2 ie = new IteratedExecution2(1000, sub);
		for (Integer integer : ie) {
			System.out.println(sub[i++] + ": " + integer);
		}
	}
	
}
