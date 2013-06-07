package extendingconcepts.filterbound;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.sparql.core.Var;

public class MultipleVariable {

	private List<Var> vars;

	public MultipleVariable() {
		super();
		vars = new ArrayList<Var>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vars == null) ? 0 : vars.hashCode());
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
		MultipleVariable other = (MultipleVariable) obj;
		if (vars == null) {
			if (other.vars != null)
				return false;
		} else {
			for (Var var : vars) {
				if (!other.vars.contains(var)) {
					return false;
				}
			}
		}
		return true;
	}

	public void add(Var var) {
		vars.add(var);
	}

	public List<Var> getVars() {
		return vars;
	}

}
