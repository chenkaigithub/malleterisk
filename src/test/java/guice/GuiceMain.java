package guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceMain {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new GuiceModule());
		GuiceConsumer gc = injector.getInstance(GuiceConsumer.class);
		gc.x();
	}
}
