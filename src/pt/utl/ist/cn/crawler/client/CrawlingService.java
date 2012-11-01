package pt.utl.ist.cn.crawler.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("crawl")
public interface CrawlingService extends RemoteService {
	String crawlServer(/*String input*/) throws IllegalArgumentException;
}
