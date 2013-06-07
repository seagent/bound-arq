package tr.edu.ege.seagent.boundarq.unionbound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import tr.edu.ege.seagent.boundarq.util.NumericBinding;
import tr.edu.ege.seagent.boundarq.util.VariablePair;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.TransformCopy;
import com.hp.hpl.jena.sparql.algebra.Transformer;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.core.Substitute;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingFactory;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;



public class SubstituteUnionBound extends Substitute {

	private static final String QUESTION_MARK = "?";

	private static final String CLOSING_PHARANTESIS = ")";

	private static final String SPACE = " ";

	private static final String FILTER_KEYWORD = "FILTER";

	private static int UNION_SIZE = 0;

	private static int filterIndex = 0;

	public static int getUnionSize() {
		return UNION_SIZE;
	}

	public static void setUnionSize(int unionSize) {
		UNION_SIZE = unionSize;
	}

	/**
	 * This method substitutes variables with appropriate bindings
	 * 
	 * @param op
	 * @param bindingList
	 * @return
	 */
	public static List<Op> substitute(Op op, List<Binding> bindingList) {
		List<Op> opList = new ArrayList<Op>();
		// Want to avoid cost if the binding is empty
		// but the empty test is not zero-cost on non-empty things.
		if (isNotNeeded(bindingList)) {
			opList.add(op);
		} else {
			// get subop, substitute for all bindings and construct union
			// query
			if (OpService.class.isInstance(op)) {
				OpService serviceOp = (OpService) op;

				List<NumericBinding> numericBindings = constructNumericBindingList(bindingList);

				// control whether binding is a service (In other words, service
				// has
				// a variable (eg. SERVICE ?service0) and this binding is the
				// input
				// for it)
				if (controlWhetherBindingIsService(op, bindingList)) {
					List<Node> serviceList = generateServiceList(bindingList,
							serviceOp);
					List<List<NumericBinding>> unionGroups = generateUnionGroups(
							numericBindings, serviceOp, serviceList);
					for (int i = 0; i < unionGroups.size(); i++) {
						List<NumericBinding> unionGroup = unionGroups.get(i);
						Node serviceValue = serviceList.get(i);
						generateOpServices(serviceOp, unionGroup, opList,
								serviceValue);
					}
				} else {
					generateOpServices(serviceOp, numericBindings, opList,
							serviceOp.getService());
				}

				// opList.add(Transformer.transform(new OpSubstituteWorkerBound(
				// bindingList), op));
			}
		}
		filterIndex = 0;
		return opList;
	}

	/**
	 * This method constructs a {@link List} of {@link NumericBinding}s as with
	 * binding in order of list and thier order number
	 * 
	 * @param bindingList
	 *            to construct numeric bindings
	 * @return {@link List} of {@link NumericBinding}s
	 */
	private static List<NumericBinding> constructNumericBindingList(
			List<Binding> bindingList) {
		List<NumericBinding> numericBindings = new ArrayList<NumericBinding>();
		for (int i = 0; i < bindingList.size(); i++) {
			Binding binding = bindingList.get(i);
			// add binding and its index order
			numericBindings.add(new NumericBinding(binding, i));
		}
		return numericBindings;
	}

