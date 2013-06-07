package extendingconcepts.util;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

public class UnionTransformer {

	private Query query;

	public Query getQuery() {
		return query;
	}

	public UnionTransformer(Query query) {
		this.query = query;
	}

	/**
	 * It controls whether given element is not an instance of
	 * {@link ElementUnion} or {@link ElementGroup}
	 * 
	 * @param element
	 *            element to be checked
	 * @return result
	 */
	public boolean isUnionOrGroup(Element element) {
		return element instanceof ElementGroup
				|| element instanceof ElementUnion;
	}

	/**
	 * This method detects union elements, leaf group elements, and leaf union
	 * elements.
	 * 
	 * @param loopingUnionQuey
	 *            query to reorganize for union order.
	 * @param allElementUnions
	 *            all element union list to be filled
	 * @param rootList
	 *            all root list whose elements will be explored
	 */
	public void detectRootElements(Query loopingUnionQuey,
			final List<ElementUnion> allElementUnions,
			final List<Element> rootList) {
		// define an ElementVisitor instance to walk on union elements and edit
		// them
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementUnion el) {
				// first get all union elements
				allElementUnions.add(el);
				// iterate on each leaf of ElementUnion instance
				for (Element subElement : el.getElements()) {
					// check whether leaf is instance of element group
					if (subElement instanceof ElementGroup) {
						// if so, cast it to ElementGroup
						ElementGroup subElementGroup = (ElementGroup) subElement;
						// get elements of leaf ElementGroup instance
						for (Element element : subElementGroup.getElements()) {
							// check whether element is union or group element
							if (isUnionOrGroup(element)) {
								// if so, add it to the root list
								rootList.add(element);
							}
						}
					}
				}
				super.visit(el);
			}

		};
		// operate walk with our ElementVisitor instance
		ElementWalker.walk(loopingUnionQuey.getQueryPattern(), tripleVisitor);
	}

	/**
	 * This method turns the looping union {@link Query} into sequential union
	 * form
	 */
	public void transform() {
		// all union elements including main root element at last
		final List<ElementUnion> allElementUnions = new ArrayList<ElementUnion>();
		// root element list
		final List<Element> rootElements = new ArrayList<Element>();

		// detect and fill inner root elements
		detectRootElements(query, allElementUnions, rootElements);

		// add main root element
		if (allElementUnions.size() > 0) {
			rootElements.add(allElementUnions.get(allElementUnions.size() - 1));
		}

		// flatten each root element and get their leafs that are not
		// ElementUnion or ElementGroup instance
		for (Element root : rootElements) {
			// flatten root element and get flattened elements
			List<Element> flattenedElements = flattenUnion(root);
			// get elements of root
			List<Element> elementsOfMainElement = getElementsOfMainElement(root);
			// clear all elements of root element
			elementsOfMainElement.clear();
			// add flattened elements to root element
			elementsOfMainElement.addAll(flattenedElements);
		}
	}

	/**
	 * This method gets all leafs of given root {@link ElementUnion} instance
	 * 
	 * @param element
	 *            {@link ElementUnion} instance to get its leafs
	 * @return {@link Element} list that are leafs of given root
	 *         {@link ElementUnion}
	 */
	public List<Element> flattenUnion(Element element) {
		// define list for all leafs
		List<Element> allLeafUnionParts = new ArrayList<Element>();

		// iterate on each leaf of root element
		for (Element subElement : getElementsOfMainElement(element)) {
			// define organized element group
			ElementGroup organizedElementGroup = new ElementGroup();
			// check sub element is ElementUnion or ElementGroup
			if (isUnionOrGroup(subElement)) {
				// iterate on element list of sub element
				for (Element elementOfSubElement : getElementsOfMainElement(subElement)) {
					// check whether this element is (union or group) or not
					if (isUnionOrGroup(elementOfSubElement)) {
						// if so, element is an instance of ElementUnion or
						// ElementGroup, and traverse it recursively
						allLeafUnionParts
								.addAll(flattenUnion(elementOfSubElement));
					} else {
						// if not add it to organized element group list
						organizedElementGroup.addElement(elementOfSubElement);
					}
				}
				// add if organized group element is not empty
				if (!organizedElementGroup.isEmpty()) {
					allLeafUnionParts.add(organizedElementGroup);
				}
			} else {
				// if sub element is not an instance of ElementGroup or
				// ElementUnion add it directly to leaf list
				allLeafUnionParts.add(subElement);
			}
		}
		// return detected leaf list
		return allLeafUnionParts;
	}

	/**
	 * This method casts given element into its real form that
	 * {@link ElementGroup} or {@link ElementUnion} and gets all elements
	 * contained by this main element
	 * 
	 * @param main
	 *            {@link Element} whose element list will be got
	 * @return {@link Element} list of main element
	 */
	private List<Element> getElementsOfMainElement(Element mainElement) {
		List<Element> elements = null;
		if (mainElement instanceof ElementGroup) {
			// if so cast it...
			ElementGroup subElementGroup = (ElementGroup) mainElement;
			// iterate on each element of this element group
			elements = subElementGroup.getElements();
		} else if (mainElement instanceof ElementUnion) {
			// if so cast it...<
			ElementUnion subElementUnion = (ElementUnion) mainElement;
			// iterate on each element of this element group
			elements = subElementUnion.getElements();
		}
		return elements;
	}
}
