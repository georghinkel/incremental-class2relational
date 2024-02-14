/*
 * SPDX-FileCopyrightText: 2021-2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */

/*
 * generated by Xtext 2.25.0
 */
package tools.refinery.language.validation;

import com.google.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;
import org.jetbrains.annotations.Nullable;
import tools.refinery.language.model.problem.*;
import tools.refinery.language.utils.ProblemDesugarer;
import tools.refinery.language.utils.ProblemUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class contains custom validation rules.
 * <p>
 * See
 * <a href="https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation">...</a>
 */
public class ProblemValidator extends AbstractProblemValidator {
	private static final String ISSUE_PREFIX = "tools.refinery.language.validation.ProblemValidator.";

	public static final String SINGLETON_VARIABLE_ISSUE = ISSUE_PREFIX + "SINGLETON_VARIABLE";

	public static final String NODE_CONSTANT_ISSUE = ISSUE_PREFIX + "NODE_CONSTANT_ISSUE";

	public static final String DUPLICATE_NAME_ISSUE = ISSUE_PREFIX + "DUPLICATE_NAME";

	public static final String INVALID_MULTIPLICITY_ISSUE = ISSUE_PREFIX + "INVALID_MULTIPLICITY";

	public static final String ZERO_MULTIPLICITY_ISSUE = ISSUE_PREFIX + "ZERO_MULTIPLICITY";

	public static final String MISSING_OPPOSITE_ISSUE = ISSUE_PREFIX + "MISSING_OPPOSITE";

	public static final String INVALID_OPPOSITE_ISSUE = ISSUE_PREFIX + "INVALID_OPPOSITE";

	public static final String INVALID_SUPERTYPE_ISSUE = ISSUE_PREFIX + "INVALID_SUPERTYPE";

	public static final String INVALID_REFERENCE_TYPE_ISSUE = ISSUE_PREFIX + "INVALID_REFERENCE_TYPE";

	public static final String INVALID_ARITY_ISSUE = ISSUE_PREFIX + "INVALID_ARITY";

	public static final String INVALID_TRANSITIVE_CLOSURE_ISSUE = ISSUE_PREFIX + "INVALID_TRANSITIVE_CLOSURE";

	public static final String INVALID_VALUE_ISSUE = ISSUE_PREFIX + "INVALID_VALUE";

	public static final String UNSUPPORTED_ASSERTION_ISSUE = ISSUE_PREFIX + "UNSUPPORTED_ASSERTION";

	@Inject
	private ReferenceCounter referenceCounter;

	@Inject
	private ProblemDesugarer desugarer;

	@Check
	public void checkSingletonVariable(VariableOrNodeExpr expr) {
		var variableOrNode = expr.getVariableOrNode();
		if (variableOrNode instanceof Variable variable && ProblemUtil.isImplicitVariable(variable)
				&& !ProblemUtil.isSingletonVariable(variable)) {
			var problem = EcoreUtil2.getContainerOfType(variable, Problem.class);
			if (problem != null && referenceCounter.countReferences(problem, variable) <= 1) {
				var name = variable.getName();
				var message = ("Variable '%s' has only a single reference. " +
						"Add another reference or mark is as a singleton variable: '_%s'").formatted(name, name);
				warning(message, expr, ProblemPackage.Literals.VARIABLE_OR_NODE_EXPR__VARIABLE_OR_NODE,
						INSIGNIFICANT_INDEX, SINGLETON_VARIABLE_ISSUE);
			}
		}
	}

	@Check
	public void checkNodeConstants(VariableOrNodeExpr expr) {
		var variableOrNode = expr.getVariableOrNode();
		if (variableOrNode instanceof Node node && !ProblemUtil.isIndividualNode(node)) {
			var name = node.getName();
			var message = ("Only individuals can be referenced in predicates. " +
					"Mark '%s' as individual with the declaration 'indiv %s.'").formatted(name, name);
			error(message, expr, ProblemPackage.Literals.VARIABLE_OR_NODE_EXPR__VARIABLE_OR_NODE,
					INSIGNIFICANT_INDEX, NODE_CONSTANT_ISSUE);
		}
	}

	@Check
	public void checkUniqueDeclarations(Problem problem) {
		var relations = new ArrayList<Relation>();
		var individuals = new ArrayList<Node>();
		for (var statement : problem.getStatements()) {
			if (statement instanceof Relation relation) {
				relations.add(relation);
			} else if (statement instanceof IndividualDeclaration individualDeclaration) {
				individuals.addAll(individualDeclaration.getNodes());
			}
		}
		checkUniqueSimpleNames(relations);
		checkUniqueSimpleNames(individuals);
	}

