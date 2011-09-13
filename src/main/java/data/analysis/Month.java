package data.analysis;

public class Month {
	public final String month;
	public final String year;
	
	public Month(String m, String y) {
		month = m;
		year = y;
	}
	
	public int hashCode() {
		return month.hashCode() * year.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof Month) {
			Month other = (Month) o;
			
			return this.month.equals(other.month) && 
				this.year.equals(other.year);
		}
		
		return false;
	}
	
	public String toString() {
		return year+", "+Integer.valueOf(month);
	}
}