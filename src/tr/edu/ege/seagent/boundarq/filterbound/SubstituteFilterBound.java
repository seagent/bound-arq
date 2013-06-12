package tr.edu.ege.seagent.boundarq.filterbound;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tr.edu.ege.seagent.boundarq.util.Constants;
import tr.edu.ege.seagent.boundarq.util.UnexpectedOpTypeException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformCopy;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.op.Op2;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Substitute;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_LogicalOr;
import com.hp.hpl.jena.sparql.expr.E_OneOf;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;


public class SubstituteFilterBound extends Substitute {

	public static List<Op> substitute(Op op, List<Binding> bindingList,
			String filterType) {

		List<Op> opServices = new ArrayList<Op>();
		// Want to avoid cost if the binding is empty
		// but the empty test is not zero-cost on non-empty things.
		if (isNotNeeded(bindingList)) {
			opServices.add(op);
		} else {

			if (OpService.class.isInstance(op)) {
				OpService serviceOp = (OpService) op;

				// control whether binding is a service (In other words, service
				// has a variable (eg. SERVICE ?service0) and this binding is
				// the input for it)
				if (controlWhetherBindingIsServiceVariable(op, bindingList)) {
					Var serviceVar = (Var) ((OpService) op).getService();
					List<Node> serviceList = generateServiceList(bindingList,
							serviceOp);
					for (Node serviceNode : serviceList) {
						List<Binding> serviceSpecificBindings = constructServiceSpecificBindings(
								bindingList, serviceVar, serviceNode);
						// generate op for each services
						opServices.add(new OpService(serviceNode,
								constructOpFilter(serviceOp.getSubOp(),
										serviceSpecificBindings, filterType),
								serviceOp.getSilent()));
					}
				} else {
					opServices.add(new OpService(serviceOp.getService(),
							constructOpFilter(serviceOp.getSubOp(),
									bindingList, filterType), serviceOp
									.getSilent()));
				}
			}
		}
		return opServices;
	}

	private static List<Binding> constructServiceSpecificBindings(
			List<Binding> bindingList, Var serviceVar, Node serviceNode) {
		List<Binding> serviceSpecificBindings = new ArrayList<Binding>();
		for (Binding binding : bindingList) {
			if (binding.get(serviceVar).equals(serviceNode)) {
				serviceSpecificBindings.add(binding);
			}
		}
		return serviceSpecificBindings;
	}

	private static Op constructOpFilter(Op op, List<Binding> bindingList,
			String filterType) {
		if (filterType.equals(Constants.OR_FILTER)) {
			return construct_OR_FilterOp(op, bindingList);
		} else {
			return construct_IN_FilterOp(op, bindingList);
		}
	}

	/**
	 * Clean duplicate bindings
	 * 
	 * @param bindingList
	 * @return
	 */
	private static List<Binding> cleanBindingList(List<Binding> bindingList) {
		ArrayList<Binding> cleanedBindings = new ArrayList<Binding>();
		for (Binding binding : bindingList) {
			if (!cleanedBindings.contains(binding)) {
				cleanedBindings.add(binding);
			}
		}
		return cleanedBindings;
	}

	/**
	 * This method constructs OR version {@link OpFilter} using binding list and
	 * {@link Op} given
	 * 
	 * @param op
	 * @param bindingList
	 * @return
	 */
	private static Op construct_OR_FilterOp(Op op, List<Binding> bindingList) {
		Op finalOpToReturn = op;
		if (bindingList.isEmpty()) {
			return op;
		}

		// get variables to be filtered
		List<Var> appropriateVars = generateAppropriateVars(op, bindingList);
		List<Triple> addedList = new ArrayList<Triple>();
		List<Triple> allTriples = new ArrayList<Triple>();
		allTriples = findAllTriples(op, allTriples);

		for (Var var : appropriateVars) {
			// get triple that constructs a group with filter block
			Triple filterTriple = findTriple(op, var);
			if (filterTriple == null) {
				Expr exprBoundFilterOr = createBoundFilterOrExpression(
						bindingList, var);
				finalOpToReturn = OpFilter.filter(exprBoundFilterOr,
						finalOpToReturn);
			} else {
				// create an OpBGP with this triple
				OpBGP opBGP = generateOpWithOnlyAppropriateTriple(filterTriple);
				// add the processed triple to the list...
				addToList(filterTriple, addedList);
				// create bound FILTER expression...
				Expr exprBoundFilterOr = createBoundFilterOrExpression(
						bindingList, var);
				// decide final op to be returned.
				finalOpToReturn = decideFinalOpToReturn(finalOpToReturn,
						filterTriple, opBGP, exprBoundFilterOr, addedList,
						allTriples);
			}
		}
		// add remaining triples, which are remaining ones after adding some to
		// the filter blocks
		finalOpToReturn = addRemainingTriplesToFinalOp(op, finalOpToReturn,
				addedList, allTriples);
		return finalOpToReturn;
	}