	@Check
	public void checkUniqueFeatures(ClassDeclaration classDeclaration) {
		checkUniqueSimpleNames(classDeclaration.getFeatureDeclarations());
	}

	@Check
	public void checkUniqueLiterals(EnumDeclaration enumDeclaration) {
		checkUniqueSimpleNames(enumDeclaration.getLiterals());
	}

	protected void checkUniqueSimpleNames(Iterable<? extends NamedElement> namedElements) {
		var names = new LinkedHashMap<String, Set<NamedElement>>();
		for (var namedElement : namedElements) {
			var name = namedElement.getName();
			var objectsWithName = names.computeIfAbsent(name, ignored -> new LinkedHashSet<>());
			objectsWithName.add(namedElement);
		}
		for (var entry : names.entrySet()) {
			var objectsWithName = entry.getValue();
			if (objectsWithName.size() <= 1) {
				continue;
			}
			var name = entry.getKey();
			var message = "Duplicate name '%s'.".formatted(name);
			for (var namedElement : objectsWithName) {
				acceptError(message, namedElement, ProblemPackage.Literals.NAMED_ELEMENT__NAME, 0,
						DUPLICATE_NAME_ISSUE);
			}
		}
	}

	@Check
	public void checkRangeMultiplicity(RangeMultiplicity rangeMultiplicity) {
		int lower = rangeMultiplicity.getLowerBound();
		int upper = rangeMultiplicity.getUpperBound();
		if (upper >= 0 && lower > upper) {
			var message = "Multiplicity range [%d..%d] is inconsistent.";
			acceptError(message, rangeMultiplicity, null, 0, INVALID_MULTIPLICITY_ISSUE);
		}
	}

	@Check
	public void checkReferenceMultiplicity(ReferenceDeclaration referenceDeclaration) {
		var multiplicity = referenceDeclaration.getMultiplicity();
		if (multiplicity == null) {
			return;
		}
		if (ProblemUtil.isContainerReference(referenceDeclaration) && (
				!(multiplicity instanceof RangeMultiplicity rangeMultiplicity) ||
						rangeMultiplicity.getLowerBound() != 0 ||
						rangeMultiplicity.getUpperBound() != 1)) {
			var message = "The only allowed multiplicity for container references is [0..1]";
			acceptError(message, multiplicity, null, 0, INVALID_MULTIPLICITY_ISSUE);
		}
		if ((multiplicity instanceof ExactMultiplicity exactMultiplicity &&
				exactMultiplicity.getExactValue() == 0) ||
				(multiplicity instanceof RangeMultiplicity rangeMultiplicity &&
						rangeMultiplicity.getLowerBound() == 0 &&
						rangeMultiplicity.getUpperBound() == 0)) {
			var message = "The multiplicity constraint does not allow any reference links";
			acceptWarning(message, multiplicity, null, 0, ZERO_MULTIPLICITY_ISSUE);
		}
	}

	@Check
	public void checkOpposite(ReferenceDeclaration referenceDeclaration) {
		var opposite = referenceDeclaration.getOpposite();
		if (opposite == null || opposite.eIsProxy()) {
			return;
		}
		var oppositeOfOpposite = opposite.getOpposite();
		if (oppositeOfOpposite == null) {
			acceptError("Reference '%s' does not declare '%s' as an opposite."
							.formatted(opposite.getName(), referenceDeclaration.getName()),
					referenceDeclaration, ProblemPackage.Literals.REFERENCE_DECLARATION__OPPOSITE, 0,
					INVALID_OPPOSITE_ISSUE);
			var oppositeResource = opposite.eResource();
			if (oppositeResource != null && oppositeResource.equals(referenceDeclaration.eResource())) {
				acceptError("Missing opposite '%s' for reference '%s'."
								.formatted(referenceDeclaration.getName(), opposite.getName()),
						opposite, ProblemPackage.Literals.NAMED_ELEMENT__NAME, 0, MISSING_OPPOSITE_ISSUE);
			}
			return;
		}
		if (!referenceDeclaration.equals(oppositeOfOpposite)) {
			var messageBuilder = new StringBuilder()
					.append("Expected reference '")
					.append(opposite.getName())
					.append("' to have opposite '")
					.append(referenceDeclaration.getName())
					.append("'");
			var oppositeOfOppositeName = oppositeOfOpposite.getName();
			if (oppositeOfOppositeName != null) {
				messageBuilder.append(", got '")
						.append(oppositeOfOppositeName)
						.append("' instead");
			}
			messageBuilder.append(".");
			acceptError(messageBuilder.toString(), referenceDeclaration,
					ProblemPackage.Literals.REFERENCE_DECLARATION__OPPOSITE, 0, INVALID_OPPOSITE_ISSUE);
		}
	}

