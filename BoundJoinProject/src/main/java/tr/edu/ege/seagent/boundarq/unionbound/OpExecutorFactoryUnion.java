package tr.edu.ege.seagent.boundarq.unionbound;

import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.main.OpExecutorFactory;


/**
 *This interface used for creating {@link SeagentOpExecutor} instance and
 * implements by {@link OpExecutorFactoryUnion} class.
 * 
 */
public interface OpExecutorFactoryUnion extends OpExecutorFactory {
	OpExecutorUnion create(ExecutionContext execCxt);
}