	/**
	 * This method creates an OR expression which contains all binding values.
	 * 
	 * @param bindingList
	 *            list of binding values.
	 * @param var
	 *            variable which will be used in the FILTER block.
	 * @return created expression.
	 */
	private static Expr createBoundFilterOrExpression(
			List<Binding> bindingList, Var var) {
		// get first variable of binding
		ExprVar exprVar = new ExprVar(var);
		// create the first expression...
		Binding firstBinding = bindingList.get(0);
		Expr exprBefore = new E_Equals(exprVar, new NodeValueNode(
				firstBinding.get(var)));
		HashMap<Integer, Boolean> filterNodeMap = new HashMap<Integer, Boolean>();
		// create expression list to add binding values in filter...
		for (int i = 1; i < bindingList.size(); i++) {
			Node filterNode = bindingList.get(i).get(var);
			Boolean contained = filterNodeMap.get(filterNode.hashCode());
			if (contained == null) {
				filterNodeMap.put(filterNode.hashCode(), true);
				E_Equals exprAfter = new E_Equals(exprVar, new NodeValueNode(
						filterNode));
				exprBefore = new E_LogicalOr(exprBefore, exprAfter);
			}
		}
		return exprBefore;
	}

	/**
	 * Adds the given triple to the given list if the list does not contain it.
	 * 
	 * @param triple
	 *            the triple which will be added into the list.
	 * @param list
	 *            list to add the triple into.
	 */
	private static void addToList(Triple triple, List<Triple> list) {
		if (!list.contains(triple)) {
			list.add(triple);
		}
	}

	/**
	 * This method constructs IN version {@link OpFilter} using binding list and
	 * {@link Op} given
	 * 
	 * @param op
	 * @param bindingList
	 * @return
	 */
	private static Op construct_IN_FilterOp(Op op, List<Binding> bindingList) {
		Op finalOpToReturn = op;
		// get variable of first element in binding list which are candidate for
		// substituting
		// List<Var> varsOfBinding = getVariablesOfBinding(bindingList);
		if (bindingList.isEmpty()) {
			return op;
		}
		List<Var> appropriateVars = generateAppropriateVars(op, bindingList);
		List<Triple> addedList = new ArrayList<Triple>();
		List<Triple> allTriples = new ArrayList<Triple>();
		allTriples = findAllTriples(op, allTriples);
		for (Var var : appropriateVars) {
			// get triple that constructs a group with filter block
			Triple filterTriple = findTriple(op, var);
			// create an OpBGP with this triple
			OpBGP opBGP = generateOpWithOnlyAppropriateTriple(filterTriple);
			// get first variable of binding
			ExprVar exprVar = new ExprVar(var);
			// create expression list to add binding values in filter
			ExprList exprList = new ExprList();
			for (int i = 0; i < bindingList.size(); i++) {
				Binding binding = bindingList.get(i);
				// get binding value
				Node bindingValue = binding.get(var);
				if (bindingValue != null) {
					// add binding value with an instance of node value node
					exprList.add(new NodeValueNode(bindingValue));
				}
			}
			// check whether expressin list to construct filter op is not empty
			if (!exprList.isEmpty()) {
				// create a one of logic filter (In filter)
				E_OneOf eOneOf = new E_OneOf(exprVar, exprList);
				// create a filter op using this logic and op before.
				// decide final op to be returned.
				finalOpToReturn = decideFinalOpToReturn(finalOpToReturn,
						filterTriple, opBGP, eOneOf, addedList, allTriples);
			}
		}
		// add remaining triples, which are remaining ones after adding some
		// to the filter blocks
		finalOpToReturn = addRemainingTriplesToFinalOp(op, finalOpToReturn,
				addedList, allTriples);
		return finalOpToReturn;
	}

