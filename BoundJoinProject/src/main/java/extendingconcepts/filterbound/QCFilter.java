package extendingconcepts.filterbound;

import java.util.List;

import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.main.QC;
import com.hp.hpl.jena.sparql.util.Context;

public class QCFilter extends QC {
	/**
	 * /** Create {@link QueryIterator} instance over {@link SeagentOpExecutor}
	 * class and with given parameters.
	 * 
	 * @param op
	 *            {@link Op} instance.
	 * @param bindingList
	 *            {@link Binding} {@link List} instance.
	 * @param filterType 
	 * @return bound {@link Op} instance
	 */
	public static List<Op> substitute(Op op, List<Binding> bindingList, String filterType) {
		return SubstituteFilterBound.substitute(op, bindingList,filterType);
	}

	/**
	 * Create {@link QueryIterator} instance over {@link SeagentOpExecutor}
	 * class and with given parameters.
	 * 
	 * @param op
	 *            {@link Op} instance.
	 * @param qIter
	 *            {@link QueryIterator} instance.
	 * @param execCxt
	 *            {@link ExecutionContext} instance.
	 * @return
	 */
	public static QueryIterator execute(Op op, QueryIterator qIter,
			ExecutionContext execCxt) {
		return OpExecutorFilter.execute(op, qIter, execCxt);
	}

	/**
	 * Creates {@link OpExecutorFactoryFilter} instance.
	 * 
	 * @param context
	 *            {@link Context} instance.
	 * @return
	 */
	public static OpExecutorFactoryFilter getBoundFactory(Context context) {
		return (OpExecutorFactoryFilter) context
				.get(ARQConstants.sysOpExecutorFactory);
	}
}
