/**
 * Specifications of a Next statement
 * @author Dan Yee
 */
public class NextNode extends StatementNode {
	private VariableNode nextVar;
	private Node forNextStatement;
	
	public NextNode(VariableNode nextVar) {
		this.nextVar = nextVar;
		this.forNextStatement = null;
	}
	
	/**
	 * Accessor to get the identifier of this Next statement
	 * @return this.nextVar - a reference to the VariableNode associated with this Next Statement
	 */
	public VariableNode getNextVar() {
		return this.nextVar;
	}
	
	/**
	 * Accessor to link back to its corresponding FOR statement
	 * @return this.forNextStatement - a reference to the corresponding FOR statement or null
	 */
	public Node getNext() {
		return this.forNextStatement;
	}
	
	/**
	 * Mutator to change the link from null to its corresponding FOR statement
	 * @param forNextStatement - a reference to the FOR statement to be linked to
	 */
	public void setNext(Node forNextStatement) {
		this.forNextStatement = forNextStatement;
	}
	
	/**
	 * toString() override to print out the NextNode
	 */
	public String toString() {
		return "NextNode(" + this.nextVar + ")";
	}
}
