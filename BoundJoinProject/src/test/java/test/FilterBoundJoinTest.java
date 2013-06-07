package test;

import static org.junit.Assert.assertFalse;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import test.experience.AbstractBoundJoinTest;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.QueryEngineRegistry;

import extendingconcepts.filterbound.QueryEngineFilter;
import extendingconcepts.util.Constants;
import extendingconcepts.util.SampleQueries;

public class FilterBoundJoinTest extends AbstractBoundJoinTest {

	// private static final String filterInFirstSeenVariableQuery = "SELECT  * "
	// + "WHERE "
	// + "{ SERVICE <http://dbpedia.org/sparql> { "
	// + "{?resource ?p ?o . "
	// +
	// "FILTER ( ?resource IN (<http://seagent.ege.edu.tr/resource/firstResource>, <http://seagent.ege.edu.tr/resource/secondResource>, <http://seagent.ege.edu.tr/resource/thirdResource>) )}"
	// + "?s1 ?p ?o . " + "?s2 ?p ?o " + "} }";

	@Test
	public void executeCrossDomain1() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_CROSS_DOMAIN_1, 90,
				"predicate", "object");

	}

	@Test
	public void executeCrossDomain2() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_CROSS_DOMAIN_2, 1,
				"party", "page");

	}

	@Test
	public void executeCrossDomain3() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_CROSS_DOMAIN_3, 2,
				"president", "party", "page");

	}

	@Test
	public void executeCrossDomain4() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_CROSS_DOMAIN_4, 1,
				"actor", "news");

	}

	@Test
	public void executeCrossDomain5() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_CROSS_DOMAIN_5, 2,
				"film", "director", "genre");

	}

	@Test
	public void executeCrossDomain6() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_CROSS_DOMAIN_6, 11);

	}

	@Test
	public void executeCrossDomain7() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_CROSS_DOMAIN_7, 1,
				"location", "news");

	}

	@Test
	public void executeLifeSciences1() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_LIFE_SCIENCES_1, 1159,
				"drug", "melt");

	}

	@Test
	public void executeLifeSciences2() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_LIFE_SCIENCES_2, 333,
				"predicate", "object");

	}

	@Test
	public void executeLifeSciences3() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_LIFE_SCIENCES_3, 9054,
				"Drug", "IntDrug", "IntEffect");

	}

	@Test
	public void executeLifeSciences4() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_LIFE_SCIENCES_4, 3,
				"drugDesc", "cpd", "equation");

	}

	@Test
	public void executeLifeSciences5() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_LIFE_SCIENCES_5, 393,
				"drug", "keggUrl", "chebiImage");

	}

	@Test
	public void executeLifeSciences6() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_LIFE_SCIENCES_6, 28,
				"drug", "title");

	}

	@Test
	public void executeLifeSciences7() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_LIFE_SCIENCES_7, 144,
				"drug", "transform", "mass");

	}

	@Test
	public void executeTwoFilterVariableQuery() throws Exception {

		compareARQAndBoundJoin(SampleQueries.TWO_FILTER_VARIABLE_QUERY_LOCAL_DBPEDIA,
				5, "subject", "label", "drug", "broader");

	}

	/**
	 * This test executes sample query with bound join and measures execution
	 * time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executeSampleQueryWithBoundJoin() throws Exception {
		long oldTime = System.currentTimeMillis();
		ArrayList<QuerySolution> solutionsBoundJoin = getQuerySolutionsWithBoundJoin(SampleQueries.FEDERATED_LIFE_SCIENCES_3);
		// writeSolutionsToFile(solutionsBoundJoin, "fcBJLSSolutions");
		// check solution list is not empty
		assertFalse(solutionsBoundJoin.isEmpty());
		// check whether the size of soliton list is correct
		// assertEquals(15, solutionsBoundJoin.size());
		for (QuerySolution querySolution : solutionsBoundJoin) {
			System.out.println(querySolution);
		}
		System.out.println(solutionsBoundJoin.size());
		long newTime = System.currentTimeMillis();
		logger.info(MessageFormat
				.format("Execution of sample query with bound join longs {0} miliseconds",
						newTime - oldTime));
	}

	/**
	 * This test executes sample query without bound join and measures execution
	 * time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executeSampleQueryWithoutBoundJoin() throws Exception {
		long oldTime = System.currentTimeMillis();
		ArrayList<QuerySolution> solutionsPureARQ = getQuerySolutionsWithPureARQ(SampleQueries.TWO_FILTER_VARIABLE_QUERY_LOCAL_DBPEDIA);
		// writeSolutionsToFile(solutionsPureARQ, "fcARQSolutions");
		// check solution list is not empty
		assertFalse(solutionsPureARQ.isEmpty());
		for (QuerySolution querySolution : solutionsPureARQ) {
			System.out.println(querySolution);
		}
		System.out.println(solutionsPureARQ.size());
		// check whether the size of soliton list is correct
		// assertEquals(15, solutionsPureARQ.size());
		long newTime = System.currentTimeMillis();
		logger.info(MessageFormat
				.format("Execution of sample query with pure ARQ longs {0} miliseconds",
						newTime - oldTime));
	}

	// private void writeSolutionsToFile(ArrayList<QuerySolution> solutions,
	// String filePattern) throws IOException {
	// FileWriter fileWriter = new FileWriter("/home/etmen/Desktop/"
	// + filePattern);
	// for (QuerySolution querySolution : solutions) {
	// fileWriter.write(querySolution.toString() + "\n");
	// }
	// fileWriter.close();
	// }
	//
	// @Test
	// public void moveFilterBlockToTheFirstSeenVaraible() throws Exception {
	// /**
	// * fixture setup
	// */
	// // setup sample bindigs
	// List<Binding> bindingList = constructThreeVariableSingleBindingList();
	//
	// // create triples that construct body of where clause
	// Triple mainTriple = new Triple(Var.alloc("resource"), Var.alloc("p"),
	// Var.alloc("o"));
	// Triple firstSubTriple = new Triple(Var.alloc("s1"), Var.alloc("p"),
	// Var.alloc("o"));
	// Triple secondSubTriple = new Triple(Var.alloc("s2"), Var.alloc("p"),
	// Var.alloc("o"));
	// // create sample opBGP with three triples
	// Op opBGP = createSampleOpFromTriples(mainTriple, firstSubTriple,
	// secondSubTriple);
	// OpService opService = new OpService(
	// Node.createURI("http://dbpedia.org/sparql"), opBGP, false);
	// /**
	// * exercise SUT
	// */
	// // generate filter op
	// List<Op> filterOpList = SubstituteFilterBound.substitute(opService,
	// bindingList);
	// System.out.println(OpAsQuery.asQuery(filterOpList.get(0)));
	// // /**
	// // * verify outcome
	// // */
	// // // check generated filter op
	// // assertFalse(filterOpList.isEmpty());
	// // Op controlOp = constructQueryOp("");
	// // assertNotNull(filterOpList.get(0));
	// // assertEquals(controlOp, filterOpList.get(0));
	// }
	//
	// /**
	// * This method creates an {@link OpBGP} intance using given {@link
	// Triple}s
	// *
	// * @param triples
	// * @return
	// */
	// private Op createSampleOpFromTriples(Triple... triples) {
	// // create basic pattern and add triples to it
	// BasicPattern pattern = new BasicPattern();
	// for (Triple triple : triples) {
	// pattern.add(triple);
	// }
	// return new OpBGP(pattern);
	// }

	/**
	 * This method executes sample Query with bound join
	 * 
	 * @param queryText
	 * 
	 * @return {@link QuerySolution} list of bound join results.
	 */
	protected ArrayList<QuerySolution> getQuerySolutionsWithBoundJoin(
			String queryText) {
		// first register our factory
		QueryEngineFilter.register();

		QueryExecution qexec = QueryExecutionFactory.create(queryText,
				emptyDataset);
		// qexec.getContext().put(Constants.UNION_SIZE_SYMBOL, UNION_COUNT);
		qexec.getContext().put(Constants.FILTER_TYPE, Constants.OR_FILTER);
		ResultSet resultSet = qexec.execSelect();
		return generateSolutionList(resultSet, false, true);
	}

	@Override
	protected List<QuerySolution> checkControlSolutions(String query,
			int expectedSolutionSize, String... labels) {
		return checkSolutions(expectedSolutionSize,
				getQuerySolutionsWithPureARQ(query));
	}

	@Override
	protected List<QuerySolution> checkBoundJoinSolutionsResults(String query,
			int expectedSolutionSize) {
		return checkSolutions(expectedSolutionSize,
				getQuerySolutionsWithBoundJoin(query));
	}

	@After
	public void after() {
		// remove factory of bound join solution
		QueryEngineRegistry.removeFactory(QueryEngineFilter.getFactory());
	}
}
