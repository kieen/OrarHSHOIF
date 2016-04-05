package orar.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.abstraction.PairOfSubjectAndObject;

/**
 * Data using to transfer entailments from the abstraction to the original ABox.
 * This data is created while generating the abstraction from types.
 * 
 * @author kien
 *
 */
public class DataForTransferingEntailments {
	private static DataForTransferingEntailments instance;

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
	private final Map<PairOfSubjectAndObject, OWLObjectProperty> xyMap2Role;
	/*
	 * Map: (z,x) ---> r (inverse functional role) in the abstract abox
	 */
	private final Map<PairOfSubjectAndObject, OWLObjectProperty> zxMap2Role;

	/*
	 * A set of x whose type contains functional roles
	 */
	private final Set<OWLNamedIndividual> xAbstractHavingFunctionalRole;

	/*
	 * A set of z whose type contains inverse functional roles.
	 */
	private final Set<OWLNamedIndividual> zAbstractHavingInverseFunctionalRole;

	private DataForTransferingEntailments() {

		this.xAbstract2OriginalIndividualsMap = new HashMap<>();
		this.yAbstract2OriginalIndividualsMap = new HashMap<>();
		this.zAbstract2OriginalIndividualsMap = new HashMap<>();

		this.uAbstract2OriginalIndividualsMap = new HashMap<>();

		this.xyMap2Role = new HashMap<>();
		this.zxMap2Role = new HashMap<>();

		this.xAbstractHavingFunctionalRole = new HashSet<>();
		this.zAbstractHavingInverseFunctionalRole = new HashSet<>();

	}

	public static DataForTransferingEntailments getInstance() {
		if (instance == null) {
			instance = new DataForTransferingEntailments();
		}
		return instance;
	}

	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getMap_XAbstractIndiv_2_OriginalIndivs() {
		return xAbstract2OriginalIndividualsMap;
	}

	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getMap_YAbstractIndiv_2_OriginalIndivs() {
		return yAbstract2OriginalIndividualsMap;
	}

	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getMap_ZAbstractIndiv_2_OriginalIndivs() {
		return zAbstract2OriginalIndividualsMap;
	}

	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getMap_UAbstractIndiv_2_OriginalIndivs() {
		return uAbstract2OriginalIndividualsMap;
	}

	public Map<PairOfSubjectAndObject, OWLObjectProperty> getMap_XY_2_Role() {
		return xyMap2Role;
	}

	public Map<PairOfSubjectAndObject, OWLObjectProperty> getMap_ZX_2_Role() {
		return zxMap2Role;
	}

	/**
	 * @return a set of abstract indiv x whose type contains functional roles.
	 */
	public Set<OWLNamedIndividual> getxAbstractHavingFunctionalRole() {
		return xAbstractHavingFunctionalRole;
	}

	/**
	 * @return a set of abstract indiv z whose type contains inverse functional
	 *         roles.
	 */
	public Set<OWLNamedIndividual> getzAbstractHavingInverseFunctionalRole() {
		return zAbstractHavingInverseFunctionalRole;
	}

	/**
	 * @param abstractInd
	 * @return a set of original individuals for which the abstractInd represents.<b> Note </b> that changing in this set will affect the mapping.
	 */
	public Set<OWLNamedIndividual> getOriginalIndividuals(OWLNamedIndividual abstractInd) {
		Set<OWLNamedIndividual> originalInds = new HashSet<>();

		Set<OWLNamedIndividual> originalOfX = this.xAbstract2OriginalIndividualsMap.get(abstractInd);
		Set<OWLNamedIndividual> originalOfY = this.yAbstract2OriginalIndividualsMap.get(abstractInd);
		Set<OWLNamedIndividual> originalOfZ = this.zAbstract2OriginalIndividualsMap.get(abstractInd);
		Set<OWLNamedIndividual> originalOfU = this.uAbstract2OriginalIndividualsMap.get(abstractInd);
		/*
		 * an abstract individual can ONLY be either x,y,z, or u.
		 */
		if (originalOfX != null) {
			originalInds.addAll(originalOfX);
		} else if (originalOfY != null) {
			originalInds.addAll(originalOfY);
		} else if (originalOfZ != null) {
			originalInds.addAll(originalOfZ);
		} else if (originalOfU != null) {
			originalInds.addAll(originalOfU);
		}

		return originalInds;
	}

	/**
	 * Clear all maps.
	 */
	public void clear() {

		this.xAbstract2OriginalIndividualsMap.clear();
		this.yAbstract2OriginalIndividualsMap.clear();
		this.zAbstract2OriginalIndividualsMap.clear();

		this.uAbstract2OriginalIndividualsMap.clear();

		this.xyMap2Role.clear();
		this.zxMap2Role.clear();

		this.xAbstractHavingFunctionalRole.clear();
		this.zAbstractHavingInverseFunctionalRole.clear();
	}
}
