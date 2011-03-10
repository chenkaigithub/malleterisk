package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class ResultTransformationScript {
	public static void main(String[] args) throws Exception {
		File folder = new File("seamce-results-2011-03-09-insticc/");

		int i = 0;
		boolean stop;
		StringBuffer sb1;
		StringBuffer sb2;
		String s;
		BufferedReader br;
		for (File file : folder.listFiles()) {
			if(file.getName().equals(".DS_Store")) continue;
			
			// process file
			sb1 = new StringBuffer();
			sb2 = new StringBuffer();
			stop = false;
			br = new BufferedReader(new FileReader(file));
			do {
				s = br.readLine();
				
				if(s!=null) sb2.append(s + ", ");
				else break;
				
				if(++i == 11 || s == null) {
					int l = sb2.length();
					sb1.append(sb2.substring(0, l-2) + "\n");
					sb2 = new StringBuffer();
					i = 0;
				}
			} while (!stop);
			br.close();
			System.out.println(sb1.toString());
			
			// store contents back into the file
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb1.toString().getBytes());
			fos.close();
		}
	}
}