	/**
	 * This method splits services as bindings would be ask to them.
	 * 
	 * @param bindingList
	 * @param serviceOp
	 * @param serviceList
	 * @return
	 */
	private static List<List<NumericBinding>> generateUnionGroups(
			List<NumericBinding> bindingList, OpService serviceOp,
			List<Node> serviceList) {
		List<List<NumericBinding>> bindingUnionGroups = new ArrayList<List<NumericBinding>>();
		// generate Ops for each service value
		for (int j = 0; j < serviceList.size(); j++) {
			List<NumericBinding> bindingGroupToUnion = new ArrayList<NumericBinding>();
			Node currentService = serviceList.get(j);
			for (int i = 0; i < bindingList.size(); i++) {
				BindingMap binding = (BindingMap) bindingList.get(i)
						.getBinding();
				Node serviceValue = binding.get((Var) serviceOp.getService());
				// if this serviceValue same as one with service list add it to
				// binding group.
				if (currentService.equals(serviceValue)) {
					bindingGroupToUnion.add(bindingList.get(i));
				}
			}
			bindingUnionGroups.add(bindingGroupToUnion);
		}
		return bindingUnionGroups;
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
	 * This method creates {@link OpService} list using bindingList and given
	 * service value.
	 * 
	 * @param serviceOp
	 * @param bindingList
	 * @param opList
	 * @param serviceValue
	 */
	private static void generateOpServices(OpService serviceOp,
			List<NumericBinding> bindingList, List<Op> opList, Node serviceValue) {
		// get sub op...
		Op serviceRootOp = serviceOp.getSubOp();
		// substitute sub op for each binding...
		List<Op> substitutedOpList = generateSubstitutedOps(bindingList,
				serviceRootOp);
		// control substituted op list size is greater than 1 to
		// decide
		// whether unioning ops is needed or not.
		if (substitutedOpList.size() > 1) {

			// generate op unions and add them to op list
			generateOpUnions(opList, substitutedOpList, serviceValue);
		} else {
			// create single op and add it to list
			opList.add(new OpService(serviceValue, substitutedOpList.get(0),
					false));
		}
	}

	/**
	 * This method unions ops and then create an opservice with union op and
	 * service value
	 * 
	 * @param opList
	 * @param substitutedOpList
	 * @param serviceValue
	 */
	private static void generateOpUnions(List<Op> opList,
			List<Op> substitutedOpList, Node serviceValue) {
		Op previousOp = substitutedOpList.get(0);
		Op opUnion = null;
		// union substituted ops...
		for (int i = 1; i < substitutedOpList.size(); i++) {
			opUnion = OpUnion.create(previousOp, substitutedOpList.get(i));
			previousOp = opUnion;
			// create ops when there is achieved to given UNION size
			// because of size restrictions for UNION
			// operation (big union operations are not allowed)
			if (i % UNION_SIZE == 0 && i + 1 < substitutedOpList.size()) {
				// creating new Opservice with union op and
				// given service value
				opList.add(new OpService(serviceValue, opUnion, false));
				// resetting opUnion and previous op to create
				// remaining ops.
				opUnion = null;
				previousOp = null;
			} else if (i + 1 == substitutedOpList.size()) {
				// there could be remaining elements which is
				// not mod of given number
				opList.add(new OpService(serviceValue, opUnion, false));
			}
		}
	}

	/**
	 * This method substituted bindings with their variable values and generates
	 * an substituted {@link Op} list.
	 * 
	 * @param numericBindingList
	 * @param serviceRootOp
	 * @return list of {@link Op}s consisting substituted bidnings.
	 */
	private static List<Op> generateSubstitutedOps(
			List<NumericBinding> numericBindingList, Op serviceRootOp) {
		List<Op> substitutedOpList = new Vector<Op>();
		for (int i = 0; i < numericBindingList.size(); i++) {
			Binding binding = numericBindingList.get(i).getBinding();

			// substitute numeric op
			Op substitutedOp = Transformer.transform(
					new OpSubstituteWorkerRegular(binding, numericBindingList
							.get(i).getBindingNo()), serviceRootOp);

			// construct numeric OpFilter instance if given subistituted op is
			// instance of OpFilter
			Op numericOpFilter = constructNumericOpFilter(i, substitutedOp);

			// add numeric op filter to list
			substitutedOpList.add(numericOpFilter);
		}
		return substitutedOpList;
	}

	/**
	 * This method constructs numeric {@link OpFilter} instance with given index
	 * value and using given substituted {@link OpFilter} instance
	 * 
	 * @param index
	 *            to add all variables in filter block
	 * @param substitutedOp
	 *            {@link OpFilter} instance whose filter block will be turned
	 *            into numeric one.
	 * @return numeric {@link OpFilter} instance
	 */
	private static Op constructNumericOpFilter(int index, Op substitutedOp) {
		Op numericOpFilter = substitutedOp;

		// check whether op instance is an OpFilter
		if (substitutedOp instanceof OpFilter) {
			// turn OpFilter into query
			Query query = OpAsQuery.asQuery(substitutedOp);

			// System.out.println("Query before: " + query);

			// assign index
			final int finalIndex = index;
			// construct numeric filter block
			List<VariablePair> variablePairs = constructVariablePairs(query,
					finalIndex);
			// get query as text
			String queryStr = query.toString();
			// split query and get filter including second part of query text
			String[] splittedQuery = queryStr.split(FILTER_KEYWORD);
			// replace variables of given text
			String filterIncludingPart = replaceVariables(variablePairs,
					splittedQuery[1]);

			// construct final query
			String finalQueryText = splittedQuery[0] + FILTER_KEYWORD
					+ filterIncludingPart;

			// System.out.println("Query after: " + finalQueryText);

			// create plan for query and get OpFilter instance from it
			numericOpFilter = QueryExecutionFactory.createPlan(
					QueryFactory.create(finalQueryText),
					DatasetGraphFactory.createMem(), null).getOp();
		}
		return numericOpFilter;
	}

	/**
	 * This method replaces all old variables containing replace text with new
	 * variables in {@link VariablePair} list
	 * 
	 * @param variablePairs
	 * @param replaceText
	 * @return
	 */
	private static String replaceVariables(List<VariablePair> variablePairs,
			String replaceText) {
		String filterIncludingPart = replaceText;
		// replace all old variables with new ones.
		for (VariablePair variablePair : variablePairs) {
			filterIncludingPart = replaceWithFinalForm(filterIncludingPart,
					variablePair, SPACE);
			filterIncludingPart = replaceWithFinalForm(filterIncludingPart,
					variablePair, CLOSING_PHARANTESIS);
		}
		return filterIncludingPart;
	}

	/**
	 * Replace non numeric variables in filter block with numeric ones
	 * appropriately.
	 * 
	 * @param replaceText
	 * @param variablePair
	 * @param endCharacter
	 * @return
	 */
	private static String replaceWithFinalForm(String replaceText,
			VariablePair variablePair, String endCharacter) {
		String oldVariableFinalForm = QUESTION_MARK
				+ variablePair.getOldVariable() + endCharacter;
		String newVariableFinalForm = QUESTION_MARK
				+ variablePair.getReplacingVariable() + endCharacter;
		return replaceText.replace(oldVariableFinalForm, newVariableFinalForm);
	}

	/**
	 * It controls whether replace text contains variable appropriately
	 * 
	 * @param replaceText
	 * @param variableToReplace
	 * @return
	 */
	private static boolean isReplaceAvailable(String replaceText,
			String variableToReplace) {
		return replaceText.contains(QUESTION_MARK + variableToReplace + SPACE)
				|| replaceText.contains(QUESTION_MARK + variableToReplace
						+ CLOSING_PHARANTESIS);
	}

	/**
	 * This method constructs {@link VariablePair} list with old and numeric
	 * ones
	 * 
	 * @param query
	 * @param finalIndex
	 */
	private static List<VariablePair> constructVariablePairs(Query query,
			final int finalIndex) {
		final List<VariablePair> variablePairs = new ArrayList<VariablePair>();
		ElementVisitorBase visitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementFilter el) {
				Set<Var> varsMentioned = el.getExpr().getVarsMentioned();
				for (Var var : varsMentioned) {
					Node numericVariable = Var.createVariable(var.getName()
							+ "_" + finalIndex);
					variablePairs.add(new VariablePair(var.getName(),
							numericVariable.getName()));
				}
				super.visit(el);
			}
		};
		ElementWalker.walk(query.getQueryPattern(), visitor);
		return variablePairs;
	}

	/**
	 * This method controls whether any binding in given binding list is service
	 * 
	 * @param op
	 * @param bindingList
	 * @return
	 */
	private static boolean controlWhetherBindingIsService(Op op,
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

	public static Op substitute(Op op, Var var, Node node) {
		Binding b = BindingFactory.binding(var, node);
		return substitute(op, b);
	}

	/**
	 * Puts binding values to the appripriate places in the given BGP, and also
	 * adds indexes to variable names.
	 * 
	 * @param bgp
	 * @param binding
	 * @param index
	 * @return
	 */
	public static BasicPattern substitute(BasicPattern bgp, Binding binding,
			int index) {
		if (isNotNeeded(binding))
			return bgp;

		BasicPattern bgp2 = new BasicPattern();
		// BGP deki tüm triple lar dolaşılıyor yerine koymak adına
		for (Triple triple : bgp) {
			// Binding listteki bindingler de UNION lanmak için dolaşılıyor.
			Triple t = substitute(triple, binding, index);
			bgp2.add(t);
		}
		return bgp2;
	}

	public static Triple substitute(Triple triple, Binding binding, int index) {
		if (isNotNeeded(binding))
			return triple;

		Node s = triple.getSubject();
		Node p = triple.getPredicate();
		Node o = triple.getObject();

		Node s1 = substitute(s, binding);
		Node p1 = substitute(p, binding);
		Node o1 = substitute(o, binding);

		Triple t = triple;
		s1 = generateCountableNode(index, s, s1);
		p1 = generateCountableNode(index, p, p1);
		o1 = generateCountableNode(index, o, o1);
		t = new Triple(s1, p1, o1);

		return t;
	}

	private static Node generateCountableNode(int count, Node node1, Node node2) {
		if (node2 == node1 && node2.isVariable()) {
			node2 = Var.alloc(node2.getName() + "_" + count);
		}
		return node2;
	}

	public static TriplePath substitute(TriplePath triplePath, Binding binding) {
		if (triplePath.isTriple())
			return new TriplePath(Substitute.substitute(triplePath.asTriple(),
					binding));

		Node s = triplePath.getSubject();
		Node o = triplePath.getObject();
		Node s1 = substitute(s, binding);
		Node o1 = substitute(o, binding);

		TriplePath tp = triplePath;
		if (s1 != s || o1 != o)
			tp = new TriplePath(s1, triplePath.getPath(), o1);
		return tp;
	}

	public static Quad substitute(Quad quad, Binding binding) {
		if (isNotNeeded(binding))
			return quad;

		Node g = quad.getGraph();
		Node s = quad.getSubject();
		Node p = quad.getPredicate();
		Node o = quad.getObject();

		Node g1 = substitute(g, binding);
		Node s1 = substitute(s, binding);
		Node p1 = substitute(p, binding);
		Node o1 = substitute(o, binding);

		Quad q = quad;
		if (s1 != s || p1 != p || o1 != o || g1 != g)
			q = new Quad(g1, s1, p1, o1);
		return q;
	}

	public static Node substitute(Node n, Binding b) {
		return Var.lookup(b, n);
	}

	public static PropFuncArg substitute(PropFuncArg propFuncArg,
			Binding binding) {
		if (isNotNeeded(binding))
			return propFuncArg;

		if (propFuncArg.isNode())
			return new PropFuncArg(substitute(propFuncArg.getArg(), binding));

		List<Node> newArgList = new ArrayList<Node>();
		for (Node n : propFuncArg.getArgList())
			newArgList.add(substitute(n, binding));
		return new PropFuncArg(newArgList);
	}

	public static Expr substitute(Expr expr, Binding binding) {
		if (isNotNeeded(binding))
			return expr;
		return expr.copySubstitute(binding);
	}

	public static ExprList substitute(ExprList exprList, Binding binding) {
		if (isNotNeeded(binding))
			return exprList;
		return exprList.copySubstitute(binding);
	}

	private static boolean isNotNeeded(List<Binding> bindingList) {
		for (Binding binding : bindingList) {
			if (!binding.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static boolean isNotNeeded(Binding b) {
		return b.isEmpty();
	}

	// ----
	private static class OpSubstituteWorkerRegular extends TransformCopy {
		private Binding binding;

		/**
		 * Index of variables new names.
		 */
		private final int index;

		/**
		 * 
		 * 
		 * @param binding
		 *            binding to substitute op.
		 * @param index
		 *            Index of substituted binding to rename the variables.
		 */
		public OpSubstituteWorkerRegular(Binding binding, int index) {
			super(TransformCopy.COPY_ALWAYS);
			this.binding = binding;
			this.index = index;
		}

		@Override
		public Op transform(OpBGP opBGP) {
			BasicPattern bgp = opBGP.getPattern();
			bgp = substitute(bgp, binding, index);
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
			// List<Expr> exprList = exprs.getList();
			// ExprList numericExprList = new ExprList();
			// for (Expr expr : exprList) {
			// numericExprList.add(expr);
			// Set<Var> varsMentioned = expr.getVarsMentioned();
			// List<Var> numericVars = new ArrayList<Var>();
			// for (Var var : varsMentioned) {
			// Var numericVar = Var.alloc(var.getName() + "_" + index);
			// numericVars
			// .add(numericVar);
			// System.out.println(numericVar);
			// }
			// }
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
			super.transform(op, sub);
			Node n = substitute(op.getService(), binding);
			return new OpService(n, sub, false);
		}
	}
}
