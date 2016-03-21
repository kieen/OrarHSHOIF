package orar.modeling.conceptassertion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class MapbasedConceptAssertionBox implements ConceptAssertionBox {

	private final Map<OWLNamedIndividual, Set<OWLClass>> conceptAssertionMap;
	private int numberOfConceptAssertions; // Note that it will be increased
											// only when adding one by one
											// assertion.

	public MapbasedConceptAssertionBox() {
		this.conceptAssertionMap = new HashMap<OWLNamedIndividual, Set<OWLClass>>();
		this.numberOfConceptAssertions = 0;
	}

	@Override
	public Set<OWLClass> getAssertedConcepts(OWLNamedIndividual individual) {

		Set<OWLClass> assertedConcepts = this.conceptAssertionMap.get(individual);
		if (assertedConcepts != null) {
			return assertedConcepts;
		}
		return new HashSet<>();

	}

	@Override
	public Set<OWLNamedIndividual> getAllIndividuals() {
		return this.conceptAssertionMap.keySet();
	}

	@Override
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertions() {
		Set<OWLClassAssertionAxiom> classAssertionAxioms = new HashSet<>();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory owlDataFactory = manager.getOWLDataFactory();

		Iterator<Entry<OWLNamedIndividual, Set<OWLClass>>> iterator = conceptAssertionMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<OWLNamedIndividual, Set<OWLClass>> entry = iterator.next();
			OWLNamedIndividual ind = entry.getKey();
			Set<OWLClass> assertedClasses = entry.getValue();
			for (OWLClass owlClass : assertedClasses) {
				OWLClassAssertionAxiom classAssertion = owlDataFactory.getOWLClassAssertionAxiom(owlClass, ind);
				classAssertionAxioms.add(classAssertion);
			}
		}

		return classAssertionAxioms;
	}

	@Override
	public boolean addManyConceptAssertions(OWLNamedIndividual individual, Set<OWLClass> concepts) {

		Set<OWLClass> existingClasses = this.conceptAssertionMap.get(individual);
		if (existingClasses == null) {
			existingClasses = new HashSet<>();
		}
		boolean hasNewElement = existingClasses.addAll(concepts);
		this.conceptAssertionMap.put(individual, existingClasses);
		return hasNewElement;
	}

	@Override
	public boolean addConceptAssertion(OWLNamedIndividual individual, OWLClass concept) {
		Set<OWLClass> existingClasses = this.conceptAssertionMap.get(individual);
		if (existingClasses == null) {
			existingClasses = new HashSet<>();
		}
		boolean hasNewElement = existingClasses.add(concept);
		this.conceptAssertionMap.put(individual, existingClasses);
		if (hasNewElement)
			this.numberOfConceptAssertions++;
		return hasNewElement;
	}

	@Override
	public int getNumberOfInitialConceptAssertions() {

		return this.numberOfConceptAssertions;
	}

}
