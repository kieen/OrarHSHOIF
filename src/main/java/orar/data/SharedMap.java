package orar.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import orar.type.IndividualType;

public class SharedMap {
	private static SharedMap instance;
	private final Map<IndividualType, Set<OWLNamedIndividual>> type2IndividualsMap;

	/*
	 * map from original individual to abstract individual is only required for
	 * non-horn. As we will need to us this information to connect Xs latter
	 */
	private final Map<OWLNamedIndividual, OWLNamedIndividual> originalIndividual2AbstractIndividualsMap;

	private final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> xAbstract2OriginalIndividualsMap;
	private final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> yAbstract2OriginalIndividualsMap;
	private final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> zAbstract2OriginalIndividualsMap;

	// private final Set<OWLNamedIndividual> nominals;
	// private final Set<OWLClass> nominalConcepts;
	//
	// private final Set<OWLObjectProperty> functionalRoles;
	// private final Set<OWLObjectProperty> inverseFunctionalRoles;

	private SharedMap() {
		/*
		 * maps to original individuals
		 */
		this.xAbstract2OriginalIndividualsMap = new HashMap<>();
		this.yAbstract2OriginalIndividualsMap = new HashMap<>();
		this.zAbstract2OriginalIndividualsMap = new HashMap<>();
		this.type2IndividualsMap = new HashMap<IndividualType, Set<OWLNamedIndividual>>();

		/*
		 * map from original individual to abstract individual
		 */
		this.originalIndividual2AbstractIndividualsMap = new HashMap<>();
	}

	public static SharedMap getInstance() {
		if (instance == null) {
			instance = new SharedMap();
		}
		return instance;
	}

	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getXAbstract2OriginalIndividualsMap() {
		return xAbstract2OriginalIndividualsMap;
	}

	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getYAbstract2OriginalIndividualsMap() {
		return yAbstract2OriginalIndividualsMap;
	}

	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getZAbstract2OriginalIndividualsMap() {
		return zAbstract2OriginalIndividualsMap;
	}

	public Map<IndividualType, Set<OWLNamedIndividual>> getType2IndividualsMap() {
		return type2IndividualsMap;
	}

	public Map<OWLNamedIndividual, OWLNamedIndividual> getOriginalIndividual2AbstractIndividualsMap() {
		return originalIndividual2AbstractIndividualsMap;
	}

	/**
	 * Clear everything except (inverse)functional roles
	 */
	public void clear() {
		this.type2IndividualsMap.clear();
		this.originalIndividual2AbstractIndividualsMap.clear();

		this.xAbstract2OriginalIndividualsMap.clear();
		this.yAbstract2OriginalIndividualsMap.clear();
		this.zAbstract2OriginalIndividualsMap.clear();

	}

}