	private static List<Triple> findAllTriples(Op op, List<Triple> allTriples) {
		if (op instanceof OpBGP) {
			OpBGP opBGP = ((OpBGP) op);
			List<Triple> tripleList = opBGP.getPattern().getList();
			for (Triple triple : tripleList) {
				addToList(triple, allTriples);
			}
		} else if (op instanceof Op2) {
			Op2 opJoin = (Op2) op;
			allTriples = findAllTriples(opJoin.getLeft(), allTriples);
			if (allTriples == null || allTriples.isEmpty()) {
				allTriples = findAllTriples(opJoin.getRight(), allTriples);
			}
			return allTriples;
		} else if (op instanceof OpFilter) {
			OpFilter opFilter = (OpFilter) op;
			allTriples = findAllTriples(opFilter.getSubOp(), allTriples);
		} else if (op instanceof OpTable) {
			allTriples = new ArrayList<Triple>();
		} else {
			throw new UnexpectedOpTypeException(
					createUnexpectedOpTypeExceptionMessage(op));
		}
		return allTriples;
	}

	private static String createUnexpectedOpTypeExceptionMessage(Op op) {
		String opName = op.getClass().getSimpleName();
		return MessageFormat.format("\"{0}\" is not supported.", opName);
	}

	/**
	 * This method constructs new {@link OpBGP} filled with remaining triples
	 * and merges this with final {@link Op}
	 * 
	 * @param op
	 * @param finalOpToReturn
	 * @param addedList
	 * @param allTriples
	 * @return
	 */
	private static Op addRemainingTriplesToFinalOp(Op op, Op finalOpToReturn,
			List<Triple> addedList, List<Triple> allTriples) {
		if (addedList.isEmpty()) {
			return finalOpToReturn;
		}
		if (op instanceof OpBGP) {
			List<Triple> tripleList = ((OpBGP) op).getPattern().getList();
			BasicPattern basicPattern = new BasicPattern();
			if (tripleList.size() > addedList.size()) {
				OpBGP remainingOp = fillWithTriples(addedList, tripleList,
						basicPattern);
				// merge two op
				finalOpToReturn = OpJoin.create(finalOpToReturn, remainingOp);
			}
		} else if (op instanceof OpJoin) {
			OpJoin opJoin = (OpJoin) op;
			finalOpToReturn = addRemainingTriplesToFinalOp(opJoin.getLeft(),
					finalOpToReturn, addedList, allTriples);
			finalOpToReturn = addRemainingTriplesToFinalOp(opJoin.getRight(),
					finalOpToReturn, addedList, allTriples);
		} else {
			finalOpToReturn = OpJoin.create(finalOpToReturn, op);
		}
		return finalOpToReturn;
	}

	private static OpBGP fillWithTriples(List<Triple> addedList,
			List<Triple> tripleList, BasicPattern basicPattern) {
		// fill basic pattern with remaining triples
		for (Triple triple : tripleList) {
			if (!addedList.contains(triple)) {
				basicPattern.add(triple);
			}
		}
		// create new OpBGP with basic pattern
		OpBGP remainingOp = new OpBGP(basicPattern);
		return remainingOp;
	}

