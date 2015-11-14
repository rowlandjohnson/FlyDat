package DatRecords;

import java.io.PrintStream;

public class Record42 extends RecordType {
    double longitude = 0.0;

    double latitude = 0.0;

    // 10 hz
    
    public Record42() {
        // TODO Auto-generated constructor stub
    }

    public void process(Payload _payload) {
        super.process(_payload);
        payloadBB = _payload.getBB();
        longitude = Math.toDegrees(payloadBB.getDouble(0));
        latitude = Math.toDegrees(payloadBB.getDouble(8));
    }

    public void printCsvHeader(PrintStream _csv) {
        _csv.print(",REC42:Longitude" + ",REC42:Latitude");

    }

    public void printCsvLine(PrintStream _csv) {
        _csv.print("," + longitude + "," + latitude);

    }

    public int getID() {
        return 42;
    }
}
