package orar.rolereasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

public class HermitRoleReasoner implements RoleReasoner {

	// output data
	private Reasoner hermit;
	private final Set<OWLObjectProperty> functionalRoles;
	private final Set<OWLObjectProperty> inverseFunctionalRoles;
	private final Set<OWLObjectProperty> transitiveRoles;
	private final Map<OWLObjectProperty, Set<OWLObjectPropertyExpression>> subRoleMaps;
	// Others
	private final Set<OWLAxiom> tboxRboxAxioms;
	private OWLOntology owlOntologyWithRoleAxiomsOnly;
	private OWLDataFactory owlDataFactory;

	public HermitRoleReasoner(Set<OWLAxiom> tboxRboxAxioms) {
		this.functionalRoles = new HashSet<>();
		this.inverseFunctionalRoles = new HashSet<>();
		this.subRoleMaps = new HashMap<>();
		this.transitiveRoles = new HashSet<>();

		this.tboxRboxAxioms = tboxRboxAxioms;
		this.owlDataFactory = OWLManager.getOWLDataFactory();

	}

	/**
	 * create OWLOntology contain only role axioms
	 */
	private void createOWLOntologyWithRoleAxiomsForComputingRoleHierarchy() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			owlOntologyWithRoleAxiomsOnly = manager.createOntology();
			Set<OWLAxiom> axioms = AxiomOfSpecificTypeGetter
					.getObjectPropertyAxiomsForComputingRoleHierarchy(this.tboxRboxAxioms);
			manager.addAxioms(owlOntologyWithRoleAxiomsOnly, axioms);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void roleReasoning() {
		createOWLOntologyWithRoleAxiomsForComputingRoleHierarchy();
		hermit = new Reasoner(owlOntologyWithRoleAxiomsOnly);
		hermit.classifyObjectProperties();
		computRoleHierarchy();
		computeTransitiveRoles();
		computeSubRolesOfDefinedFunctionalRoles();
		computeSubRolesOfDefinedInverseFunctionalRoles();
		computeFunctionalityTakingIntoAccountInverseAxioms();
		computeFunctionalityTakingIntoAccountSymetricRoleAxioms();
		this.functionalRoles.remove(owlDataFactory.getOWLBottomObjectProperty());
		this.inverseFunctionalRoles.remove(owlDataFactory.getOWLBottomObjectProperty());
	}

	private void computRoleHierarchy() {
		Set<OWLObjectProperty> allRoles = owlOntologyWithRoleAxiomsOnly.getObjectPropertiesInSignature(true);
		allRoles.remove(owlDataFactory.getOWLTopObjectProperty());
		for (OWLObjectProperty role : allRoles) {
			Set<OWLObjectPropertyExpression> superRoles = hermit.getSuperObjectProperties(role, false).getFlattened();
			superRoles.remove(owlDataFactory.getOWLTopObjectProperty());
			superRoles.remove(role);
			if (!superRoles.isEmpty()) {
				this.subRoleMaps.put(role, superRoles);
			}
		}
	}

	private void computeTransitiveRoles() {
		Set<OWLTransitiveObjectPropertyAxiom> tranRoleAxioms = AxiomOfSpecificTypeGetter
				.getTranRoleAxioms(tboxRboxAxioms);
		for (OWLTransitiveObjectPropertyAxiom axiom : tranRoleAxioms) {
			Set<OWLObjectProperty> roles = axiom.getObjectPropertiesInSignature();
			this.transitiveRoles.addAll(roles);
		}

	}

	/**
	 * compute sub-roles of defined Functional roles.
	 */
	private void computeSubRolesOfDefinedFunctionalRoles() {
		Set<OWLFunctionalObjectPropertyAxiom> definedFuncAxioms = AxiomOfSpecificTypeGetter
				.getFunctionalAxioms(this.tboxRboxAxioms);
		for (OWLFunctionalObjectPropertyAxiom axiom : definedFuncAxioms) {
			OWLObjectProperty functProperty = axiom.getProperty().asOWLObjectProperty();
			this.functionalRoles.add(functProperty);
			Set<OWLObjectPropertyExpression> subPropertyExpressions = hermit
					.getSubObjectProperties(functProperty, false).getFlattened();
			for (OWLObjectPropertyExpression exp : subPropertyExpressions) {

				OWLObjectProperty subRole;
				if (exp instanceof OWLObjectInverseOf) {
					OWLObjectInverseOf expInverserOf = (OWLObjectInverseOf) exp;
					subRole = expInverserOf.getSimplified().getInverseProperty().getSimplified().asOWLObjectProperty();
					this.inverseFunctionalRoles.add(subRole);
				} else {
					subRole = exp.asOWLObjectProperty();
					this.functionalRoles.add(subRole);
				}

			}
		}
	}