	/**
	 * This method decides final {@link Op} to be returned.
	 * 
	 * @param finalOpToReturn
	 * @param filterTriple
	 * @param opBGP
	 * @param filterExpr
	 * @param allTriples
	 * @param addedList
	 * @return
	 */
	private static Op decideFinalOpToReturn(Op finalOpToReturn,
			Triple filterTriple, OpBGP opBGP, Expr filterExpr,
			List<Triple> addedList, List<Triple> allTriples) {

		// check whether current filter expression is used before
		if (doesFilterExpressionContainedBefore(finalOpToReturn, filterExpr)) {
			return finalOpToReturn;
		}
		// create a filter op using this logic and opBGP that contains only
		// one triple.
		Op opFilter = OpFilter.filter(filterExpr, opBGP);
		// check found filter-block-triple contained by finalOpToReturn
		// before.
		if (!doesOpContainTriple(finalOpToReturn, filterTriple)) {
			// if the triple is not contained (means that this triple is
			// different from triples filtered before), create Join op with
			// finalOpToReturn and opFilter before.
			finalOpToReturn = OpJoin.create(finalOpToReturn, opFilter);
		} else {
			// if triple be contained (means new filter variable is included in
			// same triple whose another variable is filtered before), create
			// Filter op using before final op.
			if (finalOpToReturn instanceof OpFilter) {
				OpFilter opFilterMain = ((OpFilter) finalOpToReturn);
				Op subOp = opFilterMain.getSubOp();
				if (subOp instanceof OpBGP) {
					finalOpToReturn = organizeDoubleFilterBlock(addedList,
							opFilter, opFilterMain);
				} else {
					finalOpToReturn = OpJoin.create(opFilter, finalOpToReturn);
				}
				// finalOpToReturn = OpFilter.filter(filterExpr,
				// finalOpToReturn);
			} else {
				// if no filter op defined before finalOpToReturn is now
				// opFilter
				finalOpToReturn = opFilter;
			}
		}
		return finalOpToReturn;
	}

	/**
	 * This method organizes double filter block and generates the logical
	 * {@link Op} appropriately.
	 * 
	 * @param addedList
	 *            triples that contained in binding filter block
	 * @param opFilterBinding
	 *            {@link OpFilter} that has been generated with binding values
	 * @param opFilterMain
	 *            main {@link OpFilter}
	 * @return
	 */
	private static Op organizeDoubleFilterBlock(List<Triple> addedList,
			Op opFilterBinding, OpFilter opFilterMain) {
		// first get sub op of main filter op
		OpBGP opBGPFilter = ((OpBGP) opFilterMain.getSubOp());
		// create a basic pattern for new main op filter
		BasicPattern basicPattern = new BasicPattern();
		// fill this new opFilter with triples that are not contained in added
		// list
		fillBasicPatternWithNotAddedTriples(addedList, opBGPFilter,
				basicPattern);
		// check whether main filter block contains another triple different
		// from added list
		if (!basicPattern.isEmpty()) {
			OpBGP newOpBGP = new OpBGP(basicPattern);
			Op newOpFilterMain = OpFilter.filter(opFilterMain.getExprs(),
					newOpBGP);
			return OpJoin.create(opFilterBinding, newOpFilterMain);
		} else {
			return OpFilter.filter(opFilterMain.getExprs(), opFilterBinding);
		}
	}

	private static void fillBasicPatternWithNotAddedTriples(
			List<Triple> addedList, OpBGP opBGPFilter, BasicPattern basicPattern) {
		for (Triple triple : opBGPFilter.getPattern().getList()) {
			if (!addedList.contains(triple)) {
				basicPattern.add(triple);
			}
		}
	}

	/**
	 * This method checks whether given filter {@link Expr} is contained before
	 * given {@link Op}
	 * 
	 * @param op
	 * @param filterExpr
	 * @return
	 */
	private static boolean doesFilterExpressionContainedBefore(Op op,
			Expr filterExpr) {
		if (op instanceof OpJoin) {
			boolean satisfied = doesFilterExpressionContainedBefore(
					((OpJoin) op).getLeft(), filterExpr);
			if (!satisfied) {
				return doesFilterExpressionContainedBefore(
						((OpJoin) op).getLeft(), filterExpr);
			}
		} else if (op instanceof OpFilter) {
			return ((OpFilter) op).getExprs().getList().contains(filterExpr);
		}
		return false;
	}

