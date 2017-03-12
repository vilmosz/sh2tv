package tv.shapeshifting.nsl.ontology;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class SparqlFileRepository {
	private static final Logger LOG = Logger.getLogger(SparqlFileRepository.class);
	private HashMap<String, String> map;
	private static SparqlFileRepository i = null;

	private SparqlFileRepository() {
		LOG.info("initialized");
		map = new HashMap<>();
	}

	public static SparqlFileRepository i() {
		if (i == null) {
			i = new SparqlFileRepository();
		}
		return i;
	}

	public String get(String queryPath) throws IOException {
		if (!map.containsKey(queryPath)) {
			String r = load(queryPath);
			map.put(queryPath, r);
		}
		return map.get(queryPath);
	}

	private synchronized String load(String queryPath) throws java.io.IOException {
		final Resource resource = resolve(queryPath);
		try (final InputStream is = resource.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8.toString());
		}
	}

	public Resource resolve(final String path) {
		Resource resource = null;
		try {
			LOG.debug(String.format("Loading [%s] as URL", path));
			resource = new UrlResource(path);
			if (!resource.exists()) {
				throw new MalformedURLException(String.format("URL [%s] doesn't exist", path));
			}
		} catch (MalformedURLException e) {
			LOG.debug(String.format("Loading [%s] from the classpath", path));
			resource = new ClassPathResource(path);
		}
		return resource;
	}

	public String getUrl(final String path) throws IOException {
		final Resource resource = resolve(path);
		return resource.getURL().toString();
	}

	public static void main(String[] agrs) throws IOException {
		LOG.debug(SparqlFileRepository.i().get("queries/narrativeRoot.query"));
	}
}
