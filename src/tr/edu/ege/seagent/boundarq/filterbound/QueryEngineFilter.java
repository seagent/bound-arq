package tr.edu.ege.seagent.boundarq.filterbound;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Substitute;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.Plan;
import com.hp.hpl.jena.sparql.engine.QueryEngineFactory;
import com.hp.hpl.jena.sparql.engine.QueryEngineRegistry;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterRoot;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorCheck;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorTiming;
import com.hp.hpl.jena.sparql.engine.main.QC;
import com.hp.hpl.jena.sparql.engine.main.QueryEngineMain;
import com.hp.hpl.jena.sparql.util.Context;

public class QueryEngineFilter extends QueryEngineMain {
	public static QueryEngineFactory getFactory() {
		return factory;
	}

	public static void register() {
		QueryEngineRegistry.addFactory(factory);
	}

	public static void unregister() {
		QueryEngineRegistry.removeFactory(factory);
	}

	/**
	 * Creates {@link QueryEngineFilter} instance.
	 * 
	 * @param query
	 *            Query parameter for query engine.
	 * @param dataset
	 *            Queried dataset.
	 * @param input
	 *            {@link Binding} instance.s
	 * @param context
	 *            {@link Context} parameter of SeagentQueryEngine. It is always
	 *            agent name of the agent of the seagent query engine.
	 */
	public QueryEngineFilter(Query query, DatasetGraph dataset, Binding input,
			Context context) {
		super(query, dataset, input, context);
	}

	/**
	 * Creates {@link QueryEngineFilter} instance.
	 * 
	 * @param op
	 *            {@link Op} instance.S
	 * @param dataset
	 *            Queried dataset.
	 * @param input
	 *            {@link Binding} instance.s
	 * @param context
	 *            {@link Context} parameter of SeagentQueryEngine. It is always
	 *            agent name of the agent of the seagent query engine.
	 */
	public QueryEngineFilter(Op op, DatasetGraph dataset, Binding input,
			Context context) {
		super(op, dataset, input, context);
	}

	/**
	 * This method is starting point for branching SeagentQuerying mechanism
	 * class. It is created same with QueryEngineMain's eval method. We use just
	 * new classes that extend Jena classes. (non-Javadoc)
	 * 
	 * @see com.hp.hpl.jena.sparql.engine.main.QueryEngineMain#eval(com.hp.hpl.jena.sparql.algebra.Op,
	 *      com.hp.hpl.jena.sparql.core.DatasetGraph,
	 *      com.hp.hpl.jena.sparql.engine.binding.Binding,
	 *      com.hp.hpl.jena.sparql.util.Context)
	 */
	@Override
	public QueryIterator eval(Op op, DatasetGraph dsg, Binding input,
			Context context) {
		ExecutionContextFilter execCxt = new ExecutionContextFilter(context,
				dsg.getDefaultGraph(), dsg, QCFilter.getBoundFactory(context));
		QueryIterator qIter1 = QueryIterRoot.create(input, execCxt);
		QueryIterator qIter = QCFilter.execute(op, qIter1, execCxt);
		// Wrap with something to check for closed iterators.
		qIter = QueryIteratorCheck.check(qIter, execCxt);
		// Need call back.
		if (context.isTrue(ARQ.enableExecutionTimeLogging))
			qIter = QueryIteratorTiming.time(qIter);
		return qIter;
	}

	private static QueryEngineFactory factory = new QueryEngineFactory() {
		public boolean accept(Query query, DatasetGraph dataset, Context context) {
			return true;
		}

		public boolean accept(Op op, DatasetGraph dataset, Context context) {
			return true;
		}

		/**
		 * Creates plan for query execution. (non-Javadoc)
		 * 
		 * @see com.hp.hpl.jena.sparql.engine.QueryEngineFactory#create(com.hp.hpl.jena.query.Query,
		 *      com.hp.hpl.jena.sparql.core.DatasetGraph,
		 *      com.hp.hpl.jena.sparql.engine.binding.Binding,
		 *      com.hp.hpl.jena.sparql.util.Context)
		 */
		public Plan create(Query query, DatasetGraph dataset, Binding input,
				Context context) {
			QueryEngineFilter engine = new QueryEngineFilter(query, dataset,
					input, context);
			return engine.getPlan();
		}

		/**
		 * Creates plan for query execution. (non-Javadoc)
		 * 
		 * @see com.hp.hpl.jena.sparql.engine.QueryEngineFactory#create(com.hp.hpl.jena.sparql.algebra.Op,
		 *      com.hp.hpl.jena.sparql.core.DatasetGraph,
		 *      com.hp.hpl.jena.sparql.engine.binding.Binding,
		 *      com.hp.hpl.jena.sparql.util.Context)
		 */
		public Plan create(Op op, DatasetGraph dataset, Binding binding,
				Context context) {
			QueryEngineFilter engine = new QueryEngineFilter(op, dataset,
					binding, context);
			return engine.getPlan();
		}
	};
}
