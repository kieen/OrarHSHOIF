package orar.modeling.sameas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class MapbasedSameAsBox implements SameAsBox {

	/*
	 * Store map between individual and its equal one. We only store such a
	 * mapping for one individual if it is really equal to individuals other
	 * than itself.
	 */
	private final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> sameAsMap;
	// private final List<Set<OWLNamedIndividual>> listOfSameasAssertions;

	public MapbasedSameAsBox() {
		this.sameAsMap = new HashMap<OWLNamedIndividual, Set<OWLNamedIndividual>>();
		// this.listOfSameasAssertions = new ArrayList<>();
	}

	@Override
	public Set<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual individual) {
		Set<OWLNamedIndividual> equalIndividuals = this.sameAsMap.get(individual);
		if (equalIndividuals != null) {
			return equalIndividuals;
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public boolean addSameAsAssertion(OWLNamedIndividual individual, OWLNamedIndividual equalIndividual) {
		Set<OWLNamedIndividual> existingEqualIndividuals = this.sameAsMap.get(individual);
		if (existingEqualIndividuals == null) {
			existingEqualIndividuals = new HashSet<>();
		}
		boolean hasNewElement = existingEqualIndividuals.add(equalIndividual);
		this.sameAsMap.put(individual, existingEqualIndividuals);
		return hasNewElement;
	}

	@Override
	public boolean addManySameAsAssertions(OWLNamedIndividual individual,
			Set<OWLNamedIndividual> manyEqualIndividuals) {
		Set<OWLNamedIndividual> existingEqualIndividuals = this.sameAsMap.get(individual);
		if (existingEqualIndividuals == null) {
			existingEqualIndividuals = new HashSet<>();
		}
		boolean hasNewElement = existingEqualIndividuals.addAll(manyEqualIndividuals);
		this.sameAsMap.put(individual, existingEqualIndividuals);
		return hasNewElement;
	}

	@Override
	public Set<OWLNamedIndividual> getAllIndividuals() {
		Set<OWLNamedIndividual> allIndividuals = new HashSet<>();
		allIndividuals.addAll(this.sameAsMap.keySet());
		for (Set<OWLNamedIndividual> value : this.sameAsMap.values()) {
			allIndividuals.addAll(value);
		}
		return allIndividuals;
	}

	@Override
	public boolean addSameasAssertions(Set<OWLNamedIndividual> setOfSameasIndividuals) {
		boolean updated = false;
		for (OWLNamedIndividual anIndividual : setOfSameasIndividuals) {
			Set<OWLNamedIndividual> existsingInds = this.sameAsMap.get(anIndividual);
			if (existsingInds == null) {
				existsingInds = new HashSet<>();
			}
			if (existsingInds.addAll(setOfSameasIndividuals)) {
				updated = true;
			}
			this.sameAsMap.put(anIndividual, existsingInds);
		}
		return updated;
	}

	@Override
	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getSameasMap() {
		return new HashMap<>(this.sameAsMap);
	}
}
