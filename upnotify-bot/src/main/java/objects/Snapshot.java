package objects;

import java.awt.image.BufferedImage;

/**
 * Stores the last response stance of a request
 */
public class Snapshot {
    public int snapshotId;
    public String url ;
    public BufferedImage screenshot;
    public String siteContentHash;

    /**
     * Default empty constructor for a snapshot
     */
    public Snapshot(){

    }
    /**
     * Instantiates a snapshot object by mapping fields in the database to fields in this instance
     * @param snapshotId id of resp. snapshot
     * @param url url to be checked by the request
     * @param screenshot last visual stance of the snapshot (or null if not visually checked)
     * @param siteContentHash last hash of the snapshot (or null if hash is not checked)
     */
    public Snapshot(int snapshotId,String url,BufferedImage screenshot,String siteContentHash){
        this.snapshotId = snapshotId;
        this.url = url;
        this.screenshot = screenshot;
        this.siteContentHash = siteContentHash;
    }
}
