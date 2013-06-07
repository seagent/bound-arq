package extendingconcepts.util;

import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class NumericBinding {
	private Binding binding;
	private int bindingNo;

	public NumericBinding(Binding binding, int bindingNo) {
		super();
		this.binding = binding;
		this.bindingNo = bindingNo;
	}

	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public int getBindingNo() {
		return bindingNo;
	}

	public void setBindingNo(int bindingNo) {
		this.bindingNo = bindingNo;
	}

}
