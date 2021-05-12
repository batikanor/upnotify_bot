package objects;

public class Request {
    public int requestId;
    public Long telegramId;
    public int snapshotId;
    public int checkInterval;
    public Long lastCheckedUnix; // ÖNEMLİ NOT: 2038'de değiştirmemiz gerekcek.
    public boolean isActive;
}
