package utils;

import java.io.FileWriter;
import java.io.IOException;

public class OutputUtils {
	public static void toCSV(String testName, Object[] data) throws IOException {
		FileWriter out = new FileWriter(testName);
		
		for(Object d : data) {
			out.write(d.toString());
			out.write("\n");
		}
		
		out.close();
	}
}
