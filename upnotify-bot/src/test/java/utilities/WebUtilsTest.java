package utilities;
import java.util.Arrays;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.codec.binary.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import utils.Config;
import utils.WebUtils;

public class WebUtilsTest {


//    static String[] missingWords(String s, String t) {
//        List<String> words = new ArrayList<String>();
//
//        String[] tTokens = t.split(" ");
//        String[] sTokens = s.split(" ");
//
//        System.out.println("sTokens = " + Arrays.asList(sTokens));
//        System.out.println("tTokens = " + Arrays.asList(tTokens));
//
//        for (int i = 0, j = 0; i < sTokens.length; i++) {
//            if (!sTokens[i].trim().equals(tTokens[j].trim())) {
//                words.add(sTokens[i]);
//            } else {
//                if (j >= tTokens.length - 1) {
//                    continue;
//                } else {
//                    j++;
//                }
//            }
//        }
//
//        return words.toArray(new String[0]);
//    }
	
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
	public void testGetNumericStringFromUrlAndXPathJsoup() {
		String url = "www.batikanor.com";
		String selectorPath = "#gatsby-focus-wrapper > div > div.layout-module--container--2TGku > div > span"; // "4 Comments"
		System.out.println(WebUtils.getWebUtils().getNumericStringFromUrlAndSelectorPathJsoup(url, selectorPath));
	}

}
