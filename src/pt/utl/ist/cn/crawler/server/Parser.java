package pt.utl.ist.cn.crawler.server;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.utl.ist.cn.crawler.server.persistence.PMF;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.LockException;

//import pt.utl.ist.cn.test.Log;

public class Parser {

	private static final int MAX_LINKS = 50;
	
	private String url, siteText, textOnly, plainTextBlobPath;
	private int textOnlyLen;

	private ArrayList<String> siteLinks, mediaLinks;


	public Parser(String url, String siteText, String plainTextBlobPath) {
		this.siteText = siteText;
		this.url = url;
		textOnly = Jsoup.parse(siteText).text();
		textOnlyLen = textOnly.length();

		this.plainTextBlobPath = plainTextBlobPath;

		try {
			createPlainTextBlobFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listLinks();
	}


	public String getTitle() {
		return Jsoup.parse(siteText).title();
	}

	public Text getSiteLinks() {
		String out = new String();
		int count = 0;
		
		for (String link : siteLinks) {
			count++;
			if(count == MAX_LINKS)
			{
				break;
			}
			
			/*
			if((out.length() + link.length() + 1) > 500)
			{
				break;
			}
			*/
			out = out + (count == 0 ? "" : " " ) + link;
		}
		
		return new Text(out);
	}
	
	public int getNumberOfSiteLinks()
	{
		return (siteLinks.size() > 50 ? 50 : siteLinks.size());
	}

	public String getMediaLinks() {
		String out = new String();
		int count = 0;
		
		for (String link : mediaLinks) {
			if((out.length() + link.length() + 1) > 500)
			{
				break;
			}
			
			out = out + (count == 0 ? "" : " " ) + link;
			
			count++;
			if(count == MAX_LINKS)
			{
				break;
			}
		}
		
		return out;
	}

	public String getDescription()
	{
		return textOnly.substring(0, textOnlyLen < 500 ? textOnlyLen : 499);
	}

	public String getPlainTextBlobPath()
	{
		return plainTextBlobPath;
	}


	public static void run(List<String> urls) throws IOException
	{
		Parser parser;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		WebSiteInfo site;

		try
		{
			for (String url : urls) 
			{
				try
				{
					site = pm.getObjectById(WebSiteInfo.class, url);
				}
				catch (javax.jdo.JDOObjectNotFoundException e)
				{
					e.printStackTrace();
					continue;
				}

				if(!site.getTitle().isEmpty())
				{
					continue;
				}
				
				try {
					parser = new Parser(url, loadUrlText(site.getHtmlBlobStorePath()), site.getTextBlobStorePath());
				} catch (FileNotFoundException  e) {
					e.printStackTrace();
					continue;
				}

				site.setTitle(parser.getTitle());
				site.setSiteLinks(parser.getSiteLinks());
				site.setNumberOfsiteLinks(parser.getNumberOfSiteLinks());
				//site.setMediaLinks(parser.getMediaLinks());
				site.setDescription(parser.getDescription());

				site.setTextBlobStorePath(parser.getPlainTextBlobPath());

				try
				{
					pm.makePersistent(site);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		finally 
		{
			pm.close();
		}

	}

	private static String loadUrlText(String blobstorePath) throws FileNotFoundException, LockException, IOException {
		int ch;

		if(blobstorePath == null)
		{
			throw new FileNotFoundException();
		}
		
		if(blobstorePath.isEmpty())
		{
			throw new FileNotFoundException();
		}

		// Get a file service
		FileService fileService = FileServiceFactory.getFileService();

		// Create a new Blob file with mime-type "text/plain"
		AppEngineFile file = new AppEngineFile(blobstorePath);

		// Later, read from the file using the file API
		FileReadChannel readChannel = fileService.openReadChannel(file, true);

		// Again, different standard Java ways of reading from the channel.
		BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));  
		//String line = reader.readLine();
		// line = "The woods are lovely dark and deep."

		StringBuffer textRed = new StringBuffer("");

		while( (ch = reader.read()) != -1)
		{
			textRed.append((char)ch);
		}

		readChannel.close();

		return textRed.toString();
	}

	private void createPlainTextBlobFile() throws IOException
	{
		if(plainTextBlobPath.isEmpty())
		{
			// Get a file service
			FileService fileService = FileServiceFactory.getFileService();

			// Create a new Blob file with mime-type "text/plain"
			AppEngineFile file;
			try {
				file = fileService.createNewBlobFile("text/plain", url + ".text");
			} catch (IOException e1) {
				throw e1;
			}

			// Open a channel to write to it
			boolean lock = true;
			FileWriteChannel writeChannel;

			writeChannel = fileService.openWriteChannel(file, lock);

			// Different standard Java ways of writing to the channel
			// are possible. Here we use a PrintWriter:
			PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
			//		out.write(header);
			out.write(textOnly);

			// Close without finalizing and save the file path for writing later
			out.close();
			writeChannel.closeFinally();

			plainTextBlobPath = file.getFullPath();
		}
	}

	private void listLinks() 
	{
		Document doc = new Document(url);
		doc.append(siteText);

		siteLinks = new ArrayList<String>();
		mediaLinks = new ArrayList<String>();

		Elements links = doc.select("a[href]");
		Elements media = doc.select("[src]");
		//		Elements imports = doc.select("link[href]");

		//		print("\nMedia: (%d)", media.size());
		for (Element src : media) {
			if (src.tagName().equals("img") && (! mediaLinks.contains(src.attr("abs:src"))))
			{
				mediaLinks.add(src.attr("abs:src"));	
				//				print(" * %s: <%s> %sx%s (%s)",	src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"), trim(src.attr("alt"), 20));
			}
			//			else
			//				print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
			
			if(mediaLinks.size() >= MAX_LINKS)
			{
				break;
			}
		}

		//		print("\nImports: (%d)", imports.size());
		//		for (Element link : imports) {
		//			print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
		//	}

		//		print("\nLinks: (%d)", links.size());*/
		for (Element link : links) {
			if(! siteLinks.contains(link.attr("abs:href")))
			{
				siteLinks.add(link.attr("abs:href"));
			}
			
			if(mediaLinks.size() >= MAX_LINKS)
			{
				break;
			}
			//			print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
		}

		//return outLinks;
	}

	/*
	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width-1) + ".";
		else
			return s;
	}
	 */
}
