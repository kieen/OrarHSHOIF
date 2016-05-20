package orar.owlconverter;

/**
 * Convert ontologies, whose assertions are stored in turtle syntax, to
 * OWLFunctionalSyntax
 * 
 * @author kien
 *
 */
public interface OWLOntologyConverter {
	public void convert(String tboxFile, String aboxListFile, String owlFunctionalSyntaxFile);
}
