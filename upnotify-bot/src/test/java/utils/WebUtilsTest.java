package utils;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import utils.Config;
import utils.WebUtils;

public class WebUtilsTest {

	
	@Test
	public void testGetHTMLBodyStringFromUrl() {
		
		// check some static websites out
		List<String> staticSites = Arrays.asList("batikanor.com","www.batikanor.tk");
		
		
	
		for (String url : staticSites) {

			String tt = WebUtils.getWebUtils().getHTMLBodyStringFromUrl(url);

			try {
				Thread.sleep(Config.getConfig().WAIT_STATIC_CHECK); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String tt2 = WebUtils.getWebUtils().getHTMLBodyStringFromUrl(url);
			
			// TODO Replace prints with logs at a later time...
			System.out.println("\n Diff: \n" + StringUtils.difference(tt, tt2) + "\n:DiffEnd\n");
			Assert.assertEquals(tt, tt2);
			
		}

		
		
	}
	@Test
	public void testGetHTMLBodyStringFromUrlJSoup() {
		List<String> staticSites = Arrays.asList("batikanor.com","www.batikanor.tk");
		for (String url : staticSites) {

			String tt = WebUtils.getWebUtils().getHTMLBodyStringFromUrlJSoup(url);

			try {
				Thread.sleep(Config.getConfig().WAIT_STATIC_CHECK);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String tt2 = WebUtils.getWebUtils().getHTMLBodyStringFromUrlJSoup(url);
			
			// TODO Replace prints with logs at a later time...
			System.out.println("\n Diff: \n" + StringUtils.difference(tt, tt2) + "\n:DiffEnd\n");
			Assert.assertEquals(tt, tt2);
			
		}
	
	}
	
	@Test
	public void testGetStringFromUrlAndSelectorPathJsoup() {
		String url = "www.batikanor.com";
		String selectorPath = "#gatsby-focus-wrapper > div > div.layout-module--container--2TGku > div > span"; // "4 Comments"
		String res = WebUtils.getWebUtils().getStringFromUrlAndSelectorPathJsoup(url, selectorPath);
		System.out.println("Result: " + res);
		Assert.assertTrue(res.endsWith("Comments")); // Should be "4 Comments" or so...
	}
	
	@Test
	public void testGetHTTPResponseFromUrl() {
		String url = "www.batikanor.com";
		String res = WebUtils.getWebUtils().getHTTPResponseFromUrl(url);
		System.out.println("Res: " + res);
		Assert.assertEquals(res, "Moved Permanently"); // cuz it should redirect from http to https.
		
	}

	@Test
	public void testGetStringFromUrlAndSelectorPathUsingSelenium() {
		String url = "http://www.batikanor.com";
		String selectorPath = "#gatsby-focus-wrapper > div > div.layout-module--container--2TGku > div > span"; // "4 Comments"
		String res = WebUtils.getWebUtils().getStringFromUrlAndSelectorPathUsingJsoupAndSelenium(url, selectorPath);
		System.out.println("Result: " + res);
		Assert.assertTrue(res.endsWith("Comments"));
	}
	@Test
	public void testSaveScreenshotUsingSelenium() {
		boolean checkIfExists = false;
		String url = "http://www.batikanor.com";
		Integer requestId = 10;
		String selectorPath = "#gatsby-focus-wrapper > div > div.layout-module--container--2TGku > div > span"; // "4 Comments"
		try {
			checkIfExists = WebUtils.getWebUtils().saveScreenshotUsingSelenium(url, selectorPath, requestId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertTrue(checkIfExists);

		//delete the taken screenshot after if assertion is successful

		try {
			Path fileToDeletePath = Paths.get("src/main/resources/SELENIUM_SCREENSHOTS/" + requestId + ".png");
			Files.delete(fileToDeletePath);
		} catch (Exception e) {
			System.out.println("file exception during test");
		}

	}

}
