package struct.tests;

import junit.framework.TestCase;
import cc.mallet.types.Alphabet;

public class TestSequence extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestSequence.class);
	}

	public TestSequence(String name) {
		super(name);
	}

	public void testBasic() {
	    assertEquals(5,5);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private static Alphabet dictOfSize (int size) {
		Alphabet ret = new Alphabet ();
		for (int i = 0; i < size; i++)
			ret.lookupIndex ("feature"+i);
		return ret;
	}
	
}