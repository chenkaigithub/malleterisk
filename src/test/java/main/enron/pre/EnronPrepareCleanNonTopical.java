package main.enron.pre;
import java.util.LinkedList;

import data.source.enron.EnronConstants;
import data.source.enron.EnronFileCleaning;

public class EnronPrepareCleanNonTopical {
	public static void main(String[] args) throws Exception {
		LinkedList<String> NON_TOPICAL_FOLDERS = new LinkedList<String>();
		NON_TOPICAL_FOLDERS.add("_sent_mail");
		NON_TOPICAL_FOLDERS.add("all_documents");
		NON_TOPICAL_FOLDERS.add("deleted_items");
		NON_TOPICAL_FOLDERS.add("discussion_threads");
		NON_TOPICAL_FOLDERS.add("inbox");
		NON_TOPICAL_FOLDERS.add("notes_inbox");
		NON_TOPICAL_FOLDERS.add("sent");
		NON_TOPICAL_FOLDERS.add("sent_items");
		
		new EnronFileCleaning(
			EnronConstants.ENRON_TOPICAL_FOLDERS_PATH, 
			EnronConstants.ENRON_NON_TOPICAL_FOLDERS_PATH, 
			NON_TOPICAL_FOLDERS
		).clean();
	}
}
