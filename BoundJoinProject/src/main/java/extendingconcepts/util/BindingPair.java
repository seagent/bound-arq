package extendingconcepts.util;

import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class BindingPair {
	private Binding selfBinding;
	private Binding parentBinding;

	public BindingPair(Binding selfBinding, Binding parentBinding) {
		this.selfBinding = selfBinding;
		this.parentBinding = parentBinding;
	}

	public Binding getParentBinding() {
		return parentBinding;
	}

	public void setParentBinding(Binding parentBinding) {
		this.parentBinding = parentBinding;
	}

	public Binding getSelfBinding() {
		return selfBinding;
	}

	public void setSelfBinding(Binding binding) {
		this.selfBinding = binding;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BindingPair other = (BindingPair) obj;
		if (parentBinding == null) {
			if (other.parentBinding != null)
				return false;
		} else if (!parentBinding.equals(other.parentBinding))
			return false;
		if (selfBinding == null) {
			if (other.selfBinding != null)
				return false;
		} else if (!selfBinding.equals(other.selfBinding))
			return false;
		return true;
	}

}
