/**
 * Subclass of Node for handling floating point numbers
 * @author Dan Yee
 */
public class FloatNode extends Node {
	private float numValue;
	
	public FloatNode(float numValue) {
		this.numValue = numValue;
	}
	
	/**
	 * Accessor for the numerical value of the FloatNode
	 * @return this.numValue - reference to the numerical value
	 */
	public float getNumValue() {
		return this.numValue;
	}
	
	/**
	 * toString override for FloatNode
	 */
	public String toString() {
		return "FloatNode(" + this.numValue + ")";
	}
}
