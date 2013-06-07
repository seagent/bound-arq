package tr.edu.ege.seagent.boundarq.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import tr.edu.ege.seagent.boundarq.filterbound.SubstituteFilterBound;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.query.Expression.Variable;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;


public class BoundJoinSubstituteTest {

	public static final String BASE_URI = "http://seagent.ege.edu.tr/resource/";

	/**
	 * This method creates an {@link OpBGP} intance using given subject
	 * predicate and object {@link Variable} names.
	 * 
	 * @return
	 */
	private Op createSampleOp(Triple... triples) {
		// create basic pattern and add triple to it
		BasicPattern pattern = new BasicPattern();
		for (Triple triple : triples) {
			pattern.add(triple);
		}
		// create a new OpBGP instance using pattern
		return new OpBGP(pattern);
	}

	/**
	 * This method checks creating {@link OpFilter} using given binding list and
	 * root {@link Op}
	 * 
	 * @param filterType
	 * @param controlQuery
	 * 
	 * @throws Exception
	 */
	public void constructFilterOpWithOneFilterSingleBindingVariable(
			String filterType, String controlQuery) throws Exception {
		/**
		 * fixture setup
		 */
		// setup sample bindigs
		List<Binding> bindingList = constructThreeVariableSingleBindingList();

		Triple firstTriple = createTriple("resource", "p", "o");
		Triple secondTriple = createTriple("resource2", "p2", "o2");
		// create sample opBGP with one triple
		OpBGP opBGP = (OpBGP) createSampleOp(firstTriple, secondTriple);

		OpService opService = new OpService(
				Node.createURI("http://dbpedia.org/sparql"), opBGP, false);
		/**
		 * exercise SUT
		 */
		// generate filter op
		List<Op> filterOpList = SubstituteFilterBound.substitute(opService,
				bindingList, filterType);
//		System.out.println(OpAsQuery.asQuery(filterOpList.get(0)));
		/**
		 * verify outcome
		 */
		// check generated filter op
		assertFalse(filterOpList.isEmpty());
		Op controlOp = constructQueryOp(controlQuery);
		assertNotNull(filterOpList.get(0));
		assertEquals(controlOp, filterOpList.get(0));

	}

	private Triple createTriple(String subject, String predicate, String object) {
		return new Triple(Var.alloc(subject), Var.alloc(predicate),
				Var.alloc(object));
	}

	/**
	 * This method checks creating {@link OpFilter} using given binding list and
	 * root {@link Op}
	 * 
	 * @param filterType
	 * @param controlQuery
	 * 
	 * @throws Exception
	 */
	public void constructFilterOpWithOneFilterDoubleBindingVariable(
			String filterType, String controlQuery) throws Exception {
		/**
		 * fixture setup
		 */
		// setup sample bindigs
		List<Binding> bindingList = constructThreeVariableDoubleBindingList();
		// create sample opBGP with one triple
		Triple firstTriple = createTriple("resource", "p", "o");
		Triple secondTriple = createTriple("resource2", "p2", "o2");
		Op opBGP = createSampleOp(firstTriple, secondTriple);
		OpService opService = new OpService(
				Node.createURI("http://dbpedia.org/sparql"), opBGP, false);
		/**
		 * exercise SUT
		 */
		// generate filter op
		List<Op> filterOpList = SubstituteFilterBound.substitute(opService,
				bindingList, filterType);
//		System.out.println(OpAsQuery.asQuery(filterOpList.get(0)));
		/**
		 * verify outcome
		 */
		// check generated filter op
		assertFalse(filterOpList.isEmpty());
		Op controlOp = constructQueryOp(controlQuery);
		assertNotNull(filterOpList.get(0));
		assertEquals(controlOp, filterOpList.get(0));

	}

