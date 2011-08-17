package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionsUtils {
	public static <T> List<T> asSortedList(Collection<T> collection, Comparator<? super T> comparator) {
	  List<T> list = new ArrayList<T>(collection);
	  Collections.sort(list, comparator);
	  
	  return list;
	}
}
