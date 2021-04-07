package utils;

import java.io.IOException;
import java.net.HttpURLConnection;
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
	
	/**
	 * Fixes the input string that should correspond to a url.
	 * Does this by assuring the connection is made via http:// 
	 * @param url input URL
	 * @return fixed string
	 */
	private String fixUrl(String url) {
		if (url.startsWith("http://")) {
			return url;
		} else if (url.startsWith("https://")) {
			return url.replaceFirst("https", "http");	
		} else {
			return "http://" + url;
		}
	}
	
	/**
	 * Returns HTML body as string using Java.Net.URLConnection library.
	 * @param url
	 * @return HTML Body as string
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

	/**
	 * Returns HTML body as Document using JSoup.
	 * @param url input URL
	 * @return HTML body as Document
	 */
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
	 * Returns element corresponding to the given URL and selector path using JSoup
	 * @param url input URL
	 * @param selectorPath e.g. //*[@id="content"]/div/div[1]/div[2]/div[2]/a
	 * @return
	 */
	public Element getElementFromUrlAndSelectorPathJsoup(String url, String selectorPath) {
		url = fixUrl(url);
		Document doc = getHTMLBodyFromUrlJSoup(url);
		
		Elements elements = doc.select(selectorPath);
		Element el = elements.get(0);
		return el;
	}
	
	/**
	 * @TODO  Doesn't work as expected, see testGetNumericStringFromUrlAndSelectorPathJsoup
	 * @param url
	 * @param selectorPath
	 * @return 
	 */
	public String getStringFromUrlAndSelectorPathJsoup(String url, String selectorPath) {
		Element el = getElementFromUrlAndSelectorPathJsoup(url, selectorPath);
		return el.text();
	}
	
	public String getHTTPResponseFromUrl(String url) {
		url = fixUrl(url);
		String response = null;
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			response = connection.getResponseMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		
		return response;
	}
	
	
	

	
	
}