	/**
	 * This method checks creating {@link OpFilter} using given binding list and
	 * root {@link Op}
	 * 
	 * @param filterType
	 * @param controlQuery
	 * 
	 * @throws Exception
	 */
	public void constructFilterOpWithTwoFilterDoubleBindingVariable(
			String filterType, String controlQuery) throws Exception {
		/**
		 * fixture setup
		 */
		// setup sample bindigs
		List<Binding> bindingList = constructThreeVariableDoubleBindingList();
		// create sample opBGP with one triple

		Triple firstTriple = createTriple("resource", "p", "object");
		Triple secondTriple = createTriple("resource2", "p2", "object2");
		Triple thirdTriple = createTriple("resource3", "p3", "object");
		Op opBGP = createSampleOp(firstTriple, secondTriple, thirdTriple);
		OpService opService = new OpService(
				Node.createURI("http://dbpedia.org/sparql"), opBGP, false);
		/**
		 * exercise SUT
		 */
		// generate filter op
		List<Op> filterOpList = SubstituteFilterBound.substitute(opService,
				bindingList, filterType);
//		System.out.println(OpAsQuery.asQuery(filterOpList.get(0)));
		/**
		 * verify outcome
		 */
		// check generated filter op
		assertFalse(filterOpList.isEmpty());
		Op controlOp = constructQueryOp(controlQuery);
		assertNotNull(filterOpList.get(0));
		assertEquals(controlOp, filterOpList.get(0));

	}

	/**
	 * This method constructs double variable {@link Binding} list
	 * 
	 * @return
	 */
	private List<Binding> constructThreeVariableDoubleBindingList() {
		// define list to be filled with sample bindings
		List<Binding> bindingList = new ArrayList<Binding>();

		// create and add sample bindings to binding list
		bindingList.add(createDoubleBinding("resource", "firstResource",
				"object", "firstObject"));
		bindingList.add(createDoubleBinding("resource", "secondResource",
				"object", "secondObject"));
		bindingList.add(createDoubleBinding("resource", "thirdResource",
				"object", "thirdObject"));
		return bindingList;
	}

	/**
	 * This method creates double binding
	 * 
	 * @param firstVariableName
	 * @param firstResourcePattern
	 * @param secondVariableName
	 * @param secondResourcePattern
	 * @return
	 */
	private Binding createDoubleBinding(String firstVariableName,
			String firstResourcePattern, String secondVariableName,
			String secondResourcePattern) {
		BindingHashMap bindingHashMap = new BindingHashMap();
		bindingHashMap.add(Var.alloc(firstVariableName),
				Node.createURI(BASE_URI + firstResourcePattern));
		bindingHashMap.add(Var.alloc(secondVariableName),
				Node.createURI(BASE_URI + secondResourcePattern));
		return bindingHashMap;
	}

	/**
	 * This method constructs single variable binding list
	 * 
	 * @return
	 */
	private List<Binding> constructThreeVariableSingleBindingList() {
		// define list to be filled with sample bindings
		List<Binding> bindingList = new ArrayList<Binding>();

		// create and add sample bindings to binding list
		bindingList.add(createSingleBinding("resource", "firstResource"));
		bindingList.add(createSingleBinding("resource", "secondResource"));
		bindingList.add(createSingleBinding("resource", "thirdResource"));
		return bindingList;
	}

	private Op constructQueryOp(String query) {
		return QueryExecutionFactory.createPlan(QueryFactory.create(query),
				DatasetGraphFactory.createMem(), null).getOp();
	}

	/**
	 * this method creates single binding
	 * 
	 * @param variableName
	 *            name of {@link Variable}
	 * @param resourcePattern
	 *            resource URI pattern
	 * @return
	 */
	private Binding createSingleBinding(String variableName,
			String resourcePattern) {
		// create sample binding with given variable name and resource URI
		BindingHashMap bindingHashMap = new BindingHashMap();
		bindingHashMap.add(Var.alloc(variableName),
				Node.createURI(BASE_URI + resourcePattern));
		return bindingHashMap;
	}
}
