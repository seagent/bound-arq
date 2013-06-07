package extendingconcepts.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openjena.riot.WebContent;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.engine.Rename;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.http.HttpParams;
import com.hp.hpl.jena.sparql.engine.http.HttpQuery;
import com.hp.hpl.jena.sparql.mgt.Explain;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;

public class ServiceBound {

	/* define the symbols that Service will use to set the HttpQuery parameters */
	public static final String base = "http://jena.hpl.hp.com/Service#";

	/**
	 * Use to set the HttpQuery.allowDeflate flag.
	 */
	public static final Symbol queryDeflate = ARQConstants.allocSymbol(base,
			"queryDeflate");

	/**
	 * Use to set the HttpQuery.allowGZip flag.
	 */
	public static final Symbol queryGzip = ARQConstants.allocSymbol(base,
			"queryGzip");

	/**
	 * Use to set the user id for basic auth.
	 */
	public static final Symbol queryAuthUser = ARQConstants.allocSymbol(base,
			"queryAuthUser");

	/**
	 * Use to set the user password for basic auth.
	 */
	public static final Symbol queryAuthPwd = ARQConstants.allocSymbol(base,
			"queryAuthPwd");

	/**
	 * Use this Symbol to allow passing additional service context variables
	 * SERVICE <IRI> call. Parameters need to be grouped by SERVICE <IRI>, a
	 * Map<String, Context> is assumed. The key of the first map is the SERVICE
	 * IRI, the value is a Context who's values will override any defaults in
	 * the original context.
	 * 
	 * @see com.hp.hpl.jena.sparql.engine.http.Service
	 */
	public static final Symbol serviceContext = ARQConstants.allocSymbol(base,
			"serviceContext");

	/**
	 * Set timeout. The value of this symbol gives the value of the timeout in
	 * milliseconds
	 * <ul>
	 * <li>A Number; the long value is used</li>
	 * <li>A string, e.g. "1000", parsed as a number</li>
	 * <li>A string, as two numbers separated by a comma, e.g. "500,10000"
	 * parsed as two numbers</li>
	 * </ul>
	 * The first value is passed to HttpQuery.setConnectTimeout() the second, if
	 * it exists, is passed to HttpQuery.setReadTimeout()
	 */
	public static final Symbol queryTimeout = ARQConstants.allocSymbol(base,
			"queryTimeout");

	private static Logger logger = Logger.getLogger(ServiceBound.class);

	public static List<Binding> exec(OpService op, Context context) {
		if (!op.getService().isURI())
			throw new QueryExecException("Service URI not bound: "
					+ op.getService());

		// This relies on the observation that the query was originally correct,
		// so reversing the scope renaming is safe (it merely restores the
		// algebra expression).
		// Any variables that reappear should be internal ones that were hidden
		// by renaming
		// in teh first place.
		// Any substitution is also safe because it replaced variables by
		// values.
		Op opRemote = Rename.reverseVarRename(op.getSubOp(), true);

		// Explain.explain("HTTP", opRemote, context) ;

		Query query;
		// if ( op.getServiceElement() != null )
		// {
		// does not cope with substitution?
		// query = QueryFactory.make() ;
		// query.setQueryPattern(op.getServiceElement().getElement()) ;
		// query.setQuerySelectType() ;
		// query.setQueryResultStar(true) ;
		// }
		// else
		query = OpAsQuery.asQuery(opRemote);
		// query.setDistinct(true);
		// System.out.println(query);

		// System.out.println("Query Before: \n" + query);
		// System.out.println("##############################");
		// UnionTransformer unionTransformer = new UnionTransformer(query);
		// unionTransformer.transform();
		// System.out.println("Endpoint: " + op.getService());
		// System.out.println("Query After: \n" + query);

		// try {
		// new
		// FileOutputStream("/home/etmen/Desktop/SubQueryFilterOr").write(query
		// .toString().getBytes());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// System.out.println("##############################");

		// Explain.explain("HTTP", QueryFactory.create(query), context);
		Explain.explain("HTTP", query, context);
		String uri = op.getService().getURI();
		// System.out.println("Asking to: <" + uri + ">");
		HttpQuery httpQuery = configureQuery(uri, context, query);
		// long beforeExecute = System.currentTimeMillis();
		InputStream in = httpQuery.exec();
		ResultSet rs = ResultSetFactory.fromXML(in);
		// long afterExecute = System.currentTimeMillis();
		// logger.info(MessageFormat
		// .format("Query execution prepared at the \"{0}\" service in \"{1}\" miliseconds.",
		// op.getService(), afterExecute - beforeExecute));
		// long beforeParse = System.currentTimeMillis();
		List<Binding> bindingList = generateBindingList(rs);
		// long afterParse = System.currentTimeMillis();
		// logger.info(MessageFormat.format(
		// "Results parsed in \"{0}\" miliseconds.", afterParse
		// - beforeParse));
		// // create a query iterator using bindings of resultset
		// QueryIterSolutionBound bindingIter = new QueryIterSolutionBound(
		// bindingList);
		return bindingList;
	}

