package tr.edu.ege.seagent.boundarq.filterbound;

import java.util.List;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpConditional;
import com.hp.hpl.jena.sparql.algebra.op.OpDatasetNames;
import com.hp.hpl.jena.sparql.algebra.op.OpDiff;
import com.hp.hpl.jena.sparql.algebra.op.OpDisjunction;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExt;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLabel;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpList;
import com.hp.hpl.jena.sparql.algebra.op.OpMinus;
import com.hp.hpl.jena.sparql.algebra.op.OpNull;
import com.hp.hpl.jena.sparql.algebra.op.OpOrder;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProcedure;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuad;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpReduced;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpSlice;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpTopN;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.main.OpExecutor;

public class OpExecutorFilter extends OpExecutor {
	protected OpExecutorFilter(ExecutionContext execCxt) {
		super(execCxt);
		this.execCxt = execCxt;
		dispatcherFilter = new ExecutionDispatchFilter(this);
	}

	/**
	 * {@link SeagentExecutionDispatch} instance.
	 */
	protected ExecutionDispatchFilter dispatcherFilter = null;

	// Set this to a different factory implementation to have a different
	// OpExecutor.
	protected static final OpExecutorFactoryFilter factoryBound = new OpExecutorFactoryFilter() {
		public OpExecutorFilter create(ExecutionContext execCxt) {
			return new OpExecutorFilter(execCxt);
		}
	};

	/**
	 * TODO Javadoc yazilacak.
	 * 
	 * @param op
	 * @param qIter
	 * @param execCxt
	 * @return
	 */
	// Public interface is via QC.execute.
	static QueryIterator execute(Op op, QueryIterator qIter,
			ExecutionContext execCxt) {
		OpExecutorFilter exec = createOpExecutor(execCxt);
		QueryIterator q = exec.executeOp(op, qIter);
		return q;
	}

	/**
	 * {@link #dispatcherFilter} is used to execute op and input.
	 * 
	 * @see com.hp.hpl.jena.sparql.engine.main.OpExecutor#executeOp(com.hp.hpl.jena
	 *      .sparql.algebra.Op, com.hp.hpl.jena.sparql.engine.QueryIterator)
	 */
	@Override
	public QueryIterator executeOp(Op op, QueryIterator input) {
		level++;
		QueryIterator qIter = dispatcherFilter.exec(op, input);
		// Intentionally not try/finally so exceptions leave some evidence
		// around.
		level--;
		return qIter;
	}

	/**
	 * TODO Javadoc yazilacak.
	 * 
	 * @param execCxt
	 * @return
	 */
	private static OpExecutorFilter createOpExecutor(ExecutionContext execCxt) {
		OpExecutorFactoryFilter factory = ((ExecutionContextFilter) execCxt)
				.getBoundExecutor();
		if (factory == null)
			factory = factoryBound;
		if (factory == null)
			return new OpExecutorFilter(execCxt);
		return factory.create(execCxt);
	}

	@Override
	protected QueryIterator execute(OpBGP opBGP, QueryIterator input) {
		return super.execute(opBGP, input);
	}

	@Override
	protected QueryIterator execute(OpTriple opTriple, QueryIterator input) {
		return super.execute(opTriple, input);
	}

	@Override
	protected QueryIterator execute(OpQuadPattern quadPattern,
			QueryIterator input) {
		return super.execute(quadPattern, input);
	}

	@Override
	protected QueryIterator execute(OpPath opPath, QueryIterator input) {
		return super.execute(opPath, input);
	}

	@Override
	protected QueryIterator execute(OpProcedure opProc, QueryIterator input) {
		return super.execute(opProc, input);
	}

	@Override
	protected QueryIterator execute(OpPropFunc opPropFunc, QueryIterator input) {
		return super.execute(opPropFunc, input);
	}

	@Override
	protected QueryIterator execute(OpJoin opJoin, QueryIterator input) {
		return super.execute(opJoin, input);
	}

	@Override
	protected QueryIterator execute(OpSequence opSequence, QueryIterator input) {
		return super.execute(opSequence, input);
	}

	@Override
	protected QueryIterator execute(OpDisjunction opDisjunction,
			QueryIterator input) {
		return super.execute(opDisjunction, input);
	}

