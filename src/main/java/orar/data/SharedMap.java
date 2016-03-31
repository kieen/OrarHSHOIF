package orar.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.abstraction.PairOfSubjectAndObject;

public class SharedMap {
	private static SharedMap instance;

	/*
	 * Maps: x/y/z --> original individuals. x,y,z are abstract individuals for
	 * combined-type.
	 */
	private final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> xAbstract2OriginalIndividualsMap;
	private final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> yAbstract2OriginalIndividualsMap;
	private final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> zAbstract2OriginalIndividualsMap;

	/*
	 * Map: u --> original individuals. u is the abstract individual for
	 * concept-type)
	 */
	private final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> uAbstract2OriginalIndividualsMap;
	/*
	 * Map: (x,y) --> r (functional role) in the abstract abox
	 */
	private final Map<PairOfSubjectAndObject, Set<OWLObjectProperty>> xyMap2Roles;
	/*
	 * Map: (z,x) ---> r (inverse functional role) in the abstract abox
	 */
	private final Map<PairOfSubjectAndObject, Set<OWLObjectProperty>> zxMap2Roles;

	private SharedMap() {

		this.xAbstract2OriginalIndividualsMap = new HashMap<>();
		this.yAbstract2OriginalIndividualsMap = new HashMap<>();
		this.zAbstract2OriginalIndividualsMap = new HashMap<>();

		this.uAbstract2OriginalIndividualsMap = new HashMap<>();

		this.xyMap2Roles = new HashMap<>();
		this.zxMap2Roles = new HashMap<>();

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

	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getUAbstract2OriginalIndividualsMap() {
		return uAbstract2OriginalIndividualsMap;
	}

	public Map<PairOfSubjectAndObject, Set<OWLObjectProperty>> getXYMap2Roles() {
		return xyMap2Roles;
	}

	public Map<PairOfSubjectAndObject, Set<OWLObjectProperty>> getZXMap2Roles() {
		return zxMap2Roles;
	}

	/**
	 * Clear all maps.
	 */
	public void clear() {

		this.xAbstract2OriginalIndividualsMap.clear();
		this.yAbstract2OriginalIndividualsMap.clear();
		this.zAbstract2OriginalIndividualsMap.clear();

		this.uAbstract2OriginalIndividualsMap.clear();

		this.xyMap2Roles.clear();
		this.zxMap2Roles.clear();
	}
}