	/**
	 * Create and configure the HttpQuery object.
	 * 
	 * The parentContext is not modified but is used to create a new context
	 * copy.
	 * 
	 * @param uri
	 *            The uri of the endpoint
	 * @param parentContext
	 *            The initial context.
	 * @param Query
	 *            the Query to execute.
	 * @return An HttpQuery configured as per the context.
	 */
	private static HttpQuery configureQuery(String uri, Context parentContext,
			Query query) {
		HttpQuery httpQuery = new HttpQuery(uri);
		Context context = new Context(parentContext);

		// add the context settings from the service context
		@SuppressWarnings("unchecked")
		Map<String, Context> serviceContextMap = (Map<String, Context>) context
				.get(serviceContext);
		if (serviceContextMap != null) {
			Context serviceContext = serviceContextMap.get(uri);
			if (serviceContext != null)
				context.putAll(serviceContext);
		}

		// configure the query object.
		httpQuery.merge(QueryEngineHTTPBound.getServiceParams(uri, context));
		httpQuery.addParam(HttpParams.pQuery, query.toString());
		httpQuery.setAccept(WebContent.contentTypeResultsXML);
		httpQuery.setAllowGZip(context.isTrue(queryGzip));
		httpQuery.setAllowDeflate(context.isTrue(queryDeflate));

		String user = context.getAsString(queryAuthUser);
		String pwd = context.getAsString(queryAuthPwd);

		if (user != null || pwd != null) {
			user = user == null ? "" : user;
			pwd = pwd == null ? "" : pwd;
			httpQuery.setBasicAuthentication(user, pwd.toCharArray());
		}

		setAnyTimeouts(httpQuery, context);

		return httpQuery;
	}

	/**
	 * Modified from QueryExecutionBase
	 * 
	 * @see com.hp.hpl.jena.sparql.engine.QueryExecutionBase
	 */
	private static void setAnyTimeouts(HttpQuery query, Context context) {
		if (context.isDefined(queryTimeout)) {
			Object obj = context.get(queryTimeout);
			if (obj instanceof Number) {
				int x = ((Number) obj).intValue();
				query.setConnectTimeout(x);
			} else if (obj instanceof String) {
				try {
					String str = obj.toString();
					if (str.contains(",")) {

						String[] a = str.split(",");
						int x1 = Integer.parseInt(a[0]);
						int x2 = Integer.parseInt(a[1]);
						query.setConnectTimeout(x1);
						query.setReadTimeout(x2);
					} else {
						int x = Integer.parseInt(str);
						query.setConnectTimeout(x);
					}
				} catch (NumberFormatException ex) {
					throw new QueryExecException(
							"Can't interpret string for timeout: " + obj);
				}
			} else {
				throw new QueryExecException("Can't interpret timeout: " + obj);
			}
		}
	}

	/**
	 * This method generates {@link List} of {@link Binding}s using given
	 * {@link ResultSet}
	 * 
	 * @param resultSet
	 *            {@link ResultSet} that {@link Binding}s will be got from
	 * @return {@link List} of {@link Binding}s
	 */
	private static List<Binding> generateBindingList(ResultSet resultSet) {
		List<Binding> bindingList = new ArrayList<Binding>();
		while (resultSet.hasNext()) {
			Binding binding = resultSet.nextBinding();
			bindingList.add(binding);
		}
		return bindingList;
	}

}
