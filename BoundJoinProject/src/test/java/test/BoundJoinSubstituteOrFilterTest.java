package test;

import org.junit.Test;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;

import extendingconcepts.util.Constants;

public class BoundJoinSubstituteOrFilterTest extends BoundJoinSubstituteTest {
	private static final String queryToControlSingleBindingValue = "SELECT  *"
			+ " WHERE "
			+ " { SERVICE <http://dbpedia.org/sparql> "
			+ "{ { ?resource ?p ?o "
			+ "FILTER ( ( ( ?resource = <http://seagent.ege.edu.tr/resource/firstResource> ) || ( ?resource = <http://seagent.ege.edu.tr/resource/secondResource> ) ) || ( ?resource = <http://seagent.ege.edu.tr/resource/thirdResource> ) ) "
			+ "} " + "{ ?resource2 ?p2 ?o2 }" + "}}";

	private static final String queryToControlDoubleBindingValue = "SELECT  *"
			+ " WHERE "
			+ " { SERVICE <http://dbpedia.org/sparql> "
			+ "{ { ?resource ?p ?o "
			+ "FILTER ( ( ( ?resource = <http://seagent.ege.edu.tr/resource/firstResource> ) || ( ?resource = <http://seagent.ege.edu.tr/resource/secondResource> ) ) || ( ?resource = <http://seagent.ege.edu.tr/resource/thirdResource> ) ) "
			+ "} " + "{ ?resource2 ?p2 ?o2 }" + "}}";

	private static final String queryToControlDoubleFilterDoubleBinding = "SELECT  * "
			+ "WHERE "
			+ "{ SERVICE <http://dbpedia.org/sparql> "
			+ "{ { ?resource ?p ?object "
			+ "FILTER ( ( ( ?resource = <http://seagent.ege.edu.tr/resource/firstResource> ) || ( ?resource = <http://seagent.ege.edu.tr/resource/secondResource> ) ) || ( ?resource = <http://seagent.ege.edu.tr/resource/thirdResource> ) ) "
			+ "FILTER ( ( ( ?object = <http://seagent.ege.edu.tr/resource/firstObject> ) || ( ?object = <http://seagent.ege.edu.tr/resource/secondObject> ) ) || ( ?object = <http://seagent.ege.edu.tr/resource/thirdObject> ) ) "
			+ "} "
			+ "{ ?resource2 ?p2 ?object2 . "
			+ "?resource3 ?p3 ?object "
			+ "}}}";

	@Test
	public void oneFilterSingleBinding() throws Exception {
		constructFilterOpWithOneFilterSingleBindingVariable(
				Constants.OR_FILTER, queryToControlSingleBindingValue);
	}

	@Test
	public void oneFilterDoubleBinding() throws Exception {
		constructFilterOpWithOneFilterDoubleBindingVariable(
				Constants.OR_FILTER, queryToControlDoubleBindingValue);
	}

	@Test
	public void twoFilterDoubleBinding() throws Exception {
		constructFilterOpWithTwoFilterDoubleBindingVariable(
				Constants.OR_FILTER, queryToControlDoubleFilterDoubleBinding);
	}

	@Test
	public void doubleFilterOp() throws Exception {
		String query = "SELECT * WHERE { {?s ?p ?o."
				+ "FILTER (?s=<http://seagent.ege.edu.tr/s1> || ?s=<http://seagent.ege.edu.tr/s2>)}"
				+ "?x ?y ?z."
				+ "FILTER(?y IN (<http://seagent.ege.edu.tr/y1>))}";
		Op op = QueryExecutionFactory.createPlan(QueryFactory.create(query),
				DatasetGraphFactory.createMem(), null).getOp();
		System.out.println(op);
	}

}
