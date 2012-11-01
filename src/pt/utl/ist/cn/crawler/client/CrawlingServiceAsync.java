package pt.utl.ist.cn.crawler.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CrawlingService</code>.
 */
public interface CrawlingServiceAsync {
	void crawlServer(/*String input, */AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
