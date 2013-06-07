package extendingconcepts.unionbound;

import java.util.List;

import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.main.QC;
import com.hp.hpl.jena.sparql.util.Context;



public class QCUnion extends QC {

	/**
	 * /** Create {@link QueryIterator} instance over {@link SeagentOpExecutor}
	 * class and with given parameters.
	 * 
	 * @param op
	 *            {@link Op} instance.
	 * @param bindingList
	 *            {@link Binding} {@link List} instance.
	 * @param unionSize
	 *            TODO
	 * @return bound {@link Op} instance
	 */
	public static List<Op> substitute(Op op, List<Binding> bindingList,
			int unionSize) {
		// first set union size
		SubstituteUnionBound.setUnionSize(unionSize);
		return SubstituteUnionBound.substitute(op, bindingList);
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
		return OpExecutorUnion.execute(op, qIter, execCxt);
	}

	/**
	 * Creates {@link OpExecutorFactoryUnion} instance.
	 * 
	 * @param context
	 *            {@link Context} instance.
	 * @return
	 */
	public static OpExecutorFactoryUnion getBoundFactory(Context context) {
		return (OpExecutorFactoryUnion) context
				.get(ARQConstants.sysOpExecutorFactory);
	}

}
