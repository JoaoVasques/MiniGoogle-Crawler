package pt.utl.ist.cn.crawler.server;

import java.math.BigDecimal;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;


@PersistenceCapable
public class WebSiteInfo {

	@PrimaryKey
	private String url;
	
	@Persistent
	private BigDecimal pageRank;

	@Persistent
	private String title;

	@Persistent
	private String description;

	@Persistent
	private String keyWords;

	@Persistent
	private Text siteLinks;
	
	@Persistent
	private int numberOfsiteLinks;
	
	@Persistent
	private String mediaLinks;
	
	@Persistent
	private String htmlBlobStorePath;
	
	@Persistent
	private String textBlobStorePath;

	
	public WebSiteInfo(String url, String htmlBlobStorePath) {
		super();
		this.url = url;
		this.htmlBlobStorePath = htmlBlobStorePath;
		
		this.pageRank = new BigDecimal(1.0);
		this.title = new String();
		this.description = new String();
		this.keyWords = new String();
		this.siteLinks = new Text("");
		this.numberOfsiteLinks = 0;
		this.mediaLinks = new String();
		this.textBlobStorePath = new String();
	}
	

	public WebSiteInfo(String url, BigDecimal pageRank, String title,
			String description, String keyWords,
			Text siteLinks, String mediaLinks,
			String htmlBlobStorePath, String textBlobStorePath) {
		super();
		this.url = url;
		this.pageRank = pageRank;
		this.title = title;
		this.description = description;
		this.keyWords = keyWords;
		this.siteLinks = siteLinks;
		this.mediaLinks = mediaLinks;
		this.htmlBlobStorePath = htmlBlobStorePath;
		this.textBlobStorePath = textBlobStorePath;
	}


	public String getUrl() {
		return url;
	}

	public BigDecimal getPageRank() {
		return pageRank;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPageRank(BigDecimal pageRank) {
		this.pageRank = pageRank;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public Text getSiteLinks() {
		return siteLinks;
	}

	public String getMediaLinks() {
		return mediaLinks;
	}

	public String getHtmlBlobStorePath() {
		return htmlBlobStorePath;
	}

	public String getTextBlobStorePath() {
		return textBlobStorePath;
	}

	public void setSiteLinks(Text siteLinks) {
		this.siteLinks = siteLinks;
	}

	public void setMediaLinks(String mediaLinks) {
		this.mediaLinks = mediaLinks;
	}

	public void setHtmlBlobStorePath(String htmlBlobStorePath) {
		this.htmlBlobStorePath = htmlBlobStorePath;
	}

	public void setTextBlobStorePath(String textBlobStorePath) {
		this.textBlobStorePath = textBlobStorePath;
	}

	public int getNumberOfsiteLinks() {
		return numberOfsiteLinks;
	}

	public void setNumberOfsiteLinks(int numberOfsiteLinks) {
		this.numberOfsiteLinks = numberOfsiteLinks;
	}

}
