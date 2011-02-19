package misc;

public abstract class A {
	public A() {
		System.out.println("A()");
	}
	
	public A(String s) {
		this();
		System.out.println(s);
	}
}
