package guice;

import com.google.inject.Binder;
import com.google.inject.Module;

public class GuiceModule implements Module {
	@Override
	public void configure(Binder binder) {
		GuiceParam p = new GuiceParam(42);
		
		binder.bind(String.class).toInstance("Hello World ");
		binder.bind(int.class).toInstance(42);
		binder.bind(GuiceParam.class).toInstance(p);
	}
}