	/**
	 * compute sub-roles of defined InverseFunctional roles.
	 */
	private void computeSubRolesOfDefinedInverseFunctionalRoles() {
		Set<OWLInverseFunctionalObjectPropertyAxiom> definedInverseFuncAxioms = AxiomOfSpecificTypeGetter
				.getInverseInverseFunctionalPropertyAxioms(this.tboxRboxAxioms);
		for (OWLInverseFunctionalObjectPropertyAxiom axiom : definedInverseFuncAxioms) {
			OWLObjectProperty inverseFunctProperty = axiom.getProperty().asOWLObjectProperty();
			this.inverseFunctionalRoles.add(inverseFunctProperty);
			Set<OWLObjectPropertyExpression> subPropertyExpressions = hermit
					.getSubObjectProperties(inverseFunctProperty, false).getFlattened();
			for (OWLObjectPropertyExpression exp : subPropertyExpressions) {

				OWLObjectProperty subRole;
				if (exp instanceof OWLObjectInverseOf) {
					OWLObjectInverseOf expInverserOf = (OWLObjectInverseOf) exp;
					subRole = expInverserOf.getSimplified().getInverseProperty().getSimplified().asOWLObjectProperty();
					this.functionalRoles.add(subRole);
				} else {
					subRole = exp.asOWLObjectProperty();
					this.inverseFunctionalRoles.add(subRole);
				}

			}
		}
	}

	/**
	 * Compute (inverse)functional roles taking InverseAxioms into account. This
	 * method should be called after
	 * {@link #computeSubRolesOfDefinedFunctionalRoles() and
	 * #computeSubRolesOfDefinedInverseFunctionalRoles()}
	 */
	private void computeFunctionalityTakingIntoAccountInverseAxioms() {
		Set<OWLInverseObjectPropertiesAxiom> inverseRoleAxioms = AxiomOfSpecificTypeGetter
				.getInverseObjectPropertyAxioms(this.tboxRboxAxioms);

		for (OWLInverseObjectPropertiesAxiom axiom : inverseRoleAxioms) {
			OWLObjectProperty firstRole = axiom.getFirstProperty().asOWLObjectProperty();
			OWLObjectProperty secondRole = axiom.getSecondProperty().asOWLObjectProperty();
			if (this.functionalRoles.contains(firstRole)) {
				this.inverseFunctionalRoles.add(secondRole);
			}

			if (this.inverseFunctionalRoles.contains(firstRole)) {
				this.functionalRoles.add(secondRole);
			}

			if (this.functionalRoles.contains(secondRole)) {
				this.inverseFunctionalRoles.add(firstRole);
			}

			if (this.inverseFunctionalRoles.contains(secondRole)) {
				this.functionalRoles.add(firstRole);
			}

		}

	}

	/**
	 * Compute (inverse)functional roles taking SymetricPropertyAxiom into
	 * account. This method should be called after
	 * {@link #computeSubRolesOfDefinedFunctionalRoles() and
	 * #computeSubRolesOfDefinedInverseFunctionalRoles()}
	 */
	private void computeFunctionalityTakingIntoAccountSymetricRoleAxioms() {
		Set<OWLSymmetricObjectPropertyAxiom> symetricAxioms = AxiomOfSpecificTypeGetter
				.getSymetricPropertyAxioms(this.tboxRboxAxioms);

		for (OWLSymmetricObjectPropertyAxiom axiom : symetricAxioms) {
			OWLObjectProperty role = axiom.getProperty().asOWLObjectProperty();

			if (this.functionalRoles.contains(role)) {
				this.inverseFunctionalRoles.add(role);
			}

			if (this.inverseFunctionalRoles.contains(role)) {
				this.functionalRoles.add(role);
			}

		}

	}

	@Override
	public Set<OWLObjectProperty> getFunctionalRoles() {

		return this.functionalRoles;
	}

	@Override
	public Set<OWLObjectProperty> getInverseFunctionalRoles() {

		return this.inverseFunctionalRoles;
	}

	@Override
	public Map<OWLObjectProperty, Set<OWLObjectPropertyExpression>> getRoleHierarchyAsMap() {
		return this.subRoleMaps;
	}

	@Override
	public Set<OWLObjectProperty> getTransitiveRoles() {

		return this.transitiveRoles;
	}

}
