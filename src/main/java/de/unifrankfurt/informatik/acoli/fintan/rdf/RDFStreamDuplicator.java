package de.unifrankfurt.informatik.acoli.fintan.rdf;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unifrankfurt.informatik.acoli.fintan.core.FintanInputStream;
import de.unifrankfurt.informatik.acoli.fintan.core.StreamRdfUpdater;

/**
 * Duplicates the contents of default FintanInputStream to all attached OutputStreams.
 * 
 * Throws IOException, if a named InputStream is set. Can only take  a single input.
 * @author CF
 *
 */
public class RDFStreamDuplicator extends StreamRdfUpdater {

	
	protected static final Logger LOG = LogManager.getLogger(RDFStreamDuplicator.class.getName());

	@Override
	public void setInputStream(FintanInputStream<Model> inputStream, String name) throws IOException {
		throw new IOException();
	}
	
	private void processStream() {
		while (getInputStream().canRead()) {
			try {
				Model model_in = getInputStream().read();
				for (String name:listOutputStreamNames()) {
					Model model_out = ModelFactory.createDefaultModel();
					model_out.add(model_in);
					getOutputStream(name).write(model_out);
				}
			} catch (InterruptedException e) {
				LOG.error("Resuming from interrupted thread when reading from default Stream: " +e);
			}
		}

		for (String name:listOutputStreamNames()) {
			getOutputStream(name).terminate();
		}
	}
	
	@Override
	public void start() {
		run();
	}

	@Override
	public void run() {
		try {
			processStream();
		} catch (Exception e) {
			LOG.error(e, e);
			System.exit(1);
		}
	}




	
}