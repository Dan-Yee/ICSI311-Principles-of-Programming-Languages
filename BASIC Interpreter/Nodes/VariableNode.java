/**
 * Variable Node class for storing the name of a variable
 * @author Dan Yee
 */
public class VariableNode extends Node {
	private String varName;
	
	public VariableNode(String varName) {
		this.varName = varName;
	}
	
	/**
	 * Accessor for the name of a variable
	 * @return this.varName - reference to the name of the variable
	 */
	public String getVarName() {
		return this.varName;
	}
	
	/**
	 * toString() override for VariableNode which stores the name of a variable
	 */
	public String toString() {
		return "VariableNode(" + this.varName + ")";
	}
}
