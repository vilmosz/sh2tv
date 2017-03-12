package tv.shapeshifting.nsl.ontology;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class FileService {

	private static final Logger LOG = Logger.getLogger(FileService.class);
	
	public String resolveUrl(final String path) throws IOException {
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
		return resource.getURL().toString();
	}
	
}
