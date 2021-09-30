import java.util.ArrayList;

/**
 * Representation of a Read node or a list of Variables
 * @author Dan Yee
 */
public class ReadNode extends StatementNode {
	private ArrayList<VariableNode> readNodeList = new ArrayList<VariableNode>();
	
	public ReadNode(ArrayList<VariableNode> readNodeList) {
		this.readNodeList = readNodeList;
	}
	
	/**
	 * Accessor for the list of variables
	 * @return this.readNodeList - reference to the ArrayList of variables
	 */
	public ArrayList<VariableNode> getReadList() {
		return this.readNodeList;
	}
	
	/**
	 * toString() override to print out the ArrayList of variables
	 */
	public String toString() {
		return "ReadNode(" + this.getReadList() + ")";
	}
}