	/**
	 * This method checks whether given op contains given triple.
	 * 
	 * @param op
	 * @param triple
	 * @return
	 */
	private static boolean doesOpContainTriple(Op op, Triple triple) {
		if (op instanceof OpBGP) {
			return ((OpBGP) op).getPattern().getList().contains(triple);
		} else if (op instanceof OpFilter) {
			return doesOpContainTriple(((OpFilter) op).getSubOp(), triple);
		} else if (op instanceof OpTable) {
			return false;
		} else {
			Op2 opJoin = (Op2) op;
			boolean contained = doesOpContainTriple(opJoin.getLeft(), triple);
			if (!contained) {
				contained = doesOpContainTriple(opJoin.getRight(), triple);
			}
			return contained;
		}
	}

	/**
	 * This method creates an {@link OpBGP} instance contains only triple that
	 * includes variable to be filtered.
	 * 
	 * @param filterTriple
	 *            {@link Triple} that contains filter variable
	 * 
	 * @return
	 */
	private static OpBGP generateOpWithOnlyAppropriateTriple(Triple filterTriple) {
		// create a new basic pattern that contains single triple
		BasicPattern basicPattern = new BasicPattern();
		// add triple
		basicPattern.add(filterTriple);
		// create a new OpBGP with basic pattern and return.
		return new OpBGP(basicPattern);
	}

	/**
	 * This method finds triple that contains filtered variable.
	 * 
	 * @param op
	 * @param var
	 * @return
	 */
	private static Triple findTriple(Op op, Var var) {
		if (op instanceof OpBGP) {
			OpBGP opBGP = ((OpBGP) op);
			List<Triple> tripleList = opBGP.getPattern().getList();
			for (Triple triple : tripleList) {
				if (triple.getSubject().isVariable()
						&& triple.getSubject().equals(var)) {
					return triple;
				}
				if (triple.getPredicate().isVariable()
						&& triple.getPredicate().equals(var)) {
					return triple;
				}
				if (triple.getObject().isVariable()
						&& triple.getObject().equals(var)) {
					return triple;
				}
			}
		} else if (op instanceof Op2) {
			Op2 opJoin = (Op2) op;
			Triple triple = findTriple(opJoin.getLeft(), var);
			if (triple == null) {
				triple = findTriple(opJoin.getRight(), var);
			}
			return triple;
		} else if (op instanceof OpFilter) {
			OpFilter opFilter = (OpFilter) op;
			return findTriple(opFilter.getSubOp(), var);
		} else if (op instanceof OpTable) {
			// return nothing...
		} else {
			throw new UnexpectedOpTypeException(
					createUnexpectedOpTypeExceptionMessage(op));
		}
		return null;
	}

	/**
	 * This method finds appropriate variables which will be used in FILTER
	 * block.
	 * 
	 * @param op
	 * @param bindingList
	 * @return
	 */
	private static List<Var> generateAppropriateVars(Op op,
			List<Binding> bindingList) {
		List<Var> appropraiteVars = new ArrayList<Var>();
		// get first binding
		Binding bindingFirst = bindingList.get(0);
		OpBGP opBGP = null;
		if (op instanceof OpFilter) {
			OpFilter opfilter = (OpFilter) op;
			opBGP = (OpBGP) opfilter.getSubOp();
			Set<Var> varsMentioned = opfilter.getExprs().getVarsMentioned();
			for (Var var : varsMentioned) {
				if (isVariableAppropriateToBeSubstituted(var, bindingFirst)) {
					appropraiteVars.add(var);
				}
			}
		} else if (op instanceof OpBGP) {
			opBGP = (OpBGP) op;
		} else if (op instanceof OpTable) {
			opBGP = new OpBGP(new BasicPattern());
		} else {
			Op2 opJoin = (Op2) op;
			appropraiteVars = generateAppropriateVars(opJoin.getLeft(),
					bindingList);
			List<Var> varsRight = generateAppropriateVars(opJoin.getRight(),
					bindingList);
			for (Var var : varsRight) {
				if (!appropraiteVars.contains(var)) {
					appropraiteVars.add(var);
				}
			}
			return appropraiteVars;

		}
		List<Triple> tripleList = opBGP.getPattern().getList();
		for (Triple triple : tripleList) {
			fillVariablesToSubstitute(appropraiteVars, triple.getSubject(),
					bindingFirst);
			fillVariablesToSubstitute(appropraiteVars, triple.getPredicate(),
					bindingFirst);
			fillVariablesToSubstitute(appropraiteVars, triple.getObject(),
					bindingFirst);
		}
		return appropraiteVars;
	}

