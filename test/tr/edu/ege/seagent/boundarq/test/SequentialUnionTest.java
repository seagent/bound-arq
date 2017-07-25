package tr.edu.ege.seagent.boundarq.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tr.edu.ege.seagent.boundarq.util.UnionTransformer;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;


public class SequentialUnionTest {

	private UnionTransformer unionTransformer;

	@Test
	public void convertThreeLoopingUnionToSequential() throws Exception {

		// define a query that consists of three union parts
		String loopingUnionQueryText = "SELECT * WHERE {   {   "
				+ "{ ?y_0 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Tetrahydrocannabinol> ."
				+ "?y_1 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> .}"
				+ "UNION{ ?y_2 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> ."
				+ "?y_5 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> .}}"
				+ "UNION{ ?y_3 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Bacillus_Calmette-Gu%C3%A9rin> ."
				+ "?y_4 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Bacillus_Calmette-Gu%C3%A9rin> .}}";

		String sequentialUnionQueryText = "SELECT  * WHERE {   { ?y_0 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Tetrahydrocannabinol> . "
				+ "?y_1 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline>} "
				+ "UNION { ?y_2 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> ."
				+ " ?y_5 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline>} "
				+ "UNION { ?y_3 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Bacillus_Calmette-Gu%C3%A9rin> ."
				+ " ?y_4 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Bacillus_Calmette-Gu%C3%A9rin>} }";

		// create query instance using query text
		Query queryToTransform = QueryFactory.create(loopingUnionQueryText);
		// initialize UnionTransformer instance
		unionTransformer = new UnionTransformer(queryToTransform);
		// transform looping union query to sequential form
		unionTransformer.transform();
		queryToTransform.serialize(System.out);

		// create union blocks of transformed query
		final List<Element> unionBlocksOfTransformedQuery = new ArrayList<Element>();
		retrieveUnionBlocks(QueryFactory.create(queryToTransform),
				unionBlocksOfTransformedQuery);
		assertFalse(unionBlocksOfTransformedQuery.isEmpty());

		// create sequential union query
		Query sequentialUnionQuery = QueryFactory
				.create(sequentialUnionQueryText);
		// create union blocks of sequential query
		final List<Element> unionBlocksOfExpectedQuery = new ArrayList<Element>();
		retrieveUnionBlocks(sequentialUnionQuery, unionBlocksOfExpectedQuery);
		assertFalse(unionBlocksOfExpectedQuery.isEmpty());

		// check whether transformed and expected queries are equal
		checkGivenListsAreEqual(unionBlocksOfTransformedQuery,
				unionBlocksOfExpectedQuery);

	}

	@Test
	public void convertInnerLoopingUnionToSequential() throws Exception {

		// define a query that consists of three union parts
		String innerLoopingQueryText = "SELECT * WHERE {   {   "
				+ "{ {?y_0 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Tetrahydrocannabinol> ."
				+ "?y_1 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> .}}"
				+ "UNION{ {{ {?y_2 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> .}UNION"
				+ "{{?y_5 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> ."
				+ "?y_7 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> .} UNION {?y_6 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> ."
				+ "}}}}}}"
				+ "UNION{ ?y_3 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Bacillus_Calmette-Gu%C3%A9rin> ."
				+ "?y_4 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Bacillus_Calmette-Gu%C3%A9rin> .}}";

		String sequentialUnionQueryText = "SELECT  * WHERE {   "
				+ "{ ?y_0 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Tetrahydrocannabinol> . "
				+ "?y_1 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline>} "
				+ "UNION { ?y_2 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline>} "
				+ "UNION { ?y_5 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline> . "
				+ "?y_7 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline>} "
				+ "UNION { ?y_6 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Mescaline>} "
				+ "UNION { ?y_3 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Bacillus_Calmette-Gu%C3%A9rin> . "
				+ "?y_4 <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Bacillus_Calmette-Gu%C3%A9rin>} }";

		// create query instance using query text
		Query queryToTransform = QueryFactory.create(innerLoopingQueryText);
		// initialize UnionTransformer instance
		unionTransformer = new UnionTransformer(queryToTransform);
		// transform looping union query to sequential form
		unionTransformer.transform();
		queryToTransform.serialize(System.out);

		// create union blocks of transformed query
		final List<Element> unionBlocksOfTransformedQuery = new ArrayList<Element>();
		retrieveUnionBlocks(QueryFactory.create(queryToTransform),
				unionBlocksOfTransformedQuery);
		assertFalse(unionBlocksOfTransformedQuery.isEmpty());

		// create sequential union query
		Query sequentialUnionQuery = QueryFactory
				.create(sequentialUnionQueryText);
		// create union blocks of sequential query
		final List<Element> unionBlocksOfExpectedQuery = new ArrayList<Element>();
		retrieveUnionBlocks(sequentialUnionQuery, unionBlocksOfExpectedQuery);
		assertFalse(unionBlocksOfExpectedQuery.isEmpty());

		// check whether transformed and expected queries are equal
		checkGivenListsAreEqual(unionBlocksOfTransformedQuery,
				unionBlocksOfExpectedQuery);

	}

