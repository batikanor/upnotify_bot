package utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


interface WebUtilsInterface{
	
	/**
	 * Returns the http response code
	 * @param url url of the website
	 * @return
	 */
	public String getHTMLBodyStringFromUrl(String url);
}

public class WebUtils implements WebUtilsInterface{
	
	private static WebUtils single_instance = null;
	
	public static WebUtils getWebUtils() {
		if (single_instance == null) {
			single_instance = new WebUtils();
			System.out.println("Instance of 'WebUtils' has been created");
		}
		return single_instance;
		
	}
	// Has only a private constructor, so that only one instance can exist
	private WebUtils() {}
	
	
	private String fixUrl(String url) {
		if (!(url.startsWith("http://") || url.startsWith("https://"))) {
			url = "http://" + url;
		} 
		return url;
		
	}

	
	/**
	 * 
	 * @param url
	 */
	public String getHTMLBodyStringFromUrl(String url) {
		
		url = fixUrl(url);
			
		
		String content = null;
		URLConnection connection = null;
		try {
		  connection =  new URL(url).openConnection();
		  Scanner scanner = new Scanner(connection.getInputStream());
		  scanner.useDelimiter("\\Z");
		  content = scanner.next();
		  scanner.close();
		}catch ( Exception ex ) {
		    ex.printStackTrace();
		}

		return content;

	}

	public Document getHTMLBodyFromUrlJSoup (String url) {
		url = fixUrl(url);
		try {
			Document doc = Jsoup.connect(url)
					.timeout(6000).get();
//			System.out.println(doc);
			return doc;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO nope
		return null;
		
	}
	public String getHTMLBodyStringFromUrlJSoup (String url) {
		return getHTMLBodyFromUrlJSoup(url).toString();
		
	}
	
	

	/**
	 * 
	 * @param url
	 * @param xPath e.g. //*[@id="content"]/div/div[1]/div[2]/div[2]/a
	 * @return
	 */
	public Element getNumericElementFromUrlAndSelectorPathJsoup(String url, String selectorPath) {
		url = fixUrl(url);
		Document doc = getHTMLBodyFromUrlJSoup(url);
		
		Elements elements = doc.select(selectorPath);
		Element el = elements.get(0);
		return el;
	}
	
	public String getNumericStringFromUrlAndSelectorPathJsoup(String url, String selectorPath) {
		Element el = getNumericElementFromUrlAndSelectorPathJsoup(url, selectorPath);
		
		return el.text();
		// <span class="disqus-comment-count" data-disqus-identifier="index" data-disqus-url="http://www.batikanor.com + /index">...</span>
		
		
		
	}
	
	
	

	
	
}