	@Check
	public void checkContainerOpposite(ReferenceDeclaration referenceDeclaration) {
		var kind = referenceDeclaration.getKind();
		var opposite = referenceDeclaration.getOpposite();
		if (opposite != null && opposite.eIsProxy()) {
			// If {@code opposite} is a proxy, we have already emitted a linker error.
			return;
		}
		if (kind == ReferenceKind.CONTAINMENT) {
			if (opposite != null && opposite.getKind() == ReferenceKind.CONTAINMENT) {
				acceptError("Opposite '%s' of containment reference '%s' is not a container reference."
								.formatted(opposite.getName(), referenceDeclaration.getName()),
						referenceDeclaration, ProblemPackage.Literals.REFERENCE_DECLARATION__OPPOSITE, 0,
						INVALID_OPPOSITE_ISSUE);
			}
		} else if (kind == ReferenceKind.CONTAINER) {
			if (opposite == null) {
				acceptError("Container reference '%s' requires an opposite.".formatted(referenceDeclaration.getName()),
						referenceDeclaration, ProblemPackage.Literals.NAMED_ELEMENT__NAME, 0, MISSING_OPPOSITE_ISSUE);
			} else if (opposite.getKind() != ReferenceKind.CONTAINMENT) {
				acceptError("Opposite '%s' of container reference '%s' is not a containment reference."
								.formatted(opposite.getName(), referenceDeclaration.getName()),
						referenceDeclaration, ProblemPackage.Literals.REFERENCE_DECLARATION__OPPOSITE, 0,
						INVALID_OPPOSITE_ISSUE);
			}
		}
	}

	@Check
	public void checkSupertypes(ClassDeclaration classDeclaration) {
		var supertypes = classDeclaration.getSuperTypes();
		int supertypeCount = supertypes.size();
		for (int i = 0; i < supertypeCount; i++) {
			var supertype = supertypes.get(i);
			if (!supertype.eIsProxy() && !(supertype instanceof ClassDeclaration)) {
				var message = "Supertype '%s' of '%s' is not a class."
						.formatted(supertype.getName(), classDeclaration.getName());
				acceptError(message, classDeclaration, ProblemPackage.Literals.CLASS_DECLARATION__SUPER_TYPES, i,
						INVALID_SUPERTYPE_ISSUE);
			}
		}
	}

	@Check
	public void checkReferenceType(ReferenceDeclaration referenceDeclaration) {
		if (referenceDeclaration.getKind() == ReferenceKind.REFERENCE &&
				!ProblemUtil.isContainerReference(referenceDeclaration)) {
			checkArity(referenceDeclaration, ProblemPackage.Literals.REFERENCE_DECLARATION__REFERENCE_TYPE, 1);
			return;
		}
		var referenceType = referenceDeclaration.getReferenceType();
		if (referenceType == null || referenceType.eIsProxy() || referenceType instanceof ClassDeclaration) {
			// Either correct, or a missing reference type where we are probably already emitting another error.
			return;
		}
		var message = "Reference type '%s' of the containment or container reference '%s' is not a class."
				.formatted(referenceType.getName(), referenceDeclaration.getName());
		acceptError(message, referenceDeclaration, ProblemPackage.Literals.REFERENCE_DECLARATION__REFERENCE_TYPE, 0,
				INVALID_REFERENCE_TYPE_ISSUE);
	}

	@Check
	public void checkParameterType(Parameter parameter) {
		checkArity(parameter, ProblemPackage.Literals.PARAMETER__PARAMETER_TYPE, 1);
	}

	@Check
	public void checkAtom(Atom atom) {
		int argumentCount = atom.getArguments().size();
		checkArity(atom, ProblemPackage.Literals.ATOM__RELATION, argumentCount);
		if (atom.isTransitiveClosure() && argumentCount != 2) {
			var message = "Transitive closure needs exactly 2 arguments, got %d arguments instead."
					.formatted(argumentCount);
			acceptError(message, atom, ProblemPackage.Literals.ATOM__TRANSITIVE_CLOSURE, 0,
					INVALID_TRANSITIVE_CLOSURE_ISSUE);
		}
	}

	@Check
	public void checkAssertion(Assertion assertion) {
		int argumentCount = assertion.getArguments().size();
		if (!(assertion.getValue() instanceof LogicConstant)) {
			var message = "Assertion value must be one of 'true', 'false', 'unknown', or 'error'.";
			acceptError(message, assertion, ProblemPackage.Literals.ASSERTION__VALUE, 0, INVALID_VALUE_ISSUE);
		}
		checkArity(assertion, ProblemPackage.Literals.ASSERTION__RELATION, argumentCount);
	}

