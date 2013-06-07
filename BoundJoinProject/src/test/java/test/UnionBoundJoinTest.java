package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import test.experience.AbstractBoundJoinTest;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.QueryEngineRegistry;

import extendingconcepts.unionbound.QueryEngineUnion;
import extendingconcepts.unionbound.SubstituteUnionBound;
import extendingconcepts.util.Constants;
import extendingconcepts.util.SampleQueries;

public class UnionBoundJoinTest extends AbstractBoundJoinTest {

	static {
		// QueryEngineBound.register();
	}

	@Test
	public void executeCrossDomain4() throws Exception {

		compareARQAndBoundJoin(SampleQueries.FEDERATED_CROSS_DOMAIN_4, 1,
				"actor", "news");

	}

	/**
	 * This test executes triple chain query on ARQ and Bound Join and compares
	 * them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForTripleChainQuery() throws Exception {

		// execute triple chain query
		compareARQAndBoundJoin(SampleQueries.TRIPLE_CHAIN_DBPEDIA_MOVIE_QUERY,
				2, "createdMovie", "producer", "producerName");
	}

	/**
	 * This test executes double chain query on ARQ and Bound Join and compares
	 * them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForDoubleChainQuery() throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(
				SampleQueries.DOUBLE_CHAIN_DBPEDIA_LMDB_MOVIE_QUERY, 71,
				"movieLabel", "directorLMDB");
	}

	/**
	 * This test executes two variable double chain query on ARQ and Bound Join
	 * and compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForTwoVariableDoubleChainQuery()
			throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(
				SampleQueries.TWO_VARIABLE_DOUBLE_CHAIN_DBPEDIA_MOVIE_QUERY,
				15, "directorDBP", "createdMovieName", "editedMovieName");
	}

	/**
	 * This test executes two bind service union query on ARQ and Bound Join and
	 * compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForTwoBindServiceUnionQuery()
			throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(
				SampleQueries.TWO_BIND_SERVICE_UNION_MOVIE_QUERY, 4,
				"subsidiary");
	}

	/**
	 * This test executesdouble chain bind service double union query on ARQ and
	 * Bound Join and compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForDoubleChainBindServiceDoubleUnionQuery()
			throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(
				SampleQueries.DOUBLE_CHAIN_BIND_SERVICE_DOUBLE_UNION_MOVIE_QUERY,
				2, "subsidiary", "subsidiaryName");
	}

	/**
	 * This test executes optional double service query on ARQ and Bound Join
	 * and compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForOptionalDoubleServiceQuery()
			throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(SampleQueries.OPTIONAL_DOUBLE_SERVICE_QUERY, 3,
				"editedMovie", "movieName");
	}

	/**
	 * This test executes optional double service bind query on ARQ and Bound
	 * Join and compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForOptionalDoubleServiceBindQuery()
			throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(
				SampleQueries.OPTIONAL_DOUBLE_SERVICE_BIND_QUERY, 6,
				"editedMovie", "movieName");
	}

	/**
	 * This test executes double chain union query on ARQ and Bound Join and
	 * compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForDoubleChainUnionQuery()
			throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(SampleQueries.DOUBLE_CHAIN_UNION_QUERY, 3,
				"directorDBP", "organization", "developedWork");
	}

	/**
	 * This test executes double chain union bind service query on ARQ and Bound
	 * Join and compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForDoubleChainUnionBindServiceQuery()
			throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(
				SampleQueries.DOUBLE_CHAIN_UNION_BIND_SERVICE_QUERY, 6,
				"directorDBP", "organization", "developedWork");
	}

	/**
	 * This test executes double chain double chain big solution query on ARQ
	 * and Bound Join and compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForDoubleChainBigSolutionQuery()
			throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(SampleQueries.DOUBLE_CHAIN_BIG_SOLUTION_QUERY,
				95, "film", "filmName");
	}

	/**
	 * This test executes double chain double chain big solution query on ARQ
	 * and Bound Join and compares them.
	 * 
	 * @throws Exception
	 */
	@Test
	public void compareARQAndBoundJoinForCrossDomainQuery() throws Exception {

		// execute double chain query
		compareARQAndBoundJoin(SampleQueries.CROSS_DOMAIN_QUERY_1, 299,
				"predicate", "object");
	}

	/**
	 * This test executes sample query with bound join and measures execution
	 * time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executeSampleQueryWithBoundJoin() throws Exception {
		long oldTime = Calendar.getInstance().getTimeInMillis();
		ArrayList<QuerySolution> solutionsBoundJoin = getQuerySolutionsWithBoundJoin(SampleQueries.DOUBLE_CHAIN_DBPEDIA_LMDB_MOVIE_QUERY);
		// check solution list is not empty
		assertFalse(solutionsBoundJoin.isEmpty());
		// check whether the size of soliton list is correct
		// assertEquals(15, solutionsBoundJoin.size());
		long newTime = Calendar.getInstance().getTimeInMillis();
		logger.info(MessageFormat
				.format("Execution of sample query with bound join longs {0} miliseconds",
						newTime - oldTime));
	}

	@Test
	public void opFilterTest() throws Exception {
		String query = "SELECT  * WHERE {   {   "
				+ "{ <http://dbpedia.org/resource/Fethiye> <http://dbpedia.org/ontology/populationMetro> ?populationMetro_0 FILTER ( ?populationMetro > 100000 ) } "
				+ "UNION "
				+ "{ <http://dbpedia.org/resource/Izmir> <http://dbpedia.org/ontology/populationMetro> ?populationMetro_1 FILTER ( ?populationMetro > 100000 ) }"
				+ " } UNION "
				+ "{ <http://dbpedia.org/resource/Manisa> <http://dbpedia.org/ontology/populationMetro> ?populationMetro_2 FILTER ( ?populationMetro > 100000 ) } }";
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://dbpedia.org/sparql", query);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			System.out.println(querySolution);
		}
	}

	/**
	 * This test executes sample query without bound join and measures execution
	 * time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executeSampleQueryWithoutBoundJoin() throws Exception {
		long oldTime = Calendar.getInstance().getTimeInMillis();
		ArrayList<QuerySolution> solutionsPureARQ = getQuerySolutionsWithPureARQ(SampleQueries.DOUBLE_CHAIN_FILTER_QUERY);
		// check solution list is not empty
		assertFalse(solutionsPureARQ.isEmpty());
		System.out.println(solutionsPureARQ.size());
		// check whether the size of soliton list is correct
		// assertEquals(15, solutionsPureARQ.size());
		long newTime = Calendar.getInstance().getTimeInMillis();
		logger.info(MessageFormat
				.format("Execution of sample query with pure ARQ longs {0} miliseconds",
						newTime - oldTime));
	}

	/**
	 * This test checks whether union count used as given value.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkUnionCount() throws Exception {
		// Execute sample query with bound join. Union count is embedded in
		// this method setted default value UNION_COUNT constant as 15.
		getQuerySolutionsWithBoundJoin(SampleQueries.CROSS_DOMAIN_QUERY_1);
		// assert union count value contained and really used in SubstitueBound
		// is same with we setted as default
		assertEquals(UNION_COUNT, SubstituteUnionBound.getUnionSize());
	}

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
		QueryEngineUnion.register();

		QueryExecution qexec = QueryExecutionFactory.create(queryText,
				emptyDataset);
		qexec.getContext().put(Constants.UNION_SIZE_SYMBOL, UNION_COUNT);
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
		QueryEngineRegistry.removeFactory(QueryEngineUnion.getFactory());
	}
}
