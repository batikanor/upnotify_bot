package objects;

import utils.DatabaseUtils;

/**
 * Stores info about a request from user side.
 * A (upnotify) Request object just like in the Database
 */
public class Request {
    public int requestId;
    public Long telegramId;
    public int snapshotId;
    public int checkInterval;
    public Long lastCheckedUnix; // Note: Only valid until 2038!
    public boolean isActive;

//    public Request() {
//		
//    	
//    }
    /**
     * Constructs a (upnotify) request instance using required parameters. This is usually copied from a row from Request table on DB.
     * @param requestId id of request to construct
     * @param telegramId telegram id of user/chat 
     * @param snapshotId id of snapshot that is tied to the request.
     * @param checkInterval Checking interval of resp. request
     * @param lastCheckedUnix Unix timestamp of last check
     * @param isActive Activity status of request
     */
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
    
    /**
     * Turns the request into a string using all its fields.
     */
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
