package experience;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Ignore;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.DatasetImpl;

import tr.edu.ege.seagent.boundarq.test.UnionBoundJoinTest;

@Ignore
public abstract class AbstractBoundJoinTest {

	protected double arqExecutionTime;
	protected double boundExecutionTime;
	protected DatasetImpl emptyDataset;
	protected static final int UNION_COUNT = 15;
	protected Logger logger = Logger.getLogger(UnionBoundJoinTest.class.toString());

	/**
	 * This method compares two {@link QuerySolution} list contains same
	 * elements.
	 * 
	 * @param solutionsPureARQ
	 * @param solutionsBoundJoin
	 * @param solutionSize
	 * @param labels
	 */
	protected void compareQueryResults(List<QuerySolution> solutionsPureARQ, List<QuerySolution> solutionsBoundJoin,
			int solutionSize, String... labels) {
		// iterate on each label
		for (String label : labels) {
			// define node lists for current label
			List<RDFNode> arqNodes = new ArrayList<RDFNode>();
			List<RDFNode> boundNodes = new ArrayList<RDFNode>();
			// construct node lists with node label values
			for (int i = 0; i < solutionSize; i++) {
				// fill arq solution
				QuerySolution arqSolution = solutionsPureARQ.get(i);
				arqNodes.add(arqSolution.get(label));
				// fill bound solution
				QuerySolution boundJoinSolution = solutionsBoundJoin.get(i);
				boundNodes.add(boundJoinSolution.get(label));
			}
			// control lists are equal
			for (int i = 0; i < solutionSize; i++) {
				// check two node list contains each other nodes.
				assertTrue(arqNodes.contains(boundNodes.get(i)));
				assertTrue(boundNodes.contains(arqNodes.get(i)));
			}
		}
	}

	/**
	 * This method executes sample query on pure ARQ.
	 * 
	 * @param queryText
	 *            query {@link String} value to be executed
	 * 
	 * @return {@link QuerySolution} list of pure ARQ results.
	 */
	protected ArrayList<QuerySolution> getQuerySolutionsWithPureARQ(String queryText) {
		QueryExecution queryExecution = QueryExecutionFactory.create(queryText, emptyDataset);
		// get result set
		ResultSet resultSet = queryExecution.execSelect();
		// check whether it is not null
		assertNotNull(resultSet);
		// generate solution list using resultset
		return generateSolutionList(resultSet, true, false);
	}

	/**
	 * This method generates {@link QuerySolution} list using given
	 * {@link ResultSet}
	 * 
	 * @param resultSet
	 *            contains {@link QuerySolution} instances
	 * @param arq
	 * @param bound
	 * @return {@link QuerySolution} list
	 */
	protected ArrayList<QuerySolution> generateSolutionList(ResultSet resultSet, boolean arq, boolean bound) {
		ArrayList<QuerySolution> solutions = new ArrayList<QuerySolution>();
		calculateExecutionTime(resultSet, arq, bound, solutions);
		return solutions;
	}

	/**
	 * This method calculates execution time and adds first result to solution
	 * list.
	 * 
	 * @param resultSet
	 * @param arq
	 * @param bound
	 * @param solutions
	 */
	private void calculateExecutionTime(ResultSet resultSet, boolean arq, boolean bound,
			ArrayList<QuerySolution> solutions) {
		double before = System.currentTimeMillis();
		if (resultSet.hasNext()) {
			double after = System.currentTimeMillis();
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			if (bound) {
				boundExecutionTime = after - before;
			}
			solutions.add(querySolution);
		}
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			solutions.add(querySolution);
		}
		if (arq) {
			double after = System.currentTimeMillis();
			arqExecutionTime = after - before;
		}
	}

	/**
	 * This method compares given query as executing it on pure ARQ and
	 * BoundJoin respectively with given expected size
	 * 
	 * @param query
	 *            query to be executed.
	 * @param expectedSolutionSize
	 *            solution size to be compared.
	 * @throws Exception
	 */
	protected void compareARQAndBoundJoin(String query, int expectedSolutionSize, String... labels) throws Exception {
		// check conrol solutions
		List<QuerySolution> solutionsPureARQ = checkControlSolutions(query, expectedSolutionSize, labels);
		logger.info(MessageFormat.format("Execution of sample query with pure ARQ longs {0} miliseconds",
				arqExecutionTime));
		// check bound join solutions
		List<QuerySolution> solutionsBoundJoin = checkBoundJoinSolutionsResults(query, expectedSolutionSize);
		logger.info(MessageFormat.format("Execution of sample query with bound join longs {0} miliseconds",
				boundExecutionTime));
		// check the results are same
		compareQueryResults(solutionsPureARQ, solutionsBoundJoin, expectedSolutionSize, labels);
	}

	protected abstract List<QuerySolution> checkControlSolutions(String query, int expectedSolutionSize,
			String... labels);

	protected abstract List<QuerySolution> checkBoundJoinSolutionsResults(String query, int expectedSolutionSize)
			throws Exception;

	protected ArrayList<QuerySolution> checkSolutions(int expectedSolutionSize, ArrayList<QuerySolution> solutions) {
		// check solution list is not empty
		assertFalse(solutions.isEmpty());
		// check whether the size of soliton list is correct
		assertEquals(expectedSolutionSize, solutions.size());
		return solutions;
	}

	@Before
	public void before() {
		// creating an empty dataset
		emptyDataset = new DatasetImpl(ModelFactory.createDefaultModel());
	}

}
