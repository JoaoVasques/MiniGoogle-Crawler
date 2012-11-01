package pt.utl.ist.cn.crawler.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

import pt.utl.ist.cn.crawler.server.persistence.PMF;
import pt.utl.ist.cn.crawler.shared.WebPageFetcher;

public class PopulateWWW 
{

	public static void run(List<String> urls) throws IOException 
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		WebSiteInfo site;

		/*
		boolean debug = true;
		
		if(debug)
		{
			Query query = pm.newQuery(WebSiteInfo.class);
		    //query.setFilter("lastName == lastNameParam");
		    //query.setOrdering("hireDate desc");
		    //query.declareParameters("String lastNameParam");

		    try {
		        @SuppressWarnings("unchecked")
				List<WebSiteInfo> results = (List<WebSiteInfo>) query.execute();
		        if (!results.isEmpty()) {
		            for (WebSiteInfo e : results) {
		            	pm.deletePersistent(e);
		            }
		        }
		    } finally {
		        query.closeAll();
		    }
		}
		*/
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
					WebPageFetcher fetcher = new  WebPageFetcher(url);
					site = new WebSiteInfo(url, createBlobFile(url, fetcher.getPageHeader(), fetcher.getPageContent()));

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
		}
		finally 
		{
			pm.close();
		}
	}

	private static String createBlobFile(String url, String header, String body) throws IOException
	{
		// Get a file service
		FileService fileService = FileServiceFactory.getFileService();

		// Create a new Blob file with mime-type "text/plain"
		AppEngineFile file;
		try {
			file = fileService.createNewBlobFile("text/plain", url + ".html");
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
		out.write(body);

		// Close without finalizing and save the file path for writing later
		out.close();
		writeChannel.closeFinally();

		return file.getFullPath();
	}
}
/*













			// Creating input file
			//file = new File("~/hadoop-0.20.2/bin/MiniGoogleDatabase/input/" + url);
			inputFilePath = new Path(inputDirectoryPath.getName() + "/" + url);
			try {
				if(!hadoopFileSystem.exists(inputFilePath))
				{
					hadoopFileSystem.createNewFile(inputFilePath);
					//file.createNewFile();

					fetcher = new  WebPageFetcher(url);

					header = fetcher.getPageHeader();
					body = fetcher.getPageContent();

					fout = new FileOutputStream(file);
					fout.write(header.getBytes());
					fout.write(body.getBytes());
					fout.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
		}
	}
}
 */