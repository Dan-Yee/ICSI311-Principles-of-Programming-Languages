/**
 * Representation of a String node
 * @author Dan Yee
 */
public class StringNode extends Node {
	private String value;
	
	public StringNode(String value) {
		this.value = value;
	}
	
	/**
	 * Accessor for the value of the String node
	 * @return this.value - value of the String node
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * toString() override from Node abstract parent class
	 */
	public String toString() {
		return "StringNode(" + this.value + ")";
	}
}
