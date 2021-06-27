package objects;

import utils.DatabaseUtils;

public class Request {
    public int requestId;
    public Long telegramId;
    public int snapshotId;
    public int checkInterval;
    public Long lastCheckedUnix; // ÖNEMLİ NOT: 2038'de değiştirmemiz gerekcek.
    public boolean isActive;

//    public Request() {
//		
//    	
//    }
    public Request(int requestId,Long telegramId,int snapshotId,int checkInterval,Long lastCheckedUnix
                    ,boolean isActive){
        this.isActive = isActive;
		this.requestId = requestId;
        this.telegramId = telegramId;
        this.snapshotId = snapshotId;
        this.checkInterval = checkInterval;
        this.lastCheckedUnix = lastCheckedUnix;
        this.isActive = isActive;
    }
    
    @Override
    public String toString() {
    	
    	Snapshot snap = DatabaseUtils.getDatabaseUtils().retrieveSnapshotFromId(snapshotId);
    	System.out.println(String.format("Turning request with id %d to string...", this.requestId));
    	String txt = "Request No: " + requestId + " ~~~ "
    + "Url:  " + snap.url + ", check interval: " + checkInterval
    + ", checking screenshot (ss) = " + Boolean.toString(snap.screenshot != null)
    + ", checking site content hash (sch) = " + Boolean.toString(snap.siteContentHash != null)
    + "\n{This request is " + (isActive ? "ACTIVE" : "PASSIVE") + "} ";
		return txt;
    	
    }
}
