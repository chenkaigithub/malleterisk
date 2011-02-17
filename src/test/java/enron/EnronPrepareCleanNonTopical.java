package enron;
import java.util.LinkedList;

import data.enron.file.EnronFileCleaning;
import data.enron.utils.EnronUtils;

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
			EnronUtils.ENRON_TOPICAL_FOLDERS_PATH, 
			EnronUtils.ENRON_NON_TOPICAL_FOLDERS_PATH, 
			NON_TOPICAL_FOLDERS
		).clean();
	}
}
