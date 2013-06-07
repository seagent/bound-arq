package test;

import org.junit.Test;

import extendingconcepts.util.Constants;

public class BoundJoinSubstituteInFilterTest extends BoundJoinSubstituteTest {

	private static final String queryToControlSingleBindingValue = "SELECT  * "
			+ "WHERE "
			+ "{ SERVICE <http://dbpedia.org/sparql> "
			+ "{ { ?resource ?p ?o "
			+ "FILTER ( ?resource IN (<http://seagent.ege.edu.tr/resource/firstResource>, <http://seagent.ege.edu.tr/resource/secondResource>, <http://seagent.ege.edu.tr/resource/thirdResource>) ) "
			+ "} " + "{ ?resource2 ?p2 ?o2 } " + "}}";

	private static final String queryToControlDoubleBindingValue = "SELECT  * "
			+ "WHERE "
			+ "{ SERVICE <http://dbpedia.org/sparql> "
			+ "{ { ?resource ?p ?o "
			+ "FILTER ( ?resource IN (<http://seagent.ege.edu.tr/resource/firstResource>, <http://seagent.ege.edu.tr/resource/secondResource>, <http://seagent.ege.edu.tr/resource/thirdResource>) ) "
			+ "} " + "{ ?resource2 ?p2 ?o2 } " + "}}";

	private static final String queryToControlDoubleFilterDoubleBinding = "SELECT  * "
			+ "WHERE "
			+ "{ SERVICE <http://dbpedia.org/sparql> "
			+ "{ { ?resource ?p ?object "
			+ "FILTER ( ?resource IN (<http://seagent.ege.edu.tr/resource/firstResource>, <http://seagent.ege.edu.tr/resource/secondResource>, <http://seagent.ege.edu.tr/resource/thirdResource>) ) "
			+ "FILTER ( ?object IN (<http://seagent.ege.edu.tr/resource/firstObject>, <http://seagent.ege.edu.tr/resource/secondObject>, <http://seagent.ege.edu.tr/resource/thirdObject>) ) "
			+ "} "
			+ "{ ?resource2 ?p2 ?object2 . "
			+ "?resource3 ?p3 ?object } " + "}}";

	@Test
	public void oneFilterSingleBinding() throws Exception {
		constructFilterOpWithOneFilterSingleBindingVariable(
				Constants.IN_FILTER, queryToControlSingleBindingValue);
	}

	@Test
	public void oneFilterDoubleBinding() throws Exception {
		constructFilterOpWithOneFilterDoubleBindingVariable(
				Constants.IN_FILTER, queryToControlDoubleBindingValue);
	}

	@Test
	public void twoFilterDoubleBinding() throws Exception {
		constructFilterOpWithTwoFilterDoubleBindingVariable(
				Constants.IN_FILTER,
				queryToControlDoubleFilterDoubleBinding);
	}

}
