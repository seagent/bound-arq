package tr.edu.ege.seagent.boundarq.util;

public class VariablePair {

	private String oldVariable;

	private String replacingVariable;

	public VariablePair(String oldVariable, String replacingVariable) {
		this.oldVariable = oldVariable;
		this.replacingVariable = replacingVariable;
	}

	public String getOldVariable() {
		return oldVariable;
	}

	public String getReplacingVariable() {
		return replacingVariable;
	}

	public void setOldVariable(String oldVariable) {
		this.oldVariable = oldVariable;
	}

	public void setReplacingVariable(String replacingVariable) {
		this.replacingVariable = replacingVariable;
	}

}
