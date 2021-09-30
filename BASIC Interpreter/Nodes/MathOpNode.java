/**
 * Subclass of Node for handling mathematical expressions
 * @author Dan Yee
 */
public class MathOpNode extends Node {
	public enum Operators {
		plus {
			@Override
			public String toString() {
				return "+";
			}
		},
		minus {
			@Override
			public String toString() {
				return "-";
			}
		},
		multiply {
			@Override
			public String toString() {
				return "*";
			}
		},
		divide {
			@Override
			public String toString() {
				return "/";
			}
		};
	}
	
	private Node leftOperand;
	private Node rightOperand;
	private Operators operator;
	
	public MathOpNode(Node leftOperand, Operators operator, Node rightOperand) {
		this.leftOperand = leftOperand;
		this.operator = operator;
		this.rightOperand = rightOperand;
	}
	
	/**
	 * Accessor for the left operand of the regular expression
	 * @return this.leftOperand - reference to the left operand
	 */
	public Node getLeftOperand() {
		return this.leftOperand;
	}
	
	/**
	 * Accessor for the right operand of the regular expression
	 * @return this.rightOperand - reference to the right operand
	 */
	public Node getRightOperand() {
		return this.rightOperand;
	}
	
	/**
	 * Accessor for the operator of this regular expression
	 * @return this.operator - reference to the operator
	 */
	public Operators getOperator() {
		return this.operator;
	}
	
	/**
	 * toString override to display MathOpNode as a regular expression
	 */
	public String toString() {
		return "MathOpNode(" + this.leftOperand + " " + this.operator + " " + this.rightOperand + ")";
	}
}
