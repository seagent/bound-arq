package tr.edu.ege.seagent.boundarq.filterbound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;

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
import com.hp.hpl.jena.sparql.engine.binding.BindingRoot;


public class QueryIterServiceFilter extends QueryIterServiceBound {

	public QueryIterServiceFilter(QueryIterator input, OpService opService,
			ExecutionContext context) {
		super(input, opService, context);
		if (getExecContext().getContext().get(Constants.FILTER_TYPE) == null) {
			getExecContext().getContext().put(Constants.FILTER_TYPE,
					Constants.OR_FILTER);
		}
		logger.setLevel(Level.DEBUG);
	}

	/**
	 * Executes next service stage
	 * 
	 * @param bindingsCouldBeParent
	 * @return
	 */
	protected List<QueryIterator> nextStage(List<Binding> bindingsCouldBeParent) {

		List<QueryIterator> allIterators = new ArrayList<QueryIterator>();
		String filterType = (String) getExecContext().getContext().get(
				Constants.FILTER_TYPE);
		// creating filter Ops.
		List<Op> filterOps = QCFilter.substitute(opService,
				bindingsCouldBeParent, filterType);
		for (int i = 0; i < filterOps.size(); i++) {
			// creating numeric binding list using numeric op
			List<Binding> bindingList = ServiceBound
					.exec((OpService) filterOps.get(i), getExecContext()
							.getContext());

			// logger.debug("Binding pairs is beginning to be generated...");
			// long before = System.currentTimeMillis();
			// find binding pairs
			List<BindingPair> bindingPairs = generateBindingPairs(bindingList,
					bindingsCouldBeParent, opService.getService(),
					((OpService) filterOps.get(i)).getService());
			// long after = System.currentTimeMillis();
			// logger.debug(MessageFormat.format(
			// "Binding pairs has been generated in \"{0}\" miliseconds",
			// after - before));

			// logger.debug("Binding pairs is beginning to be intersected...");
			// before = System.currentTimeMillis();
			// intersect bindings and their parents contained in binding pairs
			bindingPairs = intersectBindingPairs(bindingPairs);
			// after = System.currentTimeMillis();
			// logger.debug(MessageFormat
			// .format("Binding pairs has been intersected in \"{0}\" miliseconds",
			// after - before));

			// logger.debug("Query iterators is beginning to be generated...");
			// before = System.currentTimeMillis();
			// make query iterators using binding pairs
			List<QueryIterator> queryIterators = generateQueryIterators(bindingPairs);
			// after = System.currentTimeMillis();
			// logger.debug(MessageFormat
			// .format("Query iterators has been generated in \"{0}\" miliseconds",
			// after - before));

			// reverse iterators
			Collections.reverse(queryIterators);
			allIterators.addAll(queryIterators);
		}
		return allIterators;
	}

	/**
	 * This method generates variable list of given {@link Binding}
	 * 
	 * @param binding
	 * @return
	 */
	private List<Var> generateVarList(Binding binding) {
		List<Var> varList = new ArrayList<Var>();
		Iterator<Var> varIter = binding.vars();
		while (varIter.hasNext()) {
			Var var = (Var) varIter.next();
			varList.add(var);
		}
		return varList;
	}

	/**
	 * This method reorganizes binding pairs as not containing any common
	 * {@link Var}<-->{@link Node} mapping
	 * 
	 * @param bindingPairs
	 * @return
	 */
	private List<BindingPair> intersectBindingPairs(
			List<BindingPair> bindingPairs) {
		List<BindingPair> organizedPairs = new ArrayList<BindingPair>();
		for (BindingPair bindingPair : bindingPairs) {
			// define new binding for not to add same variable value with parent
			BindingHashMap bindingHashMap = new BindingHashMap();
			// construct variable list of parent binding
			List<Var> parentVars = generateVarList(bindingPair
					.getParentBinding());
			// get variable iterator of self binding
			Iterator<Var> varIter = bindingPair.getSelfBinding().vars();
			while (varIter.hasNext()) {
				Var var = (Var) varIter.next();
				// check parent variables contains this variable
				if (!parentVars.contains(var)) {
					// if so not add this variable value mapping to the hashmap
					bindingHashMap.add(var,
							bindingPair.getSelfBinding().get(var));
				}
			}
			organizedPairs.add(new BindingPair(bindingHashMap, bindingPair
					.getParentBinding()));
		}
		return organizedPairs;
	}