	@Override
	protected QueryIterator execute(OpLeftJoin opLeftJoin, QueryIterator input) {
		return super.execute(opLeftJoin, input);
	}

	@Override
	protected QueryIterator execute(OpConditional opCondition,
			QueryIterator input) {
		return super.execute(opCondition, input);
	}

	@Override
	protected QueryIterator execute(OpDiff opDiff, QueryIterator input) {
		return super.execute(opDiff, input);
	}

	@Override
	protected QueryIterator execute(OpMinus opMinus, QueryIterator input) {
		return super.execute(opMinus, input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hp.hpl.jena.sparql.engine.main.OpExecutor#execute(com.hp.hpl.jena
	 * .sparql.algebra.op.OpService,
	 * com.hp.hpl.jena.sparql.engine.QueryIterator)
	 */
	@Override
	protected QueryIterator execute(OpService opService, QueryIterator input) {
		return new QueryIterServiceFilter(input, opService, this.execCxt);
	}

	/*
	 * (non-Javadoc)FIXME:OPUnion gerçekleştirilecek
	 * 
	 * @see
	 * com.hp.hpl.jena.sparql.engine.main.OpExecutor#execute(com.hp.hpl.jena
	 * .sparql.algebra.op.OpUnion, com.hp.hpl.jena.sparql.engine.QueryIterator)
	 */
	@Override
	protected QueryIterator execute(OpUnion opUnion, QueryIterator input) {
		List<Op> x = flattenUnion(opUnion);
		return new QueryIterUnionBoundFilter(input, x, execCxt);
	}

	@Override
	protected List<Op> flattenUnion(OpUnion opUnion) {
		return super.flattenUnion(opUnion);
	}

	@Override
	protected void flattenUnion(List<Op> acc, OpUnion opUnion) {
		super.flattenUnion(acc, opUnion);
	}

	@Override
	protected QueryIterator execute(OpFilter opFilter, QueryIterator input) {
		return super.execute(opFilter, input);
	}

	@Override
	protected QueryIterator execute(OpGraph opGraph, QueryIterator input) {
		return super.execute(opGraph, input);
	}

	@Override
	protected QueryIterator execute(OpDatasetNames dsNames, QueryIterator input) {
		return super.execute(dsNames, input);
	}

	@Override
	protected QueryIterator execute(OpTable opTable, QueryIterator input) {
		return super.execute(opTable, input);
	}

	@Override
	protected QueryIterator execute(OpExt opExt, QueryIterator input) {
		return super.execute(opExt, input);
	}

	@Override
	protected QueryIterator execute(OpLabel opLabel, QueryIterator input) {
		return super.execute(opLabel, input);
	}

	@Override
	protected QueryIterator execute(OpNull opNull, QueryIterator input) {
		return super.execute(opNull, input);
	}

	@Override
	protected QueryIterator execute(OpList opList, QueryIterator input) {
		return super.execute(opList, input);
	}

	@Override
	protected QueryIterator execute(OpOrder opOrder, QueryIterator input) {
		return super.execute(opOrder, input);
	}

	@Override
	protected QueryIterator execute(OpProject opProject, QueryIterator input) {
		return super.execute(opProject, input);
	}

	@Override
	protected QueryIterator execute(OpSlice opSlice, QueryIterator input) {
		return super.execute(opSlice, input);
	}

	@Override
	protected QueryIterator execute(OpGroup opGroup, QueryIterator input) {
		return super.execute(opGroup, input);
	}

	@Override
	protected QueryIterator execute(OpDistinct opDistinct, QueryIterator input) {
		return super.execute(opDistinct, input);
	}

	@Override
	protected QueryIterator execute(OpReduced opReduced, QueryIterator input) {
		return super.execute(opReduced, input);
	}

	@Override
	protected QueryIterator execute(OpAssign opAssign, QueryIterator input) {
		return super.execute(opAssign, input);
	}

	@Override
	protected QueryIterator execute(OpExtend opExtend, QueryIterator input) {
		return super.execute(opExtend, input);
	}

	@Override
	protected QueryIterator execute(OpTopN opTop, QueryIterator input) {
		return super.execute(opTop, input);
	}

	protected QueryIterator execute(OpQuad opQuad, QueryIterator input) {
		return super.execute(opQuad, input);
	}

	@Override
	protected QueryIterator root() {
		return super.root();
	}
}
