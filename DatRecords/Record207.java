package DatRecords;

import java.io.PrintStream;
import java.nio.ByteBuffer;

import DatRecords.RecordType.cvsTermType;

public class Record207 extends RecordType {

    // 200 hZ

    static int ID = 207;

    static boolean _csvHeaderPrinted = false;

    ByteBuffer payload = null;

    double longitude = 0.0;

    double latitude = 0.0;

    double roll = (float) 0.0;

    double pitch = (float) 0.0;

    double yaw = (float) 0.0;

    int magMod = 0;

    double magYaw = 0.0;

    public Record207() {
        addCSVTerm("NumSats", cvsTermType.BYTE, 116);
        addCSVTerm("gpsAltitude", cvsTermType.FLOAT4, 16);
        addCSVTerm("accelX", cvsTermType.FLOAT4, 20);
        addCSVTerm("accelY", cvsTermType.FLOAT4, 24);
        addCSVTerm("accelZ", cvsTermType.FLOAT4, 28);
        addCSVTerm("gyroX", cvsTermType.FLOAT4, 32);
        addCSVTerm("gyroY", cvsTermType.FLOAT4, 36);
        addCSVTerm("gyroZ", cvsTermType.FLOAT4, 40);
        addCSVTerm("baroAlt", cvsTermType.FLOAT4, 44);
        addCSVTerm("quatW", cvsTermType.FLOAT4, 48);
        addCSVTerm("quatX", cvsTermType.FLOAT4, 52);
        addCSVTerm("quatY", cvsTermType.FLOAT4, 56);
        addCSVTerm("quatZ", cvsTermType.FLOAT4, 60);
        addCSVTerm("velN", cvsTermType.FLOAT4, 76);
        addCSVTerm("velE", cvsTermType.FLOAT4, 80);
        addCSVTerm("velD", cvsTermType.FLOAT4, 84);
        addCSVTerm("magX", cvsTermType.SHORT, 100);
        addCSVTerm("magY", cvsTermType.SHORT, 102);
        addCSVTerm("magZ", cvsTermType.SHORT, 104);
        addCSVTerm("REC207:X1", cvsTermType.FLOAT4, 64);
        addCSVTerm("REC207:X2", cvsTermType.FLOAT4, 68);
        addCSVTerm("REC207:X3", cvsTermType.FLOAT4, 72);
        addCSVTerm("REC207:X4", cvsTermType.FLOAT4, 88);
        addCSVTerm("REC207:X5", cvsTermType.FLOAT4, 92);
        addCSVTerm("REC207:X6", cvsTermType.FLOAT4, 96);
        addCSVTerm("REC207:I1", cvsTermType.SHORT, 106);
        addCSVTerm("REC207:I2", cvsTermType.SHORT, 108);
        addCSVTerm("REC207:I3", cvsTermType.SHORT, 110);
        addCSVTerm("REC207:I4", cvsTermType.SHORT, 112);
        addCSVTerm("REC207:I5", cvsTermType.SHORT, 114);
    }

    public void process(Payload _payload) {
        super.process(_payload);
        payload = _payload.getBB();
        longitude = Math.toDegrees(payload.getDouble(0));
        latitude = Math.toDegrees(payload.getDouble(8));

        float quatW = payload.getFloat(48);
        float quatX = payload.getFloat(52);
        float quatY = payload.getFloat(56);
        float quatZ = payload.getFloat(60);

        int magX = payload.getShort(100);
        int magY = payload.getShort(102);
        int magZ = payload.getShort(104);

        Quaternion q = new Quaternion(quatW, quatX, quatY, quatZ);
        double[] eulerAngs = q.toEuler();
        roll = Math.toDegrees(eulerAngs[0]);
        pitch = Math.toDegrees(eulerAngs[1]);
        yaw = Math.toDegrees(eulerAngs[2]);
        magMod = (int) Math.sqrt(magX * magX + magY * magY + magZ * magZ);
        magYaw = 0.0;
        if (!Double.isNaN(pitch)) {
            magYaw = -Math.toDegrees(Math.atan2(magY, magX));
        }
    }

    public int getID() {
        return 207;
    }

    public void printCsvHeader(PrintStream _csv) {
        _csv.print(",Longitude" + ",Latitude");
        super.printCsvHeader(_csv);
        _csv.print(",Roll" + ",Pitch" + ",Yaw" + ",MagMod" + ",MagYaw");
    }

    public void printCsvLine(PrintStream _csv) {
        _csv.print("," + longitude + "," + latitude);
        super.printCsvLine(_csv);
        _csv.print("," + roll + "," + pitch + "," + yaw + "," + magMod + ","
                + magYaw);
    }
}
