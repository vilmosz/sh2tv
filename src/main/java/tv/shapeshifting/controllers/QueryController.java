package tv.shapeshifting.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;

import tv.shapeshifting.nsl.OntologyInterface;
import tv.shapeshifting.nsl.OntologyInterface.ModelType;
import tv.shapeshifting.nsl.Settings;

@RestController
@RequestMapping(value = "query")
public class QueryController extends AbstractController {

    public enum Command { QUERY, CONSTRUCT, UPDATE }

    private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

    @GetMapping
    public String query(
            @RequestParam(value = "command", required = false, defaultValue = "QUERY") final Command command,
            @RequestParam(value = "modelType", required = false, defaultValue = "RAW") ModelType modelType,
            @RequestParam(value = "query") final String query,
            final HttpServletRequest request) throws IOException  {
        HttpSession session = request.getSession();
        String id = session.getId();
        LOG.debug("Session id: " + id);     
        
        if(session.getAttribute(Settings.ONTOLOGY) != null && query != null) {
            request.setAttribute("query", query);

            OntologyInterface ow = (OntologyInterface) session.getAttribute(Settings.ONTOLOGY);
            
            String res = "";
            Model model = ow.getModel(modelType);
            switch(command) {
                case QUERY:
                    res = ow.logQuery(query, model);
                    break;
                case CONSTRUCT:
                    res = "" + ow.construct(query, model);
                    break;
                case UPDATE:
                    ow.update(query, model);
                    break;            
            }
            request.setAttribute("result", res);
        } else {
            LOG.debug("Ontology not initialized for session id " + id);
        }
        
        return "query";
    }
}