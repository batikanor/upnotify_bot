package upnotify_bot;

import objects.Request;
import objects.Snapshot;

import utils.DatabaseUtils;

public class UpnotifyReceiver implements Runnable{
	private Request upnotify;
	
	public UpnotifyReceiver(Request upnotify) {
		this.upnotify = upnotify;
	}
	
	// This is where a single upnotify Request will run. 
	@Override
	public void run() {
		
		// Check if enough time has passed after last control
		long unixTime = System.currentTimeMillis() / 1000L;
		
		long diff = unixTime - upnotify.lastCheckedUnix;
		
		long expectedWait = upnotify.checkInterval * 60;
		if (diff < expectedWait) {
			try {
				Thread.sleep(expectedWait - diff);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		// TODO add a field isActive to Request object, so that it can be the controller of this while loop
		while(true) {
			// work
			// get snapshot object
			Snapshot snap = DatabaseUtils.getDatabaseUtils().retrieveSnapshotFromId(upnotify.snapshotId);
			
			// check non-null fields in snap again, report any changes
			
					
			if (snap.screenshot != null) {
				// take new screenshot, compare with old using ImageUtils
				System.out.println('Comparing current screenshot with one on database');
			}
			
			if (snap.siteContentHash != null) {
				System.out.println('Comparing current site content hash with one on database');
				// take new hash
				
			}
			
			// wait
			try {
				Thread.sleep(upnotify.checkInterval * 60 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
	}

}
