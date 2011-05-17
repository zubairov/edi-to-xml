package org.zubairov.edi2xml;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;

@SuppressWarnings("serial")
public class ToXMLServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.sendRedirect("/");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String content = req.getParameter("content");
		if (content != null && content.length() > 0) {
			String key = processEDI(content);
			resp.sendRedirect("/xml/" + key);
		} else {
			resp.sendRedirect("/");
		}
	}

	/**
	 * Process EDIFACT
	 * 
	 * @param content
	 */
	private String processEDI(String content) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Transaction transaction = datastore.beginTransaction();
		Entity entity = new Entity("EDI");
		entity.setProperty("content", new Text(content));
		Key key = datastore.put(entity);
		transaction.commit();
		return KeyFactory.keyToString(key);
	}

}