	private static void fillVariablesToSubstitute(List<Var> appropraiteVars,
			Node subject, Binding bindingFirst) {
		if (isVariableAppropriateToBeSubstituted(subject, bindingFirst)) {
			appropraiteVars.add((Var) subject);
		}
	}

	private static boolean isVariableAppropriateToBeSubstituted(Node node,
			Binding binding) {
		return !substitute(node, binding).equals(node);
	}

	/**
	 * This method controls whether any binding in given binding list is service
	 * 
	 * @param op
	 * @param bindingList
	 * @return
	 */
	private static boolean controlWhetherBindingIsServiceVariable(Op op,
			List<Binding> bindingList) {
		// try to cast Op value to OpService
		OpService opService = null;
		if (op.getClass().asSubclass(OpService.class).equals(OpService.class)) {
			opService = (OpService) op;
		}
		// if casting is successful control service is variable and service
		// variable is binding variable
		if (opService != null && isServiceVariable(opService)
				&& bindingList.size() > 0) {
			BindingMap binding = (BindingMap) bindingList.get(0);
			List<Var> varList = generateVarList(binding.vars());
			Var var = varList.get(0);
			Node serviceNode = binding.get(var);
			if (serviceNode != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method creates variable List
	 * 
	 * @param varIter
	 * @return
	 */
	private static List<Var> generateVarList(Iterator<Var> varIter) {
		List<Var> varList = new ArrayList<Var>();
		if (varIter.hasNext()) {
			Var var = (Var) varIter.next();
			varList.add(var);
		}
		return varList;
	}

	/**
	 * This method controls whether given opService is variable or not.
	 * 
	 * @param opService
	 * 
	 * @return
	 */
	private static boolean isServiceVariable(OpService opService) {
		if (opService.getService().isURI()) {
			return false;
		}
		return true;
	}

	/**
	 * This method creates service value {@link Node} list searching in given
	 * bindingList.
	 * 
	 * @param bindingList
	 * @param serviceOp
	 * @return
	 */
	private static List<Node> generateServiceList(List<Binding> bindingList,
			OpService serviceOp) {
		List<Node> services = new ArrayList<Node>();
		for (int i = 0; i < bindingList.size(); i++) {
			BindingMap binding = (BindingMap) bindingList.get(i);
			// get service value
			Node serviceValue = binding.get((Var) serviceOp.getService());
			// add this service if services doesn't contain it.
			if (!services.contains(serviceValue)) {
				services.add(serviceValue);
			}
		}
		return services;
	}

	/**
	 * TODO: birden fazla variable içerebilir mi? This method retrieves first
	 * variable (a binding already contains only one variable) of first
	 * {@link Binding} in given binding {@link List}
	 * 
	 * @param bindings
	 * @return
	 */
	private static List<Var> getVariablesOfBinding(List<Binding> bindings) {
		List<Var> varList = new ArrayList<Var>();
		if (bindings != null && !bindings.isEmpty()) {
			Binding binding = bindings.get(0);
			Iterator<Var> vars = binding.vars();
			while (vars.hasNext()) {
				varList.add(vars.next());
			}
		}
		return varList;
	}

	/**
	 * This method generates substituted {@link Op} list
	 * 
	 * @param op
	 * @param bindingList
	 * @return
	 */
	private static List<Op> generateSubstitutedOps(Op op,
			List<Binding> bindingList) {
		List<Op> substitutedOpList = new ArrayList<Op>();
		for (Binding binding : bindingList) {
			// substitute each binding and generate substituted op list
			substitutedOpList.add(Transformer.transform(new OpSubstituteWorker(
					binding), op));
		}
		return substitutedOpList;
	}

	private static boolean isNotNeeded(List<Binding> bindingList) {
		for (Binding binding : bindingList) {
			if (!binding.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	// ----
	private static class OpSubstituteWorker extends TransformCopy {
		private Binding binding;

		public OpSubstituteWorker(Binding binding) {
			super(TransformCopy.COPY_ALWAYS);
			this.binding = binding;
		}

		@Override
		public Op transform(OpBGP opBGP) {
			BasicPattern bgp = opBGP.getPattern();
			bgp = substitute(bgp, binding);
			return new OpBGP(bgp);
		}

		@Override
		public Op transform(OpQuadPattern quadPattern) {
			Node gNode = quadPattern.getGraphNode();
			Node g = substitute(gNode, binding);

			BasicPattern triples = new BasicPattern();
			for (Triple triple : quadPattern.getBasicPattern()) {
				Node s = substitute(triple.getSubject(), binding);
				Node p = substitute(triple.getPredicate(), binding);
				Node o = substitute(triple.getObject(), binding);
				Triple t = new Triple(s, p, o);
				triples.add(t);
			}

			// Pure quading.
			// for ( Iterator iter = quadPattern.getQuads().iterator() ;
			// iter.hasNext() ; )
			// {
			// Quad quad = (Quad)iter.next() ;
			// if ( ! quad.getGraph().equals(gNode) )
			// throw new
			// ARQInternalErrorException("Internal error: quads block is not uniform over the graph node")
			// ;
			// Node s = substitute(quad.getSubject(), binding) ;
			// Node p = substitute(quad.getPredicate(), binding) ;
			// Node o = substitute(quad.getObject(), binding) ;
			// Triple t = new Triple(s, p, o) ;
			// triples.add(t) ;
			// }

			return new OpQuadPattern(g, triples);
		}

		@Override
		public Op transform(OpPath opPath) {
			return new OpPath(substitute(opPath.getTriplePath(), binding));
		}

		@Override
		public Op transform(OpPropFunc opPropFunc, Op subOp) {
			PropFuncArg sArgs = opPropFunc.getSubjectArgs();
			PropFuncArg oArgs = opPropFunc.getObjectArgs();

			PropFuncArg sArgs2 = substitute(sArgs, binding);
			PropFuncArg oArgs2 = substitute(oArgs, binding);

			if (sArgs2 == sArgs && oArgs2 == oArgs
					&& opPropFunc.getSubOp() == subOp)
				return super.transform(opPropFunc, subOp);
			return new OpPropFunc(opPropFunc.getProperty(), sArgs2, oArgs2,
					subOp);
		}

		@Override
		public Op transform(OpFilter filter, Op op) {
			ExprList exprs = filter.getExprs().copySubstitute(binding);
			if (exprs == filter.getExprs())
				return filter;
			return OpFilter.filter(exprs, op);
		}

		@Override
		public Op transform(OpAssign opAssign, Op subOp) {
			VarExprList varExprList2 = transformVarExprList(opAssign
					.getVarExprList());
			if (varExprList2.isEmpty())
				return subOp;
			return OpAssign.assign(subOp, varExprList2);
		}

		@Override
		public Op transform(OpExtend opExtend, Op subOp) {
			VarExprList varExprList2 = transformVarExprList(opExtend
					.getVarExprList());
			if (varExprList2.isEmpty())
				return subOp;

			return OpExtend.extend(subOp, varExprList2);
		}

		private VarExprList transformVarExprList(VarExprList varExprList) {
			VarExprList varExprList2 = new VarExprList();
			for (Var v : varExprList.getVars()) {
				// if ( binding.contains(v))
				// // Already bound. No need to do anything because the
				// // logical assignment will test value.
				// continue ;
				Expr expr = varExprList.getExpr(v);
				expr = expr.copySubstitute(binding);
				varExprList2.add(v, expr);
			}
			return varExprList2;
		}

		// The expression?
		// public Op transform(OpLeftJoin opLeftJoin, Op left, Op right) {
		// return xform(opLeftJoin, left, right) ; }

		@Override
		public Op transform(OpGraph op, Op sub) {
			Node n = substitute(op.getNode(), binding);
			return new OpGraph(n, sub);
		}

		@Override
		public Op transform(OpService op, Op sub) {
			Node n = substitute(op.getService(), binding);
			return new OpService(n, sub, op.getSilent());
		}
	}
}
