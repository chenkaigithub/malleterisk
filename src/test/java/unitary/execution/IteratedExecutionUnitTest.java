package unitary.execution;

import junit.framework.Assert;

import org.junit.Test;

import execution.IteratedExecution;

public class IteratedExecutionUnitTest {
	@Test
	public void test() {
		IteratedExecution ie = new IteratedExecution(1000, 10);
		int i = 0;
		while(ie.hasNext()) {
			double v = ie.next().doubleValue();
			System.out.println(i + ": " + v);
			Assert.assertEquals(1000.0-(100*i++), v);
		}
		
	}

}
