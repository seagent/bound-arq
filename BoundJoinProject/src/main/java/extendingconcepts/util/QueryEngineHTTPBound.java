package extendingconcepts.util;

import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecException;
import com.hp.hpl.jena.sparql.engine.http.HttpParams;
import com.hp.hpl.jena.sparql.engine.http.Params;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.sparql.util.Context;

public class QueryEngineHTTPBound extends QueryEngineHTTP {

	public QueryEngineHTTPBound(String serviceURI, Query query) {
		super(serviceURI, query);
	}

	public QueryEngineHTTPBound(String serviceURI, String queryString) {
		super(serviceURI, queryString);
	}

	protected static Params getServiceParams(String serviceURI, Context context)
			throws QueryExecException {
		Params params = new Params();
		@SuppressWarnings("unchecked")
		Map<String, Map<String, List<String>>> serviceParams = (Map<String, Map<String, List<String>>>) context
				.get(ARQ.serviceParams);
		if (serviceParams != null) {
			Map<String, List<String>> paramsMap = serviceParams.get(serviceURI);
			if (paramsMap != null) {
				for (String param : paramsMap.keySet()) {
					if (HttpParams.pQuery.equals(param))
						throw new QueryExecException(
								"ARQ serviceParams overrides the 'query' SPARQL protocol parameter");

					List<String> values = paramsMap.get(param);
					for (String value : values)
						params.addParam(param, value);
				}
			}
		}
		return params;
	}

}
