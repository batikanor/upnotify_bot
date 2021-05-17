package objects;

import java.awt.image.BufferedImage;

public class Snapshot {
    public int snapshotId;
    public String url ;
    public BufferedImage screenshot;
    public String siteContentHash;

    public Snapshot(){

    }
    public Snapshot(int snapshotId,String url,BufferedImage screenshot,String siteContentHash){
        this.snapshotId = snapshotId;
        this.url = url;
        this.screenshot = screenshot;
        this.siteContentHash = siteContentHash;
    }
}
