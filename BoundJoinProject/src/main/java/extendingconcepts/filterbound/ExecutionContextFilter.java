package extendingconcepts.filterbound;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.main.OpExecutorFactory;
import com.hp.hpl.jena.sparql.util.Context;

public class ExecutionContextFilter extends ExecutionContext {
	private OpExecutorFactoryFilter executorFilter = null;

	public ExecutionContextFilter(Context params, Graph activeGraph,
			DatasetGraph dataset, OpExecutorFactory factory) {
		super(params, activeGraph, dataset, factory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hp.hpl.jena.sparql.engine.ExecutionContext#getExecutor()
	 */
	public OpExecutorFactoryFilter getBoundExecutor() {
		return executorFilter;
	}
}
