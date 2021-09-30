/**
 * Specifications of a LabeledStatementNode
 * @author Dan Yee
 */
public class LabeledStatementNode extends StatementNode {
	private String label;
	private Node statement;
	
	public LabeledStatementNode(String label, Node statement) {
		this.label = label;
		this.statement = statement;
	}
	
	/**
	 * Accessor for the name of the label
	 * @return this.label - a reference to the name of the label
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * Accessor to get the statement that is being labeled
	 * @return this.statement - a reference to the labeled statement
	 */
	public Node getStatement() {
		return this.statement;
	}
	
	/**
	 * toString() override to print out the LabeledStatementNode
	 */
	public String toString() {
		return "LabeledStatementNode(" + this.label + ", " + this.statement + ")";
	}
}