	/**
	 * This method generates {@link BindingPair}s which contans {@link Binding}
	 * itself and its parent.
	 * 
	 * @param bindingList
	 * @param bindingsCouldBeParent
	 * @param serviceNode
	 * @param node
	 * @return
	 */
	private List<BindingPair> generateBindingPairs(List<Binding> bindingList,
			List<Binding> bindingsCouldBeParent, Node serviceNodeOriginal,
			Node serviceNodeSubstituted) {

		List<BindingPair> bindingPairs = new ArrayList<BindingPair>();

		// check whether binding lists are not empty
		if (!bindingList.isEmpty() && !bindingsCouldBeParent.isEmpty()) {

			// reduce unappropriate parent candidates
			bindingsCouldBeParent = reduceParentBindings(bindingsCouldBeParent,
					serviceNodeOriginal, serviceNodeSubstituted);

			// create self variables
			List<Var> selfVars = generateVarList(bindingList.get(0));
			// create parent variables
			List<Var> parentVars = generateVarList(bindingsCouldBeParent.get(0));
			// find common variable
			MultipleVariable multipleCommonVariable = findCommonVariables(
					selfVars, parentVars);
			// conrtol multiple common variable
			if (!multipleCommonVariable.getVars().isEmpty()) {
				// generate binding mappings for parent bindings
				HashMap<Integer, List<Binding>> parentMappings = constructBindingMappings(
						bindingsCouldBeParent, multipleCommonVariable);
				// fill binding pair list
				fillBindingPairs(bindingList, bindingPairs,
						multipleCommonVariable, parentMappings);
			} else {
				for (Binding binding : bindingList) {
					assignAllParents(bindingsCouldBeParent, bindingPairs,
							binding);
				}
			}
		}
		return bindingPairs;
	}

	/**
	 * This method reduces given parent {@link Binding} candidate {@link List}
	 * as removing unappropriate candidates.
	 * 
	 * @param bindingsCouldBeParent
	 * @param serviceNodeOriginal
	 * @param serviceNodeSubstituted
	 * @return
	 */
	private List<Binding> reduceParentBindings(
			List<Binding> bindingsCouldBeParent, Node serviceNodeOriginal,
			Node serviceNodeSubstituted) {
		// check whether original service node is variable or not
		if (serviceNodeOriginal.isVariable()) {
			List<Binding> newParentList = new ArrayList<Binding>();
			// reduce parent candidate bindings
			for (Binding binding : bindingsCouldBeParent) {
				if (binding.get((Var) serviceNodeOriginal).equals(
						serviceNodeSubstituted)) {
					newParentList.add(binding);
				}
			}
			bindingsCouldBeParent = newParentList;
		}
		return bindingsCouldBeParent;
	}

	private void fillBindingPairs(List<Binding> bindingList,
			List<BindingPair> bindingPairs,
			MultipleVariable multipleCommonVariable,
			HashMap<Integer, List<Binding>> parentMappings) {
		// iterate on each self binding and try to get parent bindings
		for (Binding binding : bindingList) {
			// generate hash code for the binding
			int multipleNodeHash = generateHashCode(multipleCommonVariable,
					binding);
			// get parent bindings for this binding
			List<Binding> parentBindings = parentMappings.get(multipleNodeHash);
			// check for these parents is not null
			if (parentBindings != null) {
				// create a binding pair for each parent binding
				for (Binding parentBinding : parentBindings) {
					bindingPairs.add(new BindingPair(binding, parentBinding));
				}
			}
		}
	}

	/**
	 * This method constructs {@link HashMap} instance that holds <hash code,
	 * binding list> pairs.
	 * 
	 * @param bindings
	 * @param multipleCommonVariable
	 * @return
	 */
	private HashMap<Integer, List<Binding>> constructBindingMappings(
			List<Binding> bindings, MultipleVariable multipleCommonVariable) {
		// define a hash map that contains <hashcode,bindings>
		// pairs
		HashMap<Integer, List<Binding>> bindingMappings = new HashMap<Integer, List<Binding>>();
		// iterate on bindings
		for (Binding binding : bindings) {
			int multipleNodeHash = generateHashCode(multipleCommonVariable,
					binding);
			// get current values contained by this hash value
			List<Binding> parentValues = bindingMappings.get(multipleNodeHash);
			// if there is no such value, create a new binding list and
			// add value and put the list, otherwise only add to the
			// binding list
			if (parentValues == null) {
				parentValues = new ArrayList<Binding>();
				parentValues.add(binding);
				bindingMappings.put(multipleNodeHash, parentValues);
			} else {
				if (!parentValues.contains(binding)) {
					parentValues.add(binding);
				}
			}
		}
		return bindingMappings;
	}

	private int generateHashCode(MultipleVariable multipleCommonVariable,
			Binding binding) {
		// creating a multiple node instance
		MultipleNode multipleNode = new MultipleNode();
		// add varaible values to multiple node
		for (Var var : multipleCommonVariable.getVars()) {
			multipleNode.addVariableValue(binding, var);
		}
		// generate hash code for this multiple node
		int multipleNodeHash = multipleNode.hashCode();
		return multipleNodeHash;
	}

