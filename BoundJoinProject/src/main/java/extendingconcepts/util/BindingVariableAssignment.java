package extendingconcepts.util;

import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class BindingVariableAssignment {

	private final Binding binding;
	private final Var var;

	public BindingVariableAssignment(Binding binding, Var var) {
		this.binding = binding;
		this.var = var;
	}

	public Binding getBinding() {
		return binding;
	}

	public Var getVar() {
		return var;
	}

}
