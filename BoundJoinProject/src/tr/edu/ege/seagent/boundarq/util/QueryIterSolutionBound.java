package tr.edu.ege.seagent.boundarq.util;

import java.util.Iterator;
import java.util.List;

import org.openjena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIteratorBase;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.util.Utils;

public class QueryIterSolutionBound extends QueryIteratorBase {

	private Iterator<Binding> bindingIter;

	public QueryIterSolutionBound(List<Binding> bindingList) {
		super();
		this.bindingIter = bindingList.iterator();
	}

	public void output(IndentedWriter out, SerializationContext sCxt) {
		out.print(Utils.className(this));
	}

	@Override
	protected boolean hasNextBinding() {
		return bindingIter.hasNext();
	}

	@Override
	protected Binding moveToNextBinding() {
		return bindingIter.next();
	}

	@Override
	protected void closeIterator() {
		bindingIter = null;
	}

	@Override
	protected void requestCancel() {
		//Don't need to do anything special to cancel
		//Superclass should take care of that and call closeIterator() where we do our actual clean up
	}

}
