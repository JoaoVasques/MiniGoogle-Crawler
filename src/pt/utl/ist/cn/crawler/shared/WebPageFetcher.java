package pt.utl.ist.cn.crawler.shared;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import pt.utl.ist.cn.crawler.shared.Log;

/** Fetches the HTML content of a web page (or HTTP header) as a String. */
public class WebPageFetcher {

	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public static final String HEADER = "header";
	public static final String CONTENT = "content";
	public static final String END_OF_INPUT = "\\Z";
	public static final String NEWLINE = System.getProperty("line.separator");

	// PRIVATE //
	private URL fURL;

	public WebPageFetcher( URL aURL ){
		if ( ! HTTP.equals(aURL.getProtocol()) && ! HTTPS.equals(aURL.getProtocol())) {
			throw new IllegalArgumentException("URL is not for HTTP Protocol: " + aURL);
		}
		fURL = aURL;
	}

	public WebPageFetcher( String aUrlName ) throws MalformedURLException {
		this ( new URL(aUrlName) );
	}

	/** Fetch the HTML content of the page as simple text.   */
	public String getPageContent() {
		String result = null;
		URLConnection connection = null;
		try {
			connection =  fURL.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter(END_OF_INPUT);
			result = scanner.next();
		}
		catch ( IOException ex ) {
			Log.log("Cannot open connection to " + fURL.toString());
		}
		return result;
	}

	/** Fetch HTML headers as simple text.  */
	public String getPageHeader(){
		StringBuilder result = new StringBuilder();

		URLConnection connection = null;
		try {
			connection = fURL.openConnection();
		}
		catch (IOException ex) {
			Log.log("Cannot open connection to URL: " + fURL);
		}

		//not all headers come in key-value pairs - sometimes the key is
		//null or an empty String
		int headerIdx = 0;
		String headerKey = null;
		String headerValue = null;
		while ( (headerValue = connection.getHeaderField(headerIdx)) != null ) {
			headerKey = connection.getHeaderFieldKey(headerIdx);
			if ( headerKey != null && headerKey.length()>0 ) {
				result.append( headerKey );
				result.append(" : ");
			}
			result.append( headerValue );
			result.append(NEWLINE);
			headerIdx++;
		}
		return result.toString();
	}

	public Boolean linkTester()
	{
		URLConnection connection = null;
		
		try {
			connection =  fURL.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter(WebPageFetcher.END_OF_INPUT);
			scanner.next();
		}
		catch ( IOException ex ) {
			return false;
		}
		
		return true;
	}

} 