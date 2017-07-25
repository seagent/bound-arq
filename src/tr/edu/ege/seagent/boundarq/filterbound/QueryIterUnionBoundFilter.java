package tr.edu.ege.seagent.boundarq.filterbound;

import java.util.List;

import org.apache.jena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterConcat;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterSingleton;
import com.hp.hpl.jena.sparql.engine.main.QC;
import com.hp.hpl.jena.sparql.engine.main.iterator.QueryIterUnion;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.util.Utils;

public class QueryIterUnionBoundFilter extends QueryIterUnion {
	protected List<Op> subOps;

	public QueryIterUnionBoundFilter(QueryIterator input, List<Op> subOps,
			ExecutionContext context) {
		super(input, subOps, context);
		this.subOps = subOps;
	}

	@Override
	protected QueryIterator nextStage(Binding binding) {
		QueryIterConcat unionQIter = new QueryIterConcat(getExecContext());
		for (Op subOp : subOps) {
			subOp = QC.substitute(subOp, binding);
			QueryIterator parent = QueryIterSingleton.create(binding,
					getExecContext());
			QueryIterator qIter = QCFilter.execute(subOp, parent,
					getExecContext());
			unionQIter.add(qIter);
		}
		return unionQIter;
	}

	@Override
	public void output(IndentedWriter out, SerializationContext sCxt) {
		out.println(Utils.className(this));
		out.incIndent();
		for (Op op : subOps)
			op.output(out, sCxt);
		out.decIndent();
		out.ensureStartOfLine();
	}
}
