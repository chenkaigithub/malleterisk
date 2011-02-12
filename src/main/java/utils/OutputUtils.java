package utils;

import java.io.FileWriter;
import java.io.IOException;

public class OutputUtils {
	public static void to_csv(String test_name, Object[] data) throws IOException {
		FileWriter out = new FileWriter(test_name);
		
		for(Object d : data) {
			out.write(d.toString());
			out.write("\n");
		}
		
		out.close();
	}
}
