package upnotify_bot;

import java.awt.image.BufferedImage;

import objects.Request;
import objects.Snapshot;
import utils.Config;
import utils.DatabaseUtils;
import utils.ImageUtils;
import utils.MessageUtils;
import utils.WebUtils;

public class UpnotifyReceiver implements Runnable{
	private UpnotifyBot ub;
	private Request upnotify;
	private boolean notificationRequired;
	private String notificationTxt;
	private BufferedImage notificationIm;
	public UpnotifyReceiver(UpnotifyBot ub, Request upnotify) {
		this.upnotify = upnotify;
		this.ub = ub;
	}
	
	// This is where a single upnotify Request will run. 
	@Override
	public void run() {
		
		// Check if enough time has passed after last control
		// in sec
		long unixTime = System.currentTimeMillis() / 1000L;
		
		long diff = unixTime - upnotify.lastCheckedUnix;
		
		long expectedWait = Config.getConfig().MIN_WAIT_LEVEL[upnotify.checkInterval] * 60;
		System.out.println("[Request "+ upnotify.requestId + "] ExpectedWait-diff = " + (expectedWait-diff));
		if (diff < expectedWait) {

			System.out.println("[Request "+ upnotify.requestId + "] waiting for  " + (expectedWait - diff) + " seconds");
			try {
				Thread.sleep((expectedWait - diff) * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		// TODO add a field isActive to Request object, so that it can be the controller of this while loop
		while(true) {
			// work
			// get snapshot object
			System.out.println("[Request "+ upnotify.requestId + "] Retrieving snapshot from db");
			Snapshot snap = DatabaseUtils.getDatabaseUtils().retrieveSnapshotFromId(upnotify.snapshotId);
			System.out.println("[Request "+ upnotify.requestId + "] Retrieved snapshot from db");
			notificationTxt = "Hello, this is a notification about your request no: " + upnotify.requestId 
			+ "\non url: " + snap.url 
			+ "\n There has been an update!";
			// check non-null fields in snap again, report any changes
			notificationIm = null;
			notificationRequired = false;
			
			if (snap.siteContentHash != null) {
				System.out.println("[Request "+ upnotify.requestId + "]Comparing current site content hash with one on database");
				
				// take new hash
				String newHash = WebUtils.getWebUtils().getHTMLBodyStringHash(snap.url);
				
				if (newHash != snap.siteContentHash){
					
					notificationRequired = true;
					notificationTxt += "\nThe site has been changed! The hash value of the site content was " + snap.siteContentHash + " and now is " + newHash;
				}
				
			}

			if (snap.screenshot != null) {
				System.out.println("[Request "+ upnotify.requestId + "]Comparing current screenshot with one on database");

				// take new screenshot, compare with old using ImageUtils
				BufferedImage newSs = WebUtils.getWebUtils().getScreenshotUsingSelenium(snap.url);
				objects.ImageDifferenceData imDiff = ImageUtils.getImageUtils().getDifferenceHighlightedResult(snap.screenshot, newSs);
				
				if (imDiff.diffPercentage > Config.getConfig().IMAGE_DIFFERENCE_THRESHOLD) {

					notificationRequired = true;
					notificationTxt += "\nThe site looks different! There has been a  " +  imDiff.diffPercentage + "% change on the look of the site!";
					notificationIm = imDiff.diffIm;
				}


			}

			

			
			if (notificationRequired){
				// notify users about the changes
				System.out.println(notificationTxt);
				MessageUtils.getMessageUtils().sendNotificationMessage(ub, upnotify.telegramId ,notificationTxt, notificationIm);
			}

			
			// wait
			int waitMin = Config.getConfig().MIN_WAIT_LEVEL[upnotify.checkInterval];
			System.out.println("[Request: "+ upnotify.requestId + "]Waiting for: " + waitMin + " minutes");
			
			
			try {
				Thread.sleep(waitMin * 60 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
	}

}
