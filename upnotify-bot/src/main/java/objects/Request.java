package objects;

public class Request {
    public int requestId;
    public int telegramId;
    public int snapshotId;
    public int checkInterval;
    public int lastCheckedUnix; // ÖNEMLİ NOT: 2038'de değiştirmemiz gerekcek.
}
