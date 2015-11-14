package Files;

public class Corrupted extends Exception {
    long tickNo = 0;

    long filePos = 0;

    public Corrupted(long _tickNo, long _filePos) {
        tickNo = _tickNo;
        filePos = _filePos;
    }
    public Corrupted() {
        
    }

    public String toString() {
        return "Partial or missing record at or near tickNo " + tickNo
                + ", file Position " + filePos;
    }

    public long getFilePos() {
        return filePos;
    }

}
