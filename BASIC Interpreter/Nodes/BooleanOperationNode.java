/**
 * Specifications of a boolean operation
 * @author Dan Yee
 */
public class BooleanOperationNode extends Node {
	public enum boolOperators {
		greaterThan{
			@Override
			public String toString() {
				return ">";
			}
		}, 
		greaterThanEquals{
			@Override
			public String toString() {
				return ">=";
			}
		}, 
		lessThan {
			@Override 
			public String toString() {
				return "<";
			}
		}, 
		lessThanEquals{
			@Override
			public String toString() {
				return "<=";
			}
		}, 
		notEquals{
			@Override
			public String toString() {
				return "<>";
			}
		}, 
		equals{
			@Override
			public String toString() {
				return "=";
			}
		};
	}
	private Node leftExpression;
	private Node rightExpression;
	private boolOperators operator;
	
	public BooleanOperationNode(Node leftExpression, boolOperators operator, Node rightExpression) {
		this.leftExpression = leftExpression;
		this.operator = operator;
		this.rightExpression = rightExpression;
	}
	
	/**
	 * Accessor to get the left expression of this boolean expression
	 * @return this.leftExpression - a reference to the left expression
	 */
	public Node getLeftExp() {
		return this.leftExpression;
	}
	
	/**
	 * Accessor to get the right expression of this boolean expression
	 * @return this.rightExpression - a reference to the right expression
	 */
	public Node getRightExp() {
		return this.rightExpression;
	}
	
	/**
	 * Accessor to get the operator of this boolean expression
	 * @return this.operator - a reference to the operator
	 */
	public boolOperators getOp() {
		return this.operator;
	}
	
	/**
	 * toString() override to display the BooleanOperationNode
	 */
	public String toString() {
		return "BoolOpNode(" + this.leftExpression + " " + this.operator + " " + this.rightExpression + ")"; 
	}
}
