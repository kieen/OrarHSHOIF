package orar.factory;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Singleton factory for: abstract individuals, fresh concept names
 * 
 * @author T.Kien Tran
 */
public class AbstractDataFactory {
	private final String IRI_BASE_X = "http://www.af#abstractX";
	private final String IRI_BASE_Y = "http://www.af#abstractY";
	private final String IRI_BASE_Z = "http://www.af#abstractZ";
	private final String IRI_BASE_CONCEPT = "http://www.af#freshC";
	private static long xCounter;
	private static long yCounter;
	private static long zCounter;

	private static AbstractDataFactory instance;

	private final Set<OWLNamedIndividual> yAbstractIndividuals;
	private final Set<OWLNamedIndividual> zAbstractIndividuals;
	private final Set<OWLNamedIndividual> xAbstractIndividuals;

	OWLOntologyManager manager;
	OWLDataFactory factory;

	private AbstractDataFactory() {
		manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		yAbstractIndividuals = new HashSet<OWLNamedIndividual>();
		xAbstractIndividuals = new HashSet<OWLNamedIndividual>();
		zAbstractIndividuals = new HashSet<OWLNamedIndividual>();
		xCounter = 0;
		yCounter = 0;
		zCounter = 0;

	}

	/**
	 * @return Singleton instance of the factory
	 */
	public static AbstractDataFactory getInstance() {
		if (instance == null)
			instance = new AbstractDataFactory();
		return instance;
	}

	/**
	 * Create abstract an individual representing individual type
	 * 
	 * @return a fresh abstract individual with x-prefix
	 */
	public OWLNamedIndividual createAbstractIndividualX() {
		OWLNamedIndividual x = factory.getOWLNamedIndividual(IRI
				.create(IRI_BASE_X + ++xCounter));
		xAbstractIndividuals.add(x);

		return x;

	}

	/**
	 * Create abstract an individual representing successors
	 * 
	 * @return a fresh abstract individual with y-prefix
	 */
	public OWLNamedIndividual createAbstractIndividualY() {
		OWLNamedIndividual y = factory.getOWLNamedIndividual(IRI
				.create(IRI_BASE_Y + ++yCounter));
		yAbstractIndividuals.add(y);

		return y;
	}

	/**
	 * Create abstract an individual representing predecessors
	 * 
	 * @return a fresh abstract individual with z-prefix
	 */
	public OWLNamedIndividual createAbstractIndividualZ() {
		OWLNamedIndividual z = factory.getOWLNamedIndividual(IRI
				.create(IRI_BASE_Z + ++zCounter));
		zAbstractIndividuals.add(z);

		return z;

	}

	/**
	 * @return the iRI_BASE_X
	 */
	public String getIRI_BASE_X() {
		return IRI_BASE_X;
	}

	/**
	 * @return the iRI_BASE_Y
	 */
	public String getIRI_BASE_Y() {
		return IRI_BASE_Y;
	}

	/**
	 * @return the iRI_BASE_Z
	 */
	public String getIRI_BASE_Z() {
		return IRI_BASE_Z;
	}

	/**
	 * @return the iRI_BASE_CONCEPT
	 */
	public String getIRI_BASE_CONCEPT() {
		return IRI_BASE_CONCEPT;
	}

	public static long getxCounter() {
		return xCounter;
	}

	public static long getyCounter() {
		return yCounter;
	}

	public static long getzCounter() {
		return zCounter;
	}

	/**
	 * @return A set of predecessors and successors, e.g. y,z
	 */
	public Set<OWLNamedIndividual> getYAbstractIndividuals() {
		return yAbstractIndividuals;
	}

	/**
	 * @return A set of predecessors and successors, e.g. y,z
	 */
	public Set<OWLNamedIndividual> getZAbstractIndividuals() {
		return zAbstractIndividuals;
	}

	// /**
	// * @return A set of predecessors and successors, e.g. y,z
	// */
	// public Set<OWLNamedIndividual> getYZAbstractIndividuals() {
	// return yzAbstractIndividuals;
	// }

	/**
	 * @return A set of abstract individual X
	 */
	public Set<OWLNamedIndividual> getXAbstractIndividuals() {
		return xAbstractIndividuals;
	}

	// /**
	// * @return All abstract individuals genearted during abstraction.
	// */
	// public Set<OWLNamedIndividual> getXYZAbstractIndividuals() {
	//
	// return xyzAbstractIndividuals;
	// }

	public void clear() {
		xAbstractIndividuals.clear();
		yAbstractIndividuals.clear();
		zAbstractIndividuals.clear();
		xCounter = 0;
		yCounter = 0;
		zCounter = 0;

	}

}
