package extendingconcepts.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIter;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterCommonParent;
import com.hp.hpl.jena.sparql.engine.main.iterator.QueryIterService;
import com.hp.hpl.jena.sparql.util.Utils;

public abstract class QueryIterServiceBound extends QueryIterService {

	protected Logger logger = Logger.getLogger(QueryIterServiceBound.class);
	protected OpService opService;
	protected int iterCount = 0;
	protected QueryIterator currentStage;
	protected List<QueryIterator> iteratorsToBeTraversed = null;

	public QueryIterServiceBound(QueryIterator input, OpService opService,
			ExecutionContext context) {
		super(input, opService, context);
		this.opService = opService;
	}

	@Override
	protected Binding moveToNextBinding() {
		if (!hasNextBinding())
			throw new NoSuchElementException(Utils.className(this)
					+ ".next()/finished");
		return currentStage.nextBinding();
	}

	@Override
	protected boolean hasNextBinding() {
		if (isFinished())
			return false;

		for (;;) {
			if (getCurrentStage() == null) {
				if (iteratorsToBeTraversed == null) {
					iterCount = 0;
					iteratorsToBeTraversed = makeNextStage();
				}
				if (iteratorsToBeTraversed != null
						&& iteratorsToBeTraversed.size() > 0) {
					if (iterCount < iteratorsToBeTraversed.size()) {
						currentStage = iteratorsToBeTraversed.get(iterCount);
						iterCount++;
					} else {
						iteratorsToBeTraversed = null;
						iterCount = 0;
					}
				}
			}

			if (getCurrentStage() == null) {
				return false;
			}

			if (getCurrentStage().hasNext()) {
				return true;
			}

			// finish this step
			getCurrentStage().close();
			currentStage = null;
			// loop
		}
		// Unreachable
	}

	@Override
	protected QueryIterator getCurrentStage() {
		return currentStage;
	}

	@Override
	protected void closeSubIterator() {
		if (currentStage != null)
			currentStage.close();
	}

	private List<QueryIterator> makeNextStage() {

		if (getInput() == null)
			return null;

		if (!getInput().hasNext()) {
			getInput().close();
			return null;
		}

		// generating binding list that holds all bindings in it.
		List<Binding> bindingList = new ArrayList<Binding>();
		while (getInput().hasNext()) {
			Binding binding = (Binding) getInput().next();
			bindingList.add(binding);
		}
		List<QueryIterator> iterators = nextStage(bindingList);
		return iterators;
	}

	protected abstract List<QueryIterator> nextStage(List<Binding> bindingList);

	// /**
	// * This method splits given {@link BindingPair} list into parts according
	// to
	// * their parent bindings.
	// *
	// * @param bindingPairList
	// * {@link BindingPair} list to be split.
	// * @return {@link List} of split {@link BindingPair} list.
	// */
	// List<List<BindingPair>> splitBindingPairs(List<BindingPair>
	// bindingPairList) {
	// // create an empty list of binding pair list
	// List<List<BindingPair>> allBindingPairs = new
	// ArrayList<List<BindingPair>>();
	//
	// // continue if binding pair list is not empty
	// while (bindingPairList != null && !bindingPairList.isEmpty()) {
	// // clear binding pair list and add new binding list to all pair list
	// // until binding list is clean.
	// bindingPairList = createAllBindingPairs(bindingPairList,
	// allBindingPairs);
	// }
	// return allBindingPairs;
	// }

	/**
	 * This method splits given {@link BindingPair} list into parts according to
	 * their parent bindings.
	 * 
	 * @param bindingPairList
	 *            {@link BindingPair} list to be split.
	 * @return {@link List} of split {@link BindingPair} list.
	 */
	List<List<BindingPair>> splitBindingPairs(List<BindingPair> bindingPairList) {
		// create an empty list of binding pair list
		List<List<BindingPair>> allBindingPairs = new ArrayList<List<BindingPair>>();

		HashMap<Binding, List<BindingPair>> parentMap = new HashMap<Binding, List<BindingPair>>();

		for (int i = 0; i < bindingPairList.size(); i++) {
			// get bindingPair
			BindingPair bindingPair = bindingPairList.get(i);
			Binding parentBinding = bindingPair.getParentBinding();
			List<BindingPair> bindingPairListToBeAdd = parentMap
					.get(parentBinding);
			if (bindingPairListToBeAdd == null) {
				bindingPairListToBeAdd = new ArrayList<BindingPair>();
			}
			bindingPairListToBeAdd.add(bindingPair);
			parentMap.put(parentBinding, bindingPairListToBeAdd);
		}
		Set<Binding> keySet = parentMap.keySet();
		for (Binding binding : keySet) {
			allBindingPairs.add(parentMap.get(binding));
		}

		return allBindingPairs;
	}

