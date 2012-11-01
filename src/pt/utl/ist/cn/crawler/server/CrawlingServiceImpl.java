package pt.utl.ist.cn.crawler.server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import pt.utl.ist.cn.crawler.server.Parser;
import pt.utl.ist.cn.crawler.server.PopulateWWW;
import pt.utl.ist.cn.crawler.client.CrawlingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CrawlingServiceImpl extends RemoteServiceServlet implements
	CrawlingService {

	public String crawlServer(/*String input*/) throws IllegalArgumentException {
		// Verify that the input is valid. 
		/*if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}*/

		//String serverInfo = getServletContext().getServerInfo();
		//String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		//input = escapeHtml(input);
		//userAgent = escapeHtml(userAgent);

		/*
		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
		*/
		
		List<String> urls = new LinkedList<String>();
		
		
		urls.add("http://en.wikipedia.org/wiki/Cooking");
		urls.add("http://www.google.pt/");
		urls.add("http://en.wikipedia.org/wiki/Food");
		urls.add("http://en.wikipedia.org/wiki/Vitamins");
		urls.add("http://en.wikipedia.org/wiki/Proteins");
		urls.add("http://en.wikipedia.org/wiki/Nutrients");
		urls.add("http://en.wikipedia.org/wiki/Plant_nutrition");
		urls.add("http://en.wikipedia.org/wiki/Humus");
		urls.add("http://en.wikipedia.org/wiki/Iron");
		urls.add("http://en.wikipedia.org/wiki/Type_II_supernova");
		urls.add("http://en.wikipedia.org/wiki/Elliptical_galaxies");
		
		try {
			PopulateWWW.run(urls);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Parser.run(urls);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "I have crawled, and I liked it!";
				
	}

	/*
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 *
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}*/
}
