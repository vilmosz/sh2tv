package tv.shapeshifting.nsl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;

import tv.shapeshifting.nsl.exceptions.TimecodeFormatException;
import tv.shapeshifting.nsl.exceptions.UnexpectedNarrativeObjectException;
import tv.shapeshifting.nsl.exceptions.UnrecognizedMediatypeException;

public interface OntologyInterface {

    public final String NSL_NAMESPACE = "http://shapeshifting.tv/ontology/nsl#";

    public void init(final String[] owls, final String[] rules) throws IOException;

    public void addRules(String url, boolean transitive)
            throws MalformedURLException;

    public Individual[] getNarrativeRoot() throws IOException;

    public void preprocessSparqlExpressions() throws IOException;

    public void preprocessContextVariables() throws IOException;

    public int applyDynamicRules(boolean transitive)
            throws MalformedURLException;

    public boolean isAtomic(Individual individual);

    public boolean isMediaObject(Individual individual);

    public boolean isImplicitObject(Individual individual);

    public boolean isLinkStructure(Individual individual);

    public boolean isBinStructure(Individual individual);

    public boolean isLayerStructure(Individual individual);

    public boolean isDecisionPoint(Individual individual);

    public boolean isLink(Individual individual);

    public void setBeingProcessed(Individual individual)
            throws IOException;

    public Map<String, Object> getPlaylistEntry(Individual individual) throws IOException, TimecodeFormatException, UnrecognizedMediatypeException;

    public Vector<HashMap<String, Object>> getInteractions(Individual individual) throws IOException, TimecodeFormatException;

    public void updateStructuredObjectTiming(Individual parent, Individual individual) throws IOException;

    public Individual getMediaObject(Individual individual)
            throws UnexpectedNarrativeObjectException;

    public Individual getStartItemOf(Individual individual)
            throws UnexpectedNarrativeObjectException;

    public Individual[] getNextLinkStructureItem(Individual individual)
            throws IOException;

    public Individual[] getBinItems(Individual individual)
            throws IOException;

    public boolean evaluateTerminationCondition(Individual individual)
            throws IOException;

    public Individual getLeadingLayerOf(Individual individual)
            throws UnexpectedNarrativeObjectException;

    public Individual[] getNarrativeItemsOf(Individual individual)
            throws UnexpectedNarrativeObjectException, IOException;

    public void updateDuration(Individual individual, long duration)
            throws IOException;

    public boolean hasPlaylistBarrier(Individual individual)
            throws IOException;

    public void setHasBeenProcessed(Individual individual)
            throws IOException;

    public void closeModels();

    public InfModel getInfModel();

    public OntModel getOntModel();

    public OntModel getRawModel();
    
    public Model getModel(ModelType modelType);

    public void update(String updateString, Model model);

    public long construct(String constructString, Model model);

    public boolean ask(String askString, Model model);

    public String logQuery(String queryString, Model model);

    public boolean isContextVariableDefined(String label)
            throws IOException;

    public long defineUntypedContextVariable(String label, Object value)
            throws IOException;

    public void setUntypedContextVariable(String label, Object value)
            throws IOException;

    public SelectType getSelectType(Individual individual)
            throws UnexpectedNarrativeObjectException;

    public enum SelectType {
        SELECT_ONE("SelectOne"),
        SELECT_SEQUENCE("SelectSequence"),
        SELECT_ALTERNATIVES("SelectAlternatives");
        private String value;

        SelectType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    
    public enum ModelType { RAW, ONTOLOGY, INFERENCE }

}