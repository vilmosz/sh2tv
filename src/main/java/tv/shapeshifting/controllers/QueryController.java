package tv.shapeshifting.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.hpl.jena.rdf.model.Model;

import tv.shapeshifting.nsl.OntologyInterface;
import tv.shapeshifting.nsl.OntologyInterface.ModelType;
import tv.shapeshifting.nsl.Settings;

@Controller
public class QueryController extends AbstractController {

    public enum Command { QUERY, CONSTRUCT, UPDATE }
    
    public static final String RESULT       = "result";
    public static final String SESSION_ID   = "sessionId";
    public static final String QUERY        = "query";
    public static final String COMMAND      = "command";
    public static final String MODEL_TYPE   = "modelType";

    private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

    @RequestMapping(value = "query")
    public String executeQuery(final HttpServletRequest request, org.springframework.ui.Model uiModel,
            @RequestParam(value = COMMAND, required = false, defaultValue = "QUERY") final Command command,
            @RequestParam(value = MODEL_TYPE, required = false, defaultValue = "RAW") ModelType modelType,
            @RequestParam(value = QUERY, required = false) final String query) throws IOException  {
        HttpSession session = request.getSession(false);
        if (session == null) {
            LOG.debug("No session");            
        } else {
            LOG.debug(String.format("Session id: [%s]", session.getId()));     
            uiModel.addAttribute(SESSION_ID, session.getId());
            if(session.getAttribute(Settings.ONTOLOGY) != null && query != null) {
                OntologyInterface ow = (OntologyInterface) session.getAttribute(Settings.ONTOLOGY);
                Model model = ow.getModel(modelType);
                switch(command) {
                    case QUERY:
                        uiModel.addAttribute(RESULT, ow.logQuery(query, model));
                        break;
                    case CONSTRUCT:
                        uiModel.addAttribute(RESULT, "" + ow.construct(query, model));
                        break;
                    case UPDATE:
                        ow.update(query, model);
                        break;            
                }
            }
        }
        uiModel.addAttribute(QUERY, query);
        return QUERY;
    }
    
}