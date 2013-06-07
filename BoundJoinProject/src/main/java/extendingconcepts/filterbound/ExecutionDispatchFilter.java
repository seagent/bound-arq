package extendingconcepts.filterbound;

import java.util.Stack;

import org.openjena.atlas.logging.Log;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitor;
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
import com.hp.hpl.jena.sparql.engine.QueryIterator;

public class ExecutionDispatchFilter implements OpVisitor {
	private Stack<QueryIterator> stack = new Stack<QueryIterator>();
	private OpExecutorFilter opExecutorFilter;

	ExecutionDispatchFilter(OpExecutorFilter exec) {
		opExecutorFilter = exec;
	}

	QueryIterator exec(Op op, QueryIterator input) {
		push(input);
		int x = stack.size();
		op.visit(this);
		int y = stack.size();
		if (x != y)
			Log.warn(this, "Possible stack misalignment");
		QueryIterator qIter = pop();
		return qIter;
	}

	public void visit(OpBGP opBGP) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opBGP, input);
		push(qIter);
	}

	public void visit(OpQuadPattern quadPattern) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(quadPattern, input);
		push(qIter);
	}

	public void visit(OpTriple opTriple) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opTriple, input);
		push(qIter);
	}

	public void visit(OpPath opPath) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opPath, input);
		push(qIter);
	}

	public void visit(OpProcedure opProc) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opProc, input);
		push(qIter);
	}

	public void visit(OpPropFunc opPropFunc) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opPropFunc, input);
		push(qIter);
	}

	public void visit(OpJoin opJoin) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opJoin, input);
		push(qIter);
	}

	public void visit(OpSequence opSequence) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opSequence, input);
		push(qIter);
	}

	public void visit(OpDisjunction opDisjunction) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opDisjunction, input);
		push(qIter);
	}

	public void visit(OpLeftJoin opLeftJoin) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opLeftJoin, input);
		push(qIter);
	}

	public void visit(OpDiff opDiff) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opDiff, input);
		push(qIter);
	}

	public void visit(OpMinus opMinus) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opMinus, input);
		push(qIter);
	}

	public void visit(OpUnion opUnion) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opUnion, input);
		push(qIter);
	}

	public void visit(OpConditional opCondition) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opCondition, input);
		push(qIter);
	}

	public void visit(OpFilter opFilter) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opFilter, input);
		push(qIter);
	}

	public void visit(OpGraph opGraph) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opGraph, input);
		push(qIter);
	}

	public void visit(OpService opService) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opService, input);
		push(qIter);
	}

	public void visit(OpDatasetNames dsNames) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(dsNames, input);
		push(qIter);
	}

	public void visit(OpTable opTable) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opTable, input);
		push(qIter);
	}

	public void visit(OpExt opExt) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opExt, input);
		push(qIter);
	}

	public void visit(OpNull opNull) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opNull, input);
		push(qIter);
	}

	public void visit(OpLabel opLabel) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opLabel, input);
		push(qIter);
	}

	public void visit(OpList opList) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opList, input);
		push(qIter);
	}

	public void visit(OpOrder opOrder) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opOrder, input);
		push(qIter);
	}

	public void visit(OpProject opProject) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opProject, input);
		push(qIter);
	}

	public void visit(OpDistinct opDistinct) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opDistinct, input);
		push(qIter);
	}

	public void visit(OpReduced opReduced) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opReduced, input);
		push(qIter);
	}

	public void visit(OpAssign opAssign) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opAssign, input);
		push(qIter);
	}

	public void visit(OpExtend opExtend) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opExtend, input);
		push(qIter);
	}

	public void visit(OpSlice opSlice) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opSlice, input);
		push(qIter);
	}

	public void visit(OpGroup opGroup) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opGroup, input);
		push(qIter);
	}

	private void push(QueryIterator qIter) {
		stack.push(qIter);
	}

	private QueryIterator pop() {
		if (stack.size() == 0)
			Log.warn(this, "Warning: pop: empty stack");
		return stack.pop();
	}

	public void visit(OpQuad opQuad) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opQuad, input);
		push(qIter);
	}

	public void visit(OpTopN opTop) {
		QueryIterator input = pop();
		QueryIterator qIter = opExecutorFilter.execute(opTop, input);
		push(qIter);
	}
}
