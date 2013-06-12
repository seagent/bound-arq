package experience;

import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;

@Ignore
public class BigFilterQueryTest {
	@Test
	public void sendBigFilterQuery() throws Exception {

		String queryFirst = "SELECT  ?Drug WHERE { "
				+ "?Drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug>."
				+ " }";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://155.223.24.47:8897/dbpedia/sparql", queryFirst);
		ResultSet resultSet = queryExecution.execSelect();

		String querySecond = "SELECT ?country WHERE { "
				+ "?country <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Country>. "
				+ "FILTER (";

		// querySecond = generateFilterWithOR(resultSet, querySecond);
		querySecond = generateFilterWithIN(resultSet, querySecond, "country",
				"Drug");
		querySecond += ") }";
		System.out.println(querySecond);

		// FileWriter fileWriter = new FileWriter(new File(
		// "/home/etmen/Desktop/bigFilterQuery"));
		// fileWriter.write(querySecond);
		// fileWriter.close();

		long before = System.currentTimeMillis();
		QueryExecution queryExecution2 = QueryExecutionFactory.sparqlService(
				"http://155.223.24.47:8897/dbpedia/sparql", querySecond);
		ResultSet resultSet2 = queryExecution2.execSelect();
		while (resultSet2.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet2.next();
			System.out.println(querySolution);
		}
		long after = System.currentTimeMillis();
		long executionTime = after - before;
		System.out.println(executionTime);

	}

	private String generateFilterWithIN(ResultSet resultSet,
			String querySecond, String variableToFilter,
			String fillVariableToGet) {
		int i = 0;
		querySecond += "?" + variableToFilter + " IN (";
		for (i = 0; resultSet.hasNext(); i++) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			if (i > 0) {
				querySecond += ",";
			}
			querySecond += "<";
			String drugURI = querySolution.get(fillVariableToGet).asResource()
					.getURI();
			querySecond += drugURI + ">";
		}
		querySecond += ")";
		return querySecond;
	}

	private String generateFilterWithOR(ResultSet resultSet, String querySecond) {
		int i = 0;
		for (i = 0; resultSet.hasNext() && i < 4500; i++) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			if (i > 0) {
				querySecond += "||";
			}
			querySecond += "?country = " + "<";
			String drugURI = querySolution.get("Drug").asResource().getURI();
			querySecond += drugURI + ">";
			i++;
		}
		return querySecond;
	}

	@Test
	public void sendDrugBankBigFilterQuery() throws Exception {
		String query = "SELECT  * "
				+ "WHERE "
				+ "{ ?y <http://www.w3.org/2002/07/owl#sameAs> ?Drug ."
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?y ."
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?IntDrug ."
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/text> ?IntEffect."
				+ "FILTER ( ?Drug IN (";

		for (int i = 0; i < 4500; i++) {
			query += "<http://http://dbpedia.org/resource/DrugRsc" + i + ">, ";
		}
		query += "<http://http://dbpedia.org/resource/DrugRsc4500> ))}";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://155.223.24.47:8893/drugbank/sparql", query);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			System.out.println(querySolution);
		}

	}

	@Test
	public void analyzeFilterQuery() throws Exception {
		String queryFirst = "SELECT ?countryFirst WHERE { ?countryFirst <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Country>.}";
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://dbpedia.org/sparql", queryFirst);
		ResultSet resultSet = queryExecution.execSelect();
		String querySecond = "SELECT ?country WHERE { SERVICE <http://dbpedia.org/sparql> { "
				+ "?country <http://dbpedia.org/ontology/capital> ?somewhere. "
				+ "FILTER (";

		// querySecond = generateFilterWithOR(resultSet, querySecond);
		querySecond = generateFilterWithIN(resultSet, querySecond, "country",
				"countryFirst");
		querySecond += "). } "
				+ " SERVICE <http://dbpedia.org/sparql>{?country <http://dbpedia.org/ontology/currency> <http://dbpedia.org/resource/Euro>.}}";
		System.out.println(querySecond);

		Op op = QueryExecutionFactory.createPlan(
				QueryFactory.create(querySecond),
				DatasetGraphFactory.createMem(), null).getOp();
		System.out.println(op.getName());

		long before = System.currentTimeMillis();
		QueryExecution queryExecution2 = QueryExecutionFactory.create(
				querySecond, ModelFactory.createDefaultModel());
		ResultSet resultSet2 = queryExecution2.execSelect();
		int i = 0;
		for (i = 0; resultSet2.hasNext(); i++) {
			QuerySolution querySolution = (QuerySolution) resultSet2.next();
			System.out.println(querySolution);
		}
		long after = System.currentTimeMillis();
		long executionTime = after - before;
		System.out.println("Solution count: " + i);
		System.out.println("Execution time: " + executionTime);
	}
}
