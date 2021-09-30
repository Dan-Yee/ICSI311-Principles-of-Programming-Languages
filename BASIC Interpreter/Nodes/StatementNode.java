/**
 * Class that parses tokens created in Lexer class
 * @author Dan Yee
 */
public abstract class StatementNode extends Node {
	protected StatementNode nextStatement = null;
	/**
	 * Abstract toString() override for child classes of StatementNode
	 */
	public abstract String toString();
	
	/**
	 * Accessor to get the next statement to be interpreted
	 * @return this.nextStatement - a reference to the next statement
	 */
	protected StatementNode getNextStatement() {
		return this.nextStatement;
	}
	
	/**
	 * Mutator to change the next statement for each StatementNode
	 * @param statement - a reference to the statement that will be the next statement to be interpreted
	 */
	protected void setNextStatement(StatementNode statement) {
		this.nextStatement = statement;
	}
}