	@Test
	public void convertThirtyLoopingUnionToSequential() throws Exception {

		// define a query that consists of three union parts
		String loopingUnionQueryText = readFile("queries/loopingUnionQuery");

		// create query instance using query text
		Query queryToTransform = QueryFactory.create(loopingUnionQueryText);
		// initialize UnionTransformer instance
		unionTransformer = new UnionTransformer(queryToTransform);
		// transform looping union query to sequential form
		unionTransformer.transform();
		queryToTransform.serialize(System.out);

		// create union blocks of transformed query
		final List<Element> unionBlocksOfTransformedQuery = new ArrayList<Element>();
		retrieveUnionBlocks(QueryFactory.create(queryToTransform),
				unionBlocksOfTransformedQuery);
		assertFalse(unionBlocksOfTransformedQuery.isEmpty());

		// read sequential query
		String sequentialUnionQueryText = readFile("queries/sequentialUnionQuery");
		Query sequentialUnionQuery = QueryFactory
				.create(sequentialUnionQueryText);
		// create union blocks of sequential query
		final List<Element> unionBlocksOfExpectedQuery = new ArrayList<Element>();
		retrieveUnionBlocks(sequentialUnionQuery, unionBlocksOfExpectedQuery);
		assertFalse(unionBlocksOfExpectedQuery.isEmpty());

		// check whether transformed and expected queries are equal
		checkGivenListsAreEqual(unionBlocksOfTransformedQuery,
				unionBlocksOfExpectedQuery);

	}

	@Test
	public void transformFilterBlockQuery() throws Exception {
		// define a query that consists of three union parts
		String loopingUnionQueryText = readFile("queries/queryOutputFilter");

		// create query instance using query text
		Query queryToTransform = QueryFactory.create(loopingUnionQueryText);
		// initialize UnionTransformer instance
		unionTransformer = new UnionTransformer(queryToTransform);
		// transform looping union query to sequential form
		unionTransformer.transform();
		queryToTransform.serialize(System.out);
	}

	/**
	 * This method controls whether given two lists are equal
	 * 
	 * @param firstElementList
	 * @param secondElementList
	 */
	private void checkGivenListsAreEqual(final List<Element> firstElementList,
			final List<Element> secondElementList) {
		// first check sizes are equal
		assertEquals(secondElementList.size(), firstElementList.size());

		// then check whether elements are equal with each other
		for (int i = 0; i < secondElementList.size(); i++) {
			assertEquals(secondElementList.get(i), firstElementList.get(i));
		}
	}

	/**
	 * This method retrieves all union block of given query
	 * 
	 * @param query
	 * @param unionLeafs
	 */
	public void retrieveUnionBlocks(Query query, final List<Element> unionLeafs) {
		// define an ElementVisitor instance to walk on union elements and edit
		// them
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementUnion el) {
				List<Element> elements = el.getElements();
				for (Element element : elements) {
					unionLeafs.add(element);
				}
				super.visit(el);
			}

		};
		// operate walk with our ElementVisitor instance
		ElementWalker.walk(query.getQueryPattern(), tripleVisitor);
	}

	/**
	 * This method reads file from given file path and returns it as
	 * {@link String} text
	 * 
	 * @param filePath
	 *            which file read from
	 * @return file text
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String readFile(String filePath) throws FileNotFoundException,
			IOException {
		String fileText = "";
		File file = new File(filePath);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = bufferedReader.readLine();
		while (line != null) {
			fileText += line;
			line = bufferedReader.readLine();
		}
		return fileText;
	}
}