	/**
	 * This method creates {@link BindingPair} list
	 * 
	 * @param bindingPairList
	 *            {@link BindingPair} list to be splitted
	 * @param allBindingPairs
	 *            {@link List} of {@link BindingPair} lists
	 * @return remaining {@link BindingPair} list after cleaning added pairs to
	 *         allBindingPairs.
	 */
	private List<BindingPair> createAllBindingPairs(
			List<BindingPair> bindingPairList,
			List<List<BindingPair>> allBindingPairs) {
		// set beforeParent and currentParent to null first.
		Binding beforeParent = null;
		Binding currentParent = null;
		// create an empty binding pair list to be add
		List<BindingPair> bindingPairListToBeAdd = new ArrayList<BindingPair>();
		for (int i = 0; i < bindingPairList.size(); i++) {
			// get bindingPair
			BindingPair bindingPair = bindingPairList.get(i);
			// set current parent as parent of bindingPair
			currentParent = bindingPair.getParentBinding();
			if (isSameParent(beforeParent, currentParent)) {
				// add binding to list
				bindingPairListToBeAdd.add(bindingPair);
				beforeParent = currentParent;
				// if binding pair list contains 1 parent (means all parents are
				// same for given binding list)
				if (i == bindingPairList.size() - 1) {
					// add bindingPair list to all binding pairs
					allBindingPairs.add(bindingPairListToBeAdd);
				}
			} else {
				// if before and current are not same
				// add bindingPair list to all binding pairs
				allBindingPairs.add(bindingPairListToBeAdd);
				// clear binding pair list
				bindingPairList = clearBindingPairList(bindingPairList,
						bindingPairListToBeAdd);
				return bindingPairList;
			}
		}
		return null;
	}

	/**
	 * This method extracts elements of second list from first list.
	 * 
	 * @param bindingPairListToBeCleaned
	 * @param bindingListToBeAdd
	 * @return cleaned {@link List}
	 */
	private List<BindingPair> clearBindingPairList(
			List<BindingPair> bindingPairListToBeCleaned,
			List<BindingPair> bindingListToBeAdd) {
		// control whether lists are available for operation
		if (isListsAvailable(bindingPairListToBeCleaned, bindingListToBeAdd)) {
			// remove binding list to be add from binding list to be cleaned
			bindingPairListToBeCleaned.removeAll(bindingListToBeAdd);
		}
		return bindingPairListToBeCleaned;
	}

	/**
	 * This method control whether given two list is null and is empty
	 * 
	 * @param firstList
	 *            first {@link List} to be controlled
	 * @param secondList
	 *            second {@link List} to be controlled
	 * @return availability condition
	 */
	@SuppressWarnings("rawtypes")
	private boolean isListsAvailable(List firstList, List secondList) {
		return secondList != null && !secondList.isEmpty() && firstList != null
				&& !firstList.isEmpty();
	}

	/**
	 * This method controls given two parent binding are same
	 * 
	 * @param beforeBinding
	 * @param currentBinding
	 * @return equality condition
	 */
	private boolean isSameParent(Binding beforeBinding, Binding currentBinding) {
		return (beforeBinding == null && currentBinding == null)
				|| (beforeBinding == null && currentBinding != null)
				|| (beforeBinding != null && currentBinding != null && beforeBinding
						.equals(currentBinding));
	}

	/**
	 * This method generates {@link QueryIterator} list that contains query
	 * results.
	 * 
	 * @param bindingPairList
	 *            binding pair list
	 * @return {@link List} of {@link QueryIterator} instances
	 */
	protected List<QueryIterator> generateQueryIterators(
			List<BindingPair> bindingPairList) {
		// create empty query iterator list
		List<QueryIterator> queryIterators = new ArrayList<QueryIterator>();
//		logger.debug("Binding pairs are beginning to be splitted...");
//		long before = System.currentTimeMillis();
		// split binding pairs according their parent bindings
		List<List<BindingPair>> splittedBindingPairs = splitBindingPairs(bindingPairList);
//		long after = System.currentTimeMillis();
//		logger.debug(MessageFormat.format(
//				"Binding pairs are splitted in \"{0}\" miliseconds", after
//						- before));
		// create iterator for each partial binding pair list
		for (List<BindingPair> partialBindingPairList : splittedBindingPairs) {
			Binding parentBinding = null;
			// create binding list from binding pairs
			List<Binding> bindings = new ArrayList<Binding>();
			for (BindingPair bindingPair : partialBindingPairList) {
				// add binding
				bindings.add(bindingPair.getSelfBinding());
				// set parent binding
				parentBinding = bindingPair.getParentBinding();
			}
			// create new query iterator for partial results (bindings)
			QueryIterSolutionBound queryIterSolutionBound = new QueryIterSolutionBound(
					bindings);
			// create query iterator with parent
			QueryIterator qIter2 = new QueryIterCommonParent(
					queryIterSolutionBound, parentBinding, getExecContext());
			// materalize query iterator
			QueryIter materializedIter = QueryIter.materialize(qIter2,
					getExecContext());
			// add iterator to list
			queryIterators.add(materializedIter);
		}
		return queryIterators;
	}

}
