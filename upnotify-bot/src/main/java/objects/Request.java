package objects;

public class Request {
    public int requestId;
    public Long telegramId;
    public int snapshotId;
    public int checkInterval;
    public Long lastCheckedUnix; // ÖNEMLİ NOT: 2038'de değiştirmemiz gerekcek.
    public boolean isActive;

    public Request(){
    }

    public Request(int requestId,Long telegramId,int snapshotId,int checkInterval,Long lastCheckedUnix
                    ,boolean isActive){
        this.requestId = requestId;
        this.telegramId = telegramId;
        this.snapshotId = snapshotId;
        this.checkInterval = checkInterval;
        this.lastCheckedUnix = lastCheckedUnix;
        this.isActive = isActive;
    }
}