	@Check
	public void checkTypeScope(TypeScope typeScope) {
		checkArity(typeScope, ProblemPackage.Literals.TYPE_SCOPE__TARGET_TYPE, 1);
	}

	private void checkArity(EObject eObject, EReference reference, int expectedArity) {
		var value = eObject.eGet(reference);
		if (!(value instanceof Relation relation) || relation.eIsProxy()) {
			// Feature does not point to a {@link Relation}, we are probably already emitting another error.
			return;
		}
		int arity = ProblemUtil.getArity(relation);
		if (arity == expectedArity) {
			return;
		}
		var message = "Expected symbol '%s' to have arity %d, got arity %d instead."
				.formatted(relation.getName(), expectedArity, arity);
		acceptError(message, eObject, reference, 0, INVALID_ARITY_ISSUE);
	}

	@Check
	public void checkMultiObjectAssertion(Assertion assertion) {
		var builtinSymbolsOption = desugarer.getBuiltinSymbols(assertion);
		if (builtinSymbolsOption.isEmpty()) {
			return;
		}
		var builtinSymbols = builtinSymbolsOption.get();
		var relation = assertion.getRelation();
		boolean isExists = builtinSymbols.exists().equals(relation);
		boolean isEquals = builtinSymbols.equals().equals(relation);
		if ((!isExists && !isEquals) || !(assertion.getValue() instanceof LogicConstant logicConstant)) {
			return;
		}
		var value = logicConstant.getLogicValue();
		if (assertion.isDefault()) {
			acceptError("Default assertions for 'exists' and 'equals' are not supported.", assertion,
					ProblemPackage.Literals.ASSERTION__DEFAULT, 0, UNSUPPORTED_ASSERTION_ISSUE);
			return;
		}
		if (value == LogicValue.ERROR) {
			acceptError("Error assertions for 'exists' and 'equals' are not supported.", assertion,
					ProblemPackage.Literals.ASSERTION__DEFAULT, 0, UNSUPPORTED_ASSERTION_ISSUE);
			return;
		}
		if (isExists) {
			checkExistsAssertion(assertion, value);
			return;
		}
		checkEqualsAssertion(assertion, value);
	}

	private void checkExistsAssertion(Assertion assertion, LogicValue value) {
		if (value == LogicValue.TRUE || value == LogicValue.UNKNOWN) {
			// {@code true} is always a valid value for {@code exists}, while {@code unknown} values may always be
			// refined to {@code true} if necessary (e.g., for individual nodes).
			return;
		}
		var arguments = assertion.getArguments();
		if (arguments.isEmpty()) {
			// We already report an error on invalid arity.
			return;
		}
		var node = getNodeArgumentForMultiObjectAssertion(arguments.get(0));
		if (node != null && !node.eIsProxy() && ProblemUtil.isIndividualNode(node)) {
			acceptError("Individual nodes must exist.", assertion, null, 0, UNSUPPORTED_ASSERTION_ISSUE);
		}
	}

	private void checkEqualsAssertion(Assertion assertion, LogicValue value) {
		var arguments = assertion.getArguments();
		if (arguments.size() < 2) {
			// We already report an error on invalid arity.
			return;
		}
		var left = getNodeArgumentForMultiObjectAssertion(arguments.get(0));
		var right = getNodeArgumentForMultiObjectAssertion(arguments.get(1));
		if (left == null || left.eIsProxy() || right == null || right.eIsProxy()) {
			return;
		}
		if (left.equals(right)) {
			if (value == LogicValue.FALSE || value == LogicValue.ERROR) {
				acceptError("A node cannot be necessarily unequal to itself.", assertion, null, 0,
						UNSUPPORTED_ASSERTION_ISSUE);
			}
		} else {
			if (value != LogicValue.FALSE) {
				acceptError("Equalities between distinct nodes are not supported.", assertion, null, 0,
						UNSUPPORTED_ASSERTION_ISSUE);
			}
		}
	}

	@Nullable
	private Node getNodeArgumentForMultiObjectAssertion(AssertionArgument argument) {
		if (argument instanceof WildcardAssertionArgument) {
			acceptError("Wildcard arguments for 'exists' are not supported.", argument, null, 0,
					UNSUPPORTED_ASSERTION_ISSUE);
			return null;
		}
		if (argument instanceof NodeAssertionArgument nodeAssertionArgument) {
			return nodeAssertionArgument.getNode();
		}
		throw new IllegalArgumentException("Unknown assertion argument: " + argument);
	}
}
