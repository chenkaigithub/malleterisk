package data.source.enron;

import java.io.File;
import java.util.Collection;

public class EnronFileCleaning {
	private final String srcFolder;
	private final String dstFolder;
	private final Collection<String> nonTopicalFolders;
	
	public EnronFileCleaning(String srcFolder, String dstFolder, Collection<String> nonTopicalFolders) {
		this.srcFolder = srcFolder;
		this.dstFolder = dstFolder;
		this.nonTopicalFolders = nonTopicalFolders;
	}
	
	public void clean() throws Exception {
		for (File accountFolder : new File(srcFolder).listFiles()) {
			if(!accountFolder.isDirectory()) continue; 
			
			for(File cf : accountFolder.listFiles()) {
				if(!cf.isDirectory()) continue;
				
				if(isNonTopicalFolder(cf.getName())) 
					moveToNonTopical(accountFolder.getName(), cf);
			}
		}
	}
	
	private boolean isNonTopicalFolder(String folderName) {
		if(nonTopicalFolders.contains(folderName))
			return true;
		return false;
	}
		
	private void moveToNonTopical(String accountFolder, File nonTopicalFolder) throws Exception {
		File f1 = new File(dstFolder + File.separator + accountFolder + File.separator + nonTopicalFolder.getName());
		if(!f1.getParentFile().exists()) f1.getParentFile().mkdir();
		if(!f1.exists()) f1.mkdir();
		
		moveToFolder(nonTopicalFolder, f1.getAbsolutePath());
	}
	
	public static void moveToFolder(File srcFolder, String dstFolder) throws Exception {
		// ATTN: this is a weak way to "move" a folder
		if(!srcFolder.renameTo(new File(dstFolder))) { 
			throw new Exception("\nMove Operation Failed!");
		}
	}
}
