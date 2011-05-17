package org.zubairov.edi2xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.milyn.edisax.EDIParser;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class EDIConversionServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(EDIConversionServlet.class);
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String ctx = req.getRequestURL().toString();
		String keyStr = ctx.substring(ctx.lastIndexOf('/') + 1, ctx.length());
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Key key = KeyFactory.stringToKey(keyStr);
		try {
			Entity entity = datastore.get(key);
			Text content = (Text) entity.getProperty("content");
			String edifact = content.getValue();
			resp.setContentType("text/xml");
			resp.getWriter().println("<?xml version=\"1.0\"?>");
			parseEDI(edifact, resp.getWriter());
		} catch (EntityNotFoundException e) {
			log.error("Exception happening when processing key " + key, e);
		} catch (Exception e) {
			log.error("Exception happening when processing key " + key, e);
			throw new RuntimeException("Failed to process EDI document", e);
		} 
	}

	private void parseEDI(String edifact, PrintWriter out) throws IOException, SAXException {
		UNEdifactInterchangeParser parser = new UNEdifactInterchangeParser();
		parser.setFeature(EDIParser.FEATURE_IGNORE_NEWLINES, true);
		parser.setFeature(EDIParser.FEATURE_VALIDATE, true);
		parser.setContentHandler(new OutputHandler(out));
		ByteArrayInputStream in = new ByteArrayInputStream(edifact.getBytes());
		parser.parse(new InputSource(in));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendRedirect("/");
	}


}
