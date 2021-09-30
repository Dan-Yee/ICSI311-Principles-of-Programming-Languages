/**
 * Specifications of For node for for loops
 * @author Dan Yee
 */
public class ForNode extends StatementNode {
	private VariableNode var;
	private IntegerNode startValue;
	private IntegerNode finalValue;
	private IntegerNode stepValue;
	private Node forNextStatement;
	
	public ForNode(VariableNode var, IntegerNode startValue, IntegerNode finalValue, IntegerNode stepValue) {
		this.var = var;
		this.startValue = startValue;
		this.finalValue = finalValue;
		this.stepValue = stepValue;
		this.forNextStatement = null;
	}
	
	public ForNode(VariableNode var, IntegerNode startValue, IntegerNode finalValue) {									// constructor if a STEP value isn't specified
		this.var = var;
		this.startValue = startValue;
		this.finalValue = finalValue;
		this.stepValue = new IntegerNode(1);
		this.forNextStatement = null;
	}
	
	/**
	 * Accessor to get the variable from the startValue
	 * @return this.assign - a reference to the VariableNode in ForNode
	 */
	public VariableNode getVar() {
		return this.var;
	}
	
	/**
	 * Accessor to get the start value of the for loop
	 * @return this.startValue - a reference to the IntegerNode for start value in ForNode
	 */
	public IntegerNode getStartValue() {
		return this.startValue;
	}
	
	/**
	 * Accessor to get the upper loop bound of a ForNode
	 * @return this.finalValue - a reference to the IntegerNode representing the upper loop bound
	 */
	public IntegerNode getFinalValue() {
		return this.finalValue;
	}
	
	/**
	 * Accessor to get the step value of the ForNode
	 * @return this.stepValue - a reference to the IntegerNode representing the step value of the ForNode
	 */
	public IntegerNode getStepValue() {
		return this.stepValue;
	}
	
	/**
	 * Accessor to link to the statement after NEXT or null
	 * @return this.forNextStatement - a reference to the statement after NEXT or null
	 */
	public Node getNext() {
		return this.forNextStatement;
	}
	
	/**
	 * Mutator to change the link from null to the statement after NEXT
	 * @param forNextStatement - a reference to the statement after NEXT or null
	 */
	public void setNext(Node forNextStatement) {
		this.forNextStatement = forNextStatement;
	}
	
	/**
	 * toString() override to print out the ForNode
	 */
	public String toString() {
		return "ForNode(FOR " + this.var + " = " + this.startValue + " TO " + this.finalValue + " STEP " + this.stepValue + ")";
	}
}