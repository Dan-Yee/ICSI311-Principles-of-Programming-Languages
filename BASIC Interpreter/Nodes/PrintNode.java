import java.util.ArrayList;

/**
 * PrintNode class for storing a list of Nodes (things to print)
 * @author Dan Yee
 */
public class PrintNode extends StatementNode {
	private ArrayList<Node> printNodeList = new ArrayList<Node>();
	
	public PrintNode(ArrayList<Node> printNodeList) {
		this.printNodeList = printNodeList;
	}
	
	/**
	 * Accessor for the current specific value of PrintNode
	 * @return this.value - a reference to the value
	 */
	public ArrayList<Node> getPrintList() {
		return this.printNodeList;
	}
	
	/**
	 * toString() override to print out the ArrayList of nodes
	 */
	public String toString() {
		return "PrintNode(" + this.getPrintList() + ")";
	}
}
