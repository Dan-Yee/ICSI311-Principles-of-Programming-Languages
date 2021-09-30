/**
 * Specifications of an if statement
 * @author Dan Yee
 */
public class IfNode extends StatementNode {
	private BooleanOperationNode boolOp;
	private VariableNode identifier;
	
	public IfNode(BooleanOperationNode boolOp, VariableNode identifier) {
		this.boolOp = boolOp;
		this.identifier = identifier;
	}
	
	/**
	 * Accessor to get the BooleanOperationNode of this if statement
	 * @return this.boolOp - a reference to the BooleanOperationNode 
	 */
	public BooleanOperationNode getOp() {
		return this.boolOp;
	}
	
	/**
	 * Accessor to get the identifier of this if statement
	 * @return this.identifier - a reference to the identifier
	 */
	public VariableNode getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * toString() override to display this IfNode
	 */
	public String toString() {
		return "IfNode(IF " + this.boolOp + " THEN " + this.identifier + ")";
	}
}
