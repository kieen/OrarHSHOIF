package orar.rolereasoning;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import orar.modeling.ontology.OrarOntology;

public class AxiomOfSpecificTypeGetter {

	public static Set<OWLFunctionalObjectPropertyAxiom> getFunctionalAxioms(OrarOntology ontology) {
		Set<OWLFunctionalObjectPropertyAxiom> functionalAxioms = new HashSet<>();

		for (OWLAxiom axiom : ontology.getTBoxAxioms()) {
			if (axiom.isOfType(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)) {
				OWLFunctionalObjectPropertyAxiom functionalAx = (OWLFunctionalObjectPropertyAxiom) axiom;
				functionalAxioms.add(functionalAx);
			}
		}

		return functionalAxioms;

	}

	public static Set<OWLFunctionalObjectPropertyAxiom> getFunctionalAxioms(Set<OWLAxiom> tboxRboxAxioms) {
		Set<OWLFunctionalObjectPropertyAxiom> functionalAxioms = new HashSet<>();

		for (OWLAxiom axiom : tboxRboxAxioms) {
			if (axiom.isOfType(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)) {
				OWLFunctionalObjectPropertyAxiom functionalAx = (OWLFunctionalObjectPropertyAxiom) axiom;
				functionalAxioms.add(functionalAx);
			}
		}

		return functionalAxioms;

	}

	public static Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseInverseFunctionalPropertyAxioms(
			OrarOntology ontology) {
		Set<OWLInverseFunctionalObjectPropertyAxiom> inverseFunctionalAxioms = new HashSet<>();
		for (OWLAxiom axiom : ontology.getTBoxAxioms()) {
			if (axiom.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)) {

				OWLInverseFunctionalObjectPropertyAxiom inverserFA = (OWLInverseFunctionalObjectPropertyAxiom) axiom;
				inverseFunctionalAxioms.add(inverserFA);
			}
		}

		return inverseFunctionalAxioms;
	}

	public static Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseInverseFunctionalPropertyAxioms(
			Set<OWLAxiom> tboxRboxAxioms) {
		Set<OWLInverseFunctionalObjectPropertyAxiom> inverseFunctionalAxioms = new HashSet<>();
		for (OWLAxiom axiom : tboxRboxAxioms) {
			if (axiom.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)) {

				OWLInverseFunctionalObjectPropertyAxiom inverserFA = (OWLInverseFunctionalObjectPropertyAxiom) axiom;
				inverseFunctionalAxioms.add(inverserFA);
			}
		}

		return inverseFunctionalAxioms;
	}

	public static Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(OrarOntology ontology) {
		Set<OWLInverseObjectPropertiesAxiom> iopAxioms = new HashSet<>();

		for (OWLAxiom axiom : ontology.getTBoxAxioms()) {
			if (axiom.isOfType(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
				OWLInverseObjectPropertiesAxiom iopAxiom = (OWLInverseObjectPropertiesAxiom) axiom;
				iopAxioms.add(iopAxiom);
			}
		}
		return iopAxioms;
	}

	public static Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(Set<OWLAxiom> tboxRboxAxioms) {
		Set<OWLInverseObjectPropertiesAxiom> iopAxioms = new HashSet<>();

		for (OWLAxiom axiom : tboxRboxAxioms) {
			if (axiom.isOfType(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
				OWLInverseObjectPropertiesAxiom iopAxiom = (OWLInverseObjectPropertiesAxiom) axiom;
				iopAxioms.add(iopAxiom);
			}
		}
		return iopAxioms;
	}

	public static Set<OWLSubObjectPropertyOfAxiom> getSubObjectPropertyAxioms(OrarOntology aromaOntology) {
		Set<OWLSubObjectPropertyOfAxiom> sopAxioms = new HashSet<>();
		for (OWLAxiom axiom : aromaOntology.getTBoxAxioms()) {
			if (axiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {
				OWLSubObjectPropertyOfAxiom sopAxiom = (OWLSubObjectPropertyOfAxiom) axiom;
				sopAxioms.add(sopAxiom);
			}
		}
		return sopAxioms;
	}

	public static Set<OWLSubObjectPropertyOfAxiom> getSubObjectPropertyAxioms(Set<OWLAxiom> tboxRboxAxioms) {
		Set<OWLSubObjectPropertyOfAxiom> sopAxioms = new HashSet<>();
		for (OWLAxiom axiom : tboxRboxAxioms) {
			if (axiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {
				OWLSubObjectPropertyOfAxiom sopAxiom = (OWLSubObjectPropertyOfAxiom) axiom;
				sopAxioms.add(sopAxiom);
			}
		}
		return sopAxioms;
	}

	public static Set<OWLSymmetricObjectPropertyAxiom> getSymetricPropertyAxioms(OrarOntology aromaOntology) {
		Set<OWLSymmetricObjectPropertyAxiom> axioms = new HashSet<>();
		for (OWLAxiom axiom : aromaOntology.getTBoxAxioms()) {
			if (axiom.isOfType(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
				OWLSymmetricObjectPropertyAxiom symetricAxiom = (OWLSymmetricObjectPropertyAxiom) axiom;
				axioms.add(symetricAxiom);
			}
		}
		return axioms;
	}

	public static Set<OWLSymmetricObjectPropertyAxiom> getSymetricPropertyAxioms(Set<OWLAxiom> tboxRboxAxioms) {
		Set<OWLSymmetricObjectPropertyAxiom> axioms = new HashSet<>();
		for (OWLAxiom axiom : tboxRboxAxioms) {
			if (axiom.isOfType(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
				OWLSymmetricObjectPropertyAxiom symetricAxiom = (OWLSymmetricObjectPropertyAxiom) axiom;
				axioms.add(symetricAxiom);
			}
		}
		return axioms;
	}

	public static Set<OWLAxiom> getObjectPropertyAxiomsForComputingRoleHierarchy(OrarOntology aromaOntology) {
		Set<OWLAxiom> axioms = new HashSet<>();
		for (OWLAxiom axiom : aromaOntology.getTBoxAxioms()) {
			if (axiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY) || axiom.isOfType(AxiomType.INVERSE_OBJECT_PROPERTIES)
					|| axiom.isOfType(AxiomType.SYMMETRIC_OBJECT_PROPERTY)
					|| axiom.isOfType(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
				axioms.add(axiom);
			}
		}

		return axioms;
	}

	public static Set<OWLAxiom> getObjectPropertyAxiomsForComputingRoleHierarchy(Set<OWLAxiom> tboxRboxAxioms) {
		Set<OWLAxiom> resultingAxioms = new HashSet<>();
		for (OWLAxiom axiom : tboxRboxAxioms) {
			if (axiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY) || axiom.isOfType(AxiomType.INVERSE_OBJECT_PROPERTIES)
					|| axiom.isOfType(AxiomType.SYMMETRIC_OBJECT_PROPERTY)
					|| axiom.isOfType(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)
					|| axiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
				resultingAxioms.add(axiom);
			}
		}

		return resultingAxioms;
	}

	public static Set<OWLTransitiveObjectPropertyAxiom> getTranRoleAxioms(Set<OWLAxiom> tboxRboxAxioms) {
		Set<OWLTransitiveObjectPropertyAxiom> resultingAxioms = new HashSet<>();
		for (OWLAxiom axiom : tboxRboxAxioms) {
			if (axiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
				resultingAxioms.add((OWLTransitiveObjectPropertyAxiom) axiom);
			}
		}
		return resultingAxioms;
	}
}
