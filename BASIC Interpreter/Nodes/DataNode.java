import java.util.ArrayList;

/**
 * Representation of a Data node or a list of StringNode, IntegerNode, and FloatNode
 * @author Dan Yee
 */
public class DataNode extends StatementNode {
	private ArrayList<Node> dataNodeList = new ArrayList<Node>();
	
	public DataNode(ArrayList<Node> dataNodeList) {
		this.dataNodeList = dataNodeList;
	}
	
	/**
	 * Accesssor to get the list of data nodes
	 * @return this.dataNodeList - a reference to the arraylist of StringNode's and Integer/Float Nodes
	 */
	public ArrayList<Node> getDataList() {
		return this.dataNodeList;
	}
	
	/**
	 * toString() override to print out the ArrayList of StringNodes, IntegerNodes, and FloatNodes
	 */
	public String toString() {
		return "DataNode(" + this.getDataList() + ")";
	}
}
