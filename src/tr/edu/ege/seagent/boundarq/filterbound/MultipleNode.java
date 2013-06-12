package tr.edu.ege.seagent.boundarq.filterbound;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class MultipleNode {
	private List<Node> nodes;

	public MultipleNode() {
		super();
		nodes = new ArrayList<Node>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		int addingHashCode = 0;
		String mergedNodeValue = "";
		if (nodes != null) {
			for (Node node : nodes) {
				mergedNodeValue += node.toString();
			}
			addingHashCode = mergedNodeValue.hashCode();
		}
		result = prime * result + addingHashCode;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultipleNode other = (MultipleNode) obj;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else {
			for (Node node : nodes) {
				if (!other.nodes.contains(node)) {
					return false;
				}
			}
		}
		return true;
	}

	public void add(Node node) {
		nodes.add(node);
	}

	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * This method adds variable value to the given node list.
	 * 
	 * @param binding
	 * @param var
	 */
	void addVariableValue(Binding binding, Var var) {
		Node node = binding.get(var);
		if (node == null) {
			node = Node.createAnon();
		}
		add(node);
	}

}
