package tv.shapeshifting.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tv.shapeshifting.nsl.NslInterpreter;
import tv.shapeshifting.nsl.OntologyInterface;
import tv.shapeshifting.nsl.Settings;

@RestController
@RequestMapping(value = "engine")
public class EngineController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(EngineController.class);

	private final OntologyInterface ontologyInterface;
	
	@Autowired
	public EngineController(final OntologyInterface ontologyInterface) {
		this.ontologyInterface = ontologyInterface;
	}

	@ResponseBody
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EngineResponse> retrieve(
			@RequestParam(value = "command", required = false) final String command,
			@RequestParam(value = "owl", required = false) final String[] owl,
			@RequestParam(value = "rules", required = false) final String[] rules,
			final HttpServletRequest request) throws IOException {

		HttpSession session = request.getSession(true);

		if (StringUtils.containsIgnoreCase(command, "start")) {
			if (StringUtils.containsIgnoreCase(command, "restart")) {
				LOG.debug("Invalidating session [" + session.getId() + "] ...");
				session.invalidate();
				session = request.getSession(true);
			}
			ontologyInterface.init(owl, rules);
			NslInterpreter i = new NslInterpreter(ontologyInterface);
			session.setAttribute(Settings.INTERPRETER, i);
			session.setAttribute(Settings.ONTOLOGY, ontologyInterface);
			session.setAttribute(Settings.SESSIONID, session.getId());
			LOG.debug("Engine started for session [" + session.getId() + "].");
		} else if (StringUtils.containsIgnoreCase(command, "stop")) {
			LOG.debug("Shutting down session [" + session.getId() + "] ...");
			session.invalidate();
			session = request.getSession(true);
		}
		LOG.debug("Session [" + session.getId() + "]");
		return ResponseEntity.ok().body(new EngineResponse(session));

	}

	class EngineResponse {

		final HttpSession session;

		public EngineResponse(final HttpSession session) {
			this.session = session;
		}

		public String getSessionid() {
			return session.getId();
		}

		public NslInterpreter.State getInterpreter() {
			NslInterpreter i = (NslInterpreter) session.getAttribute(Settings.INTERPRETER);
			return i != null ? i.getState() : NslInterpreter.State.UNKNOWN;
		}

		public String getVersion() {
			NslInterpreter i = (NslInterpreter) session.getAttribute(Settings.INTERPRETER);
			return i != null ? i.version() : "bla";
		}

		public Long getOwl() {
			OntologyInterface ow = (OntologyInterface) session.getAttribute(Settings.ONTOLOGY);
			return ow != null ? ow.getInfModel().size() : 0;
		}

	}

}
