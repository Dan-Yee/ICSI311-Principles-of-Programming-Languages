/**
 * AssignmentNode stores the node of a variable being assigned to an expression
 * @author Dan Yee
 */
public class AssignmentNode extends StatementNode {
	private VariableNode varNode;
	private Node value;
	
	public AssignmentNode(VariableNode varNode, Node value) {
		this.varNode = varNode;
		this.value = value;
	}
	
	/**
	 * Accessor for the VariableNode which stores the name of the variable
	 * @return this.varNode - a reference to the VariableNode
	 */
	public VariableNode getVarNode() {
		return this.varNode;
	}
	
	/**
	 * Accessor for the value of the assignment
	 * @return this.value - a reference to value of this specific assignment
	 */
	public Node getValue() {
		return this.value;
	}
	
	/**
	 * toString() override to format what the class looks like
	 */
	public String toString() {
		return "AssignmentNode(" + this.varNode + " = " + this.value + ")";
	}
}
