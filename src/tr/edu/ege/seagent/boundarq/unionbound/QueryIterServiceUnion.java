package tr.edu.ege.seagent.boundarq.unionbound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tr.edu.ege.seagent.boundarq.util.BindingPair;
import tr.edu.ege.seagent.boundarq.util.Constants;
import tr.edu.ege.seagent.boundarq.util.QueryIterServiceBound;
import tr.edu.ege.seagent.boundarq.util.ServiceBound;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;


public class QueryIterServiceUnion extends QueryIterServiceBound {

	public QueryIterServiceUnion(QueryIterator input, OpService opService,
			ExecutionContext context) {
		super(input, opService, context);
		if (getExecContext().getContext().get(Constants.UNION_SIZE_SYMBOL) == null) {
			getExecContext().getContext().put(Constants.UNION_SIZE_SYMBOL, 15);
		}
	}

	/**
	 * Executes next service stage
	 * 
	 * @param bindingsCouldBeParent
	 * @return
	 */
	protected List<QueryIterator> nextStage(List<Binding> bindingsCouldBeParent) {

		List<QueryIterator> allIterators = new ArrayList<QueryIterator>();
		// first get union size
		int unionSize = (Integer) getExecContext().getContext().get(
				Constants.UNION_SIZE_SYMBOL);
		// creating op with numeric variables
		List<Op> numericOps = QCUnion.substitute(opService,
				bindingsCouldBeParent, unionSize);
		for (int i = 0; i < numericOps.size(); i++) {
			Op numericOp = numericOps.get(i);
			// creating numeric binding list using numeric op
			List<Binding> numericBindingList = ServiceBound.exec(
					(OpService) numericOp, getExecContext().getContext());
			// creating original binding list using numeric binding list
			List<Binding> originalBindingList = transformBindingsToOriginal(numericBindingList);

			// get binding-parentBinding pairs
			List<BindingPair> bindingPairList = generateBindingPairs(
					bindingsCouldBeParent, originalBindingList,
					numericBindingList, i);
			// generate query iterators
			List<QueryIterator> queryIterators = generateQueryIterators(bindingPairList);

			Collections.reverse(queryIterators);
			allIterators.addAll(queryIterators);
		}
		return allIterators;
	}

	/**
	 * This methos transforms numeric bindings to original ones.
	 * 
	 * @param numericBindingList
	 *            {@link Binding} list to be transformed into original
	 * @return transformed {@link Binding} list
	 */
	protected List<Binding> transformBindingsToOriginal(
			List<Binding> numericBindingList) {
		List<Binding> originalBindingList = new ArrayList<Binding>();
		// traverse on numeric binding list to transform originals
		for (Binding binding : numericBindingList) {
			// create a new binding map
			BindingMap newBinding = new BindingHashMap();
			// get variables of numeric binding
			Iterator<Var> vars = binding.vars();
			// traverse on variables
			while (vars.hasNext()) {
				// get variable itself
				Var var = (Var) vars.next();
				// get Node value of variable
				Node node = binding.get(var);
				// get variable name
				String varName = var.getName();
				// transform variable name
				String originalName = varName.split("_")[0];
				// create original variable value using tranfromed variable name
				Var originalVar = Var.alloc(originalName);
				// add original variable and its node value to binding map
				newBinding.add(originalVar, node);
			}
			// add binding to original binding list
			originalBindingList.add(newBinding);
		}
		return originalBindingList;
	}

	/**
	 * This method finds parent binding of given {@link QueryIterator} instance
	 * 
	 * @param bindingsCouldBeParent
	 * @param defaultIndex
	 * @param queryIterator
	 * @param originalBindingList
	 * @param numericBindingList
	 * 
	 * @return
	 */
	protected List<BindingPair> generateBindingPairs(
			List<Binding> bindingsCouldBeParent,
			List<Binding> originalBindings, List<Binding> numericBindings,
			int defaultIndex) {
		// create an empty bindingPair list
		List<BindingPair> binPairList = new ArrayList<BindingPair>();
		if (numericBindings != null && numericBindings.size() > 1
				&& originalBindings.size() == numericBindings.size()) {
			for (int i = 0; i < numericBindings.size(); i++) {
				Binding numericBinding = numericBindings.get(i);
				// find parent binding number
				int parentBindingNo = findNumberOfBindingVars(
						numericBinding.vars(), 0);
				// get parent binding
				Binding parentBinding = bindingsCouldBeParent
						.get(parentBindingNo);
				// creating binding pair
				BindingPair bindingPair = new BindingPair(
						originalBindings.get(i), parentBinding);
				// add pair to list
				// if (!binPairList.contains(bindingPair)) {
				binPairList.add(bindingPair);
				// }
			}
		} else if (numericBindings != null && numericBindings.size() == 1
				&& originalBindings.size() == numericBindings.size()) {
			// if numeric bindings contains 1 element add only itself and parent
			// as itself to list
			int parentBindingNo = findNumberOfBindingVars(numericBindings
					.get(0).vars(), defaultIndex);
			Binding parent = bindingsCouldBeParent.get(parentBindingNo);
			System.out.println("Self binding: " + originalBindings.get(0)
					+ ", Parent Binding: " + parent);
			binPairList.add(new BindingPair(originalBindings.get(0), parent));
		}
		return binPairList;
	}

	/**
	 * This method finds number of binding
	 * 
	 * @param vars
	 * @param defaultIndex
	 * @return binding number
	 */
	private int findNumberOfBindingVars(Iterator<Var> vars, int defaultIndex) {
		while (vars.hasNext()) {
			Var var = (Var) vars.next();
			String varName = var.getName();
			// split variable name as "_" token
			String[] splittedTexts = varName.split("_");
			if (splittedTexts != null && splittedTexts.length > 1) {
				return Integer.parseInt(splittedTexts[1]);
			}
		}
		return defaultIndex;
	}

}
