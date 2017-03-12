package tv.shapeshifting.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
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

import tv.shapeshifting.nsl.NslInterpreter;
import tv.shapeshifting.nsl.Settings;
import tv.shapeshifting.nsl.exceptions.InterpreterStateException;
import tv.shapeshifting.nsl.exceptions.UnexpectedNarrativeObjectException;

@RestController
@RequestMapping(value = "playlist")
public class PlaylistController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(PlaylistController.class);

	@SuppressWarnings("unchecked")
	@ResponseBody
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Vector<Object>>> retrieve(
			@RequestParam(value = "command", required = false) final String command, final HttpServletRequest request)
			throws IOException, UnexpectedNarrativeObjectException, InterruptedException, InterpreterStateException {

		HttpSession session = request.getSession(true);

		if (StringUtils.containsIgnoreCase(command, "start")) {
			if (StringUtils.containsIgnoreCase(command, "restart")) {
				LOG.debug("Invalidating session [" + session.getId() + "] ...");
				session.invalidate();
				session = request.getSession(true);
			}

		}

		Map<String, Vector<Object>> playlistFragment = Maps.newHashMap();
		if (session.getAttribute(Settings.INTERPRETER) != null) {
			NslInterpreter i = (NslInterpreter) session.getAttribute(Settings.INTERPRETER);
			if (StringUtils.containsIgnoreCase(command, "old")) {
				Vector<Map<String, Vector<Object>>> history = i.getPlaylistHistory();
				playlistFragment = history.lastElement();
			} else {
				i.interpret();
				playlistFragment = i.getPlaylistFragment();
			}
			if (playlistFragment != null) {
				if (Settings.i().getBoolean("INTERACTION_URL_REWRITE")) {
					if (playlistFragment.containsKey("interaction")) {
						String[] keys = Settings.i().getStringArray("INTERACTION_URL_REWRITE_KEY");
						Vector<Object> interaction = playlistFragment.get("interaction");
						String prefix = "http://" + request.getHeader("host") + request.getContextPath();
						for (int j = 0; j < interaction.size(); j++) {
							for (int k = 0; k < keys.length; k++) {
								Map<String, Object> o = (Map<String, Object>) interaction.get(j);
								if (o.containsKey(keys[k]))
									o.put(keys[k], prefix + "/" + o.get(keys[k]).toString());
							}
						}
					}
				}
			}
		}
		return ResponseEntity.ok().body(playlistFragment);
	}
}