package utils;

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
	public String getHTMLBodyFromUrl(String url);
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
	public String getHTMLBodyFromUrl(String url) {
		// TODO Auto-generated method stub
		
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
		//System.out.println(content);
		Document doc = Jsoup.parse(content);
		Elements elements = doc.select("body").first().children();
		//or only `<p>` elements
		//Elements elements = doc.select("p"); 
		//for (Element el : elements)
		    //System.out.println("element: "+el);
	
		//return content;
		System.out.println(elements.toString());
		return elements.toString();

	}

	

	
	
	
	

	
	
}
