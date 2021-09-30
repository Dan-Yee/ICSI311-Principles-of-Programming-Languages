import java.util.ArrayList;

/**
 * holds a list of StatementNode
 * @author Dan Yee
 */
public class StatementsNode extends StatementNode {
	private ArrayList<Node> statementNodeList = new ArrayList<Node>();
	
	public StatementsNode(ArrayList<Node> statementNodeList) {
		this.statementNodeList = statementNodeList;
	}
	
	/**
	 * Accessor the current specific value of node
	 * @return this.node - a reference to the node
	 */
	public ArrayList<Node> getStatementsNode() {
		return this.statementNodeList;
	}
	
	/**
	 * toString() override to print out the ArrayList of StatementNodes
	 */
	public String toString() {
		return "StatementsNode(" + statementNodeList.toString() + ")";
	}
}
