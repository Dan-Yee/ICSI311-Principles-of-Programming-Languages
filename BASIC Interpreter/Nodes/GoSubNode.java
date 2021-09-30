/**
 * Specifications of GOSUB node for subroutines
 * @author Dan Yee
 */
public class GoSubNode extends StatementNode {
	private VariableNode gosubto;
	
	public GoSubNode(VariableNode gosubto) {
		this.gosubto = gosubto;
	}
	
	/**
	 * Accessor to get the VariableNode being referenced to for GOSUB
	 * @return this.gosubto - a reference to the variable that GOSUB will refer to
	 */
	public VariableNode getGoSub() {
		return this.gosubto;
	}
	
	/**
	 * toString() override to print out the GoSubNode
	 */
	public String toString() {
		return "GOSUB(" + this.gosubto + ")";
	}
}
