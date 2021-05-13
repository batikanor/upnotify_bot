package objects;

import java.sql.Blob;

public class Snapshot {
    public int snapshotId;
    public String url ;
    public Blob screenshot;
    public String siteContentHash;

    public Snapshot(){

    }
    public Snapshot(int snapshotId,String url,Blob screenshot,String siteContentHash){
        this.snapshotId = snapshotId;
        this.url = url;
        this.screenshot = screenshot;
        this.siteContentHash = siteContentHash;
    }
}
