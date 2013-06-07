package tr.edu.ege.seagent.boundarq.filterbound;

import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.main.OpExecutorFactory;

public interface OpExecutorFactoryFilter extends OpExecutorFactory {
	OpExecutorFilter create(ExecutionContext execCxt);
}
