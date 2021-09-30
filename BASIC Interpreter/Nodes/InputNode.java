import java.util.ArrayList;

/**
 * Class that handles an Input Statement
 * @author Dan Yee
 */
public class InputNode extends StatementNode {
	private Node firstValue;
	private ArrayList<VariableNode> varList = new ArrayList<VariableNode>();
	
	public InputNode(Node firstValue, ArrayList<VariableNode> varList) {
		this.firstValue = firstValue;
		this.varList = varList;
	}
	
	/**
	 * Accessor for the first value of the input statement
	 * @return this.firstValue - a reference to the first value of the statement
	 */
	public Node getFirstValue() {
		return this.firstValue;
	}
	
	/**
	 * Accessor for the list of variables of the input statement
	 * @return this.varList - a reference to the arraylist of VariableNode's associated with this statement
	 */
	public ArrayList<VariableNode> getVarList() {
		return this.varList;
	}
	
	/**
	 * toString() override for formatting
	 */
	public String toString() {
		return "InputNode(" + this.firstValue + ", " + this.varList + ")";
	}
}
