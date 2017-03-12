package tv.shapeshifting.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;

import tv.shapeshifting.nsl.OntologyInterface;
import tv.shapeshifting.nsl.Settings;
import tv.shapeshifting.nsl.ontology.Wrapper;

@RestController
@RequestMapping(value = "interaction")
public class InteractionController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(InteractionController.class);

	@SuppressWarnings("unchecked")
	@ResponseBody
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> interaction(
			@RequestParam(value = "command", required = false) final String command, final HttpServletRequest request) throws IOException  {

		HttpSession session = request.getSession(false);
		String id = (session != null) ? session.getId() : "no session initiated.";

		Map<String, Object> interactions = session.getAttribute(Settings.INTERACTION) == null ?
				Maps.newHashMap() :
				(HashMap<String, Object>) session.getAttribute(Settings.INTERACTION);
		
		
		if(session != null) {
			// check if the OWL is initialized and insert the value in the
			Map<String, String[]> parameterMap = request.getParameterMap();
			
			if(!parameterMap.isEmpty() && session.getAttribute(Settings.ONTOLOGY) != null) {
					OntologyInterface ow = (OntologyInterface) session.getAttribute(Settings.ONTOLOGY);
					boolean flag = false;
					for (Iterator<Entry<String, String[]>> it = parameterMap.entrySet().iterator(); it.hasNext(); ) {
						Map.Entry<String, String[]> pair = it.next();
						if(pair.getValue().length == 1) {
							flag = true;
							String canonicalValue = Wrapper.toCanonicalValue(pair.getValue()[0]);
							// Assign as typed context variable 
							if(ow.isContextVariableDefined(pair.getKey()))
								// Check whether it has already been defined
								ow.setUntypedContextVariable(pair.getKey(), canonicalValue);
							else
								ow.defineUntypedContextVariable(pair.getKey(), canonicalValue);
						} else if(pair.getValue().length == 0) {
							LOG.warn("Varaible [" + pair.getKey() + "] not assigned to empty value.");
						} else {
							LOG.warn("Variable [" + pair.getKey() + "] not assigned to list as lists are not supported yet.");						
						}
					}
					//TODO this call may be non-blocking
					if ( flag )
						ow.applyDynamicRules(false);
			
			}
			
			// Keep a local copy in a hash map
			
			if(parameterMap != null) {
				interactions.putAll(parameterMap);
				session.setAttribute(Settings.INTERACTION, interactions);
			}
		}
		interactions.put("session", id);
		return ResponseEntity.ok().body(interactions);		
	}
	
}

