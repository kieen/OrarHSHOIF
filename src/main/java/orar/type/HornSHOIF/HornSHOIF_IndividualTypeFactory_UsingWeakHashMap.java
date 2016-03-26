package orar.type.HornSHOIF;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.type.IndividualType;

public class HornSHOIF_IndividualTypeFactory_UsingWeakHashMap implements HornSHOIF_IndividualTypeFactory {

	private final Map<IndividualType, WeakReference<IndividualType>> cache;
	private static HornSHOIF_IndividualTypeFactory_UsingWeakHashMap instance;

	private HornSHOIF_IndividualTypeFactory_UsingWeakHashMap() {
		this.cache = new WeakHashMap<IndividualType, WeakReference<IndividualType>>();

	}

	public static HornSHOIF_IndividualTypeFactory_UsingWeakHashMap getInstance() {
		if (instance == null) {
			instance = new HornSHOIF_IndividualTypeFactory_UsingWeakHashMap();
		}
		return instance;

	}

	@Override
	public IndividualType getIndividualType(Set<OWLClass> atomicConcepts, Set<OWLObjectProperty> preRoles,
			Set<OWLObjectProperty> sucRoles) {
		IndividualType newType = new HornSHOIF_IndividualType(atomicConcepts, preRoles, sucRoles);

		WeakReference<IndividualType> valueOfNewType = this.cache.get(newType);
		if (valueOfNewType == null) {
			this.cache.put(newType, new WeakReference<IndividualType>(newType));
			return newType;
		}

		return valueOfNewType.get();

	}

}
