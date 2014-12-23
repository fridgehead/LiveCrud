package compiler;

public class FuckedSourceException extends Exception {
	public long row, column;
	public String error;
	
	public String toString(){
		return "Error at " + row + ":" + column + " - " + error;
	
	}
}
