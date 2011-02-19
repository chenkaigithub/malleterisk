package guice;

import com.google.inject.Inject;

public class GuiceConsumer {
	private String s;
	private int i;
	private GuiceParam p;
	
	@Inject
	public GuiceConsumer(String s, int i, GuiceParam p) {
		this.s = s;
		this.i = i;
		this.p = p;
	}
	
	public void x() {
		System.out.println(s + (i + p.getX()));
	}
}
