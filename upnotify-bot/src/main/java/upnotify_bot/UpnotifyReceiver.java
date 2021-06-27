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
	private objects.ImageDifferenceData imDiff;
	private volatile boolean shutdown = false;
	
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
		
		
		/**
		 * note that the upnotify instance in this may not be up to date. 
		 * 	option 1: check the db at every iteration and actualize
		 * 	option 2(chosen): just shut thread down when toggling activity, and add it back on later on
		 */
		while(!shutdown && upnotify.isActive) {
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
				
				if (!newHash.contentEquals(snap.siteContentHash)){
					notificationRequired = true;
					notificationTxt += "\nThe site has been changed! The hash value of the site content was " + snap.siteContentHash + " and now is " + newHash;
					snap.siteContentHash = newHash;
				}
							
			}

			if (snap.screenshot != null) {
				System.out.println("[Request "+ upnotify.requestId + "]Comparing current screenshot with one on database");

				// take new screenshot, compare with old using ImageUtils
				BufferedImage newSs = WebUtils.getWebUtils().getScreenshotUsingSelenium(snap.url);
				imDiff = ImageUtils.getImageUtils().getDifferenceHighlightedResult(snap.screenshot, newSs);
				
				if (imDiff.diffPercentage > Config.getConfig().IMAGE_DIFFERENCE_THRESHOLD) {

					notificationRequired = true;
					notificationTxt += "\nThe site looks different! There has been a  " +  imDiff.diffPercentage + "% change on the look of the site!";
					notificationIm = imDiff.diffIm;
					snap.screenshot = newSs;
				}

			}

			
			
			if (notificationRequired){
				// notify users about the changes
				System.out.println(notificationTxt);
				MessageUtils.getMessageUtils().sendNotificationMessage(ub, upnotify.telegramId ,notificationTxt, notificationIm);
				//edit
				DatabaseUtils.getDatabaseUtils().editRequest(upnotify, snap);
			} else if  (snap.screenshot != null && imDiff.diffPercentage > 0) {
				//edit
				DatabaseUtils.getDatabaseUtils().editRequest(upnotify, snap);
			}
			
	
			// wait
			//System.out.println("CHECKINTERVALLLLL" + upnotify.checkInterval);
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
	
	public void shutdown() {
		shutdown = true;
	}

}