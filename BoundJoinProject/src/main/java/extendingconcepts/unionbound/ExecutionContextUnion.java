package extendingconcepts.unionbound;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.main.OpExecutorFactory;
import com.hp.hpl.jena.sparql.util.Context;

public class ExecutionContextUnion extends ExecutionContext {

	private OpExecutorFactoryUnion executorUnion = null;

	public ExecutionContextUnion(Context params, Graph activeGraph,
			DatasetGraph dataset, OpExecutorFactory factory) {
		super(params, activeGraph, dataset, factory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hp.hpl.jena.sparql.engine.ExecutionContext#getExecutor()
	 */
	public OpExecutorFactoryUnion getBoundExecutor() {
		return executorUnion;
	}

}
