package utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import utils.Config.OS;


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
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		// TODO check if the site has ssl, if so return https if not return http
		return "https://" + url; 
		
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
	 * @return text found within the inputted element.
	 */
	public String getStringFromUrlAndSelectorPathJsoup(String url, String selectorPath) {
		Element el = getElementFromUrlAndSelectorPathJsoup(url, selectorPath);
		return el.text();
	}


 
	public String getStringFromUrlAndSelectorPathUsingJsoupAndSelenium(String url, String selectorPath) {
		//URL driverURL = getClass().getClassLoader().getResource("chromedriver.exe");
		//System.out.println("URL:" + driverURL);
		

		String path = "CHROME_DRIVERS/chromedriver_89_" + (Config.getConfig().os == OS.LINUX ? "linux" : "win.exe");
//		System.out.println("path: " + path);
		
		URL chrome_driver_url = getClass().getClassLoader().getResource(path);

		System.out.println(chrome_driver_url);
		String chrome_driver_path = chrome_driver_url.getPath();
//		System.out.println("driver path: " + chrome_driver_path);
		//String chrome_driver_path = "src/main/resources/CHROME_DRIVERS/chromedriver_89_" + (Config.getConfig().os == OS.LINUX ? "linux" : "win.exe");
		System.setProperty("webdriver.chrome.driver", chrome_driver_path);
		WebDriver driver = new ChromeDriver();
		driver.get(fixUrl(url));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		Document doc = Jsoup.parse(driver.getPageSource());
		Elements elements = doc.select(selectorPath);
		Element el = elements.get(0);
		return el.text();
		
	}

	public void getScreenshotUsingSelenium(String url, String selectorPath) {
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get(url);
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