	/**
	 * This method finds {@link BindingPair}s which are bindings and their
	 * parents.
	 * 
	 * @param multipleCommonVariable
	 * @param selfMap
	 * @param parentMap
	 * @param bindingList
	 * @param bindingsCouldBeParent
	 * @return
	 */
	private List<BindingPair> constructBindingPairs(
			MultipleVariable multipleCommonVariable,
			HashMap<MultipleVariable, List<MultipleNode>> selfMap,
			HashMap<MultipleVariable, List<MultipleNode>> parentMap,
			List<Binding> bindingList, List<Binding> bindingsCouldBeParent) {

		// define binding pair list
		List<BindingPair> bindingPairs = new ArrayList<BindingPair>();
		// get current binding nodes
		List<MultipleNode> selfMultipleNodes = selfMap
				.get(multipleCommonVariable);

		// get parent binding nodes
		List<MultipleNode> parentMultipleNodes = parentMap
				.get(multipleCommonVariable);

		// iterate on self nodes
		for (int selfIndex = 0; selfIndex < selfMultipleNodes.size(); selfIndex++) {
			MultipleNode selfMultipleNode = selfMultipleNodes.get(selfIndex);
			// get self binding
			Binding selfBinding = bindingList.get(selfIndex);
			// get parent index
			int parentIndex = parentMultipleNodes.indexOf(selfMultipleNode);
			if (parentIndex > -1) {
				// get parent binding
				Binding parentBinding = bindingsCouldBeParent.get(parentIndex);
				// add binding pair to the pair list
				bindingPairs.add(new BindingPair(selfBinding, parentBinding));
			} else
			// if self binding has no parent, create binding pairs with all
			// parent binding candidates
			{
				assignAllParents(bindingsCouldBeParent, bindingPairs,
						selfBinding);
			}
		}
		return bindingPairs;
	}

	/**
	 * This method takes given {@link Binding}, creates {@link BindingPair}s
	 * using this and all given parent {@link Binding} candidates, and adds
	 * these pairs to the binding pair list.
	 * 
	 * @param bindingsCouldBeParent
	 * @param bindingPairs
	 * @param selfBinding
	 */
	private void assignAllParents(List<Binding> bindingsCouldBeParent,
			List<BindingPair> bindingPairs, Binding selfBinding) {
		for (Binding bindingCouldBeParent : bindingsCouldBeParent) {
			bindingPairs
					.add(new BindingPair(selfBinding, bindingCouldBeParent));
		}
	}

	/**
	 * This method searchs common variables in given two {@link Var} list and
	 * returns them.
	 * 
	 * @param selfVars
	 * @param parentVars
	 * @return
	 */
	private MultipleVariable findCommonVariables(List<Var> selfVars,
			List<Var> parentVars) {
		MultipleVariable multipleVariable = new MultipleVariable();
		for (Var var : selfVars) {
			if (parentVars.contains(var)) {
				multipleVariable.add(var);
			}
		}
		return multipleVariable;
	}

	/**
	 * This method atomizes given {@link Binding} list according to their common
	 * variable variables
	 * 
	 * @param bindingList
	 * @return
	 */
	private HashMap<MultipleVariable, List<MultipleNode>> atomizeBindingVars(
			List<Binding> bindingList, MultipleVariable multipleCommonVariable) {
		// create hashmap containing variables and value list for this variables
		HashMap<MultipleVariable, List<MultipleNode>> map = new HashMap<MultipleVariable, List<MultipleNode>>();
		// iterate on these variables and fill list with this variable
		// values
		List<MultipleNode> multipleNodes = new ArrayList<MultipleNode>();
		for (Binding binding : bindingList) {
			MultipleNode multipleNode = new MultipleNode();
			for (Var commonVar : multipleCommonVariable.getVars()) {
				// add node value of binding to the node list
				multipleNode.addVariableValue(binding, commonVar);
			}
			multipleNodes.add(multipleNode);
		}
		map.put(multipleCommonVariable, multipleNodes);
		return map;
	}

	private Var currentVar;

	public Var getCurrentVar() {
		return currentVar;
	}

	public void setCurrentVar(Var currentVar) {
		this.currentVar = currentVar;
	}

	/**
	 * This method searchs parent {@link Binding} for a given {@link Binding}
	 * instance.
	 * 
	 * @param bindingsCouldBeParent
	 * @param binding
	 * @return
	 */
	private Binding findParentBinding(List<Binding> bindingsCouldBeParent,
			Binding binding) {
		Binding parent = null;
		for (Binding parentCandidate : bindingsCouldBeParent) {
			// iterate on binding variables
			Iterator<Var> varIter = binding.vars();
			while (varIter.hasNext()) {
				// check candidate is real parent
				parent = checkForParent(binding, parent, parentCandidate,
						varIter.next());
			}
		}
		return parent;
	}

	/**
	 * This method checks given parent candidate {@link Binding} is appropriate
	 * to be parent for self {@link Binding}
	 * 
	 * @param binding
	 * @param parent
	 * @param parentCandidate
	 * @param var
	 * @return
	 */
	private Binding checkForParent(Binding binding, Binding parent,
			Binding parentCandidate, Var var) {
		// get node value of binding for given variable
		Node bindingNode = binding.get(var);
		// get node value of candidate for given variable
		Node parentNode = parentCandidate.get(var);
		if (bindingNode != null && parentNode != null
				&& bindingNode.equals(parentNode)
				|| parentCandidate instanceof BindingRoot) {
			parent = parentCandidate;
		}
		return parent;
	}

}
