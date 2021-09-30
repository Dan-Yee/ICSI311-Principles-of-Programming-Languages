/**
 * Subclass of Node for handling Integers
 * @author Dan Yee
 */
public class IntegerNode extends Node {
	private int numValue;
	
	public IntegerNode(int numValue) {
		this.numValue = numValue;
	}
	
	/**
	 * Accessor for the numerical value of the IntegerNode
	 * @return this.numValue - reference to the numerical value
	 */
	public int getNumValue() {
		return this.numValue;
	}
	
	/**
	 * toString override for IntegerNode
	 */
	public String toString() {
		return "IntegerNode(" + this.numValue + ")";
	}
}
