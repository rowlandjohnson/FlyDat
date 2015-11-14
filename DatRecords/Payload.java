package DatRecords;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import Files.DatFile;
import Files.FileEnd;

public class Payload {

    byte xorArray[] = null;

    ByteBuffer BB = null;

    int length = 0;

    //long filePos = 0;

    int segType = 0;

    long tickNo = 0;

    long start = 0;
    byte flags[] = new byte[3];

    public Payload(DatFile df, long _start, int _length, int _segtype,
            long _tickNo, byte[] _flags) throws IOException, FileEnd {
        start = _start;
        length = _length;
        segType = _segtype;
        tickNo = _tickNo;
        flags = _flags;

        xorArray = new byte[length];
        BB = ByteBuffer.wrap(xorArray).order(ByteOrder.LITTLE_ENDIAN);

        byte xorKey = (byte) (tickNo % 256);
        for (int i = 0; i < length; i++) {
            if (start + i >= df.getLength()) {
                throw (new FileEnd());
            }
            df.setPosition(start + i);
            xorArray[i] = (byte) (df.getByte() ^ xorKey);
        }
    }
    
    public long getStart() {
        return start;
    }

    public ByteBuffer getBB() {
        return BB;
    }
    
    public byte[] getFlags() {
        return flags;
    }

    public void printBB(PrintStream printStream) {
        printStream.print("Rec" + segType + ":");
        printStream.print("FilePos = " + start + " ");
        if (tickNo >= 0)
            printStream.print("TickNo = " + tickNo);
        printStream.println("");
        for (int i = 0; i < length; i++) {
            printStream.print(i + ":"
                    + String.format("%02X", (0xff & BB.get(i))) + ":"
                    + String.format("%C", (0xff & BB.get(i))) + ":"
                    + (0xff & BB.get(i)));
            if (i < length - 1) {
                printStream.print(":Shrt " + BB.getShort(i) + " :UShrt "
                        + getUnsignedShort(BB, i));
            }
            if (i < length - 3) {
                printStream.print(" :I " + BB.getInt(i) + " :UI "
                        + getUnsignedInt(BB, i) + " :F " + BB.getFloat(i));
            }
            if (i < length - 7) {
                printStream.print(" :L " + BB.getLong(i) + " :D "
                        + BB.getDouble(i));
            }
            printStream.println("");
        }
    }

    private static long getUnsignedInt(ByteBuffer BB, int index) {
        return (long) (0xff & BB.get(index))
                + (256 * (long) (0xff & BB.get(index + 1)))
                + (65536 * (long) (0xff & BB.get(index + 2)))
                + (65536 * 256 * (long) (0xff & BB.get(index + 3)));
    }

    public static int getUnsignedShort(ByteBuffer BB, int index) {
        return (int) (0xff & BB.get(index) + 256
                * (int) (0xff & BB.get(index + 1)));
    }

    protected short getShort(ByteBuffer BB, int index) {
        return BB.getShort((int) index);
    }

    public long getTickNo() {
        return tickNo;
    }

    public void lookforQuat() {
        if (segType == 207 || segType == 44 || segType == 225 || segType == 187
                || segType == 127)
            return;
        for (int i = 0; i < length - 16; i++) {
            float x1 = Math.abs(BB.getFloat(i));
            if (x1 < 2.0 && x1 > 0.2) {
                float x2 = Math.abs(BB.getFloat(i + 4));
                if (x2 < 2.0 && x2 > 1.0E-4) {
                    float x3 = Math.abs(BB.getFloat(i + 8));
                    if (x3 < 2.0 && x3 > 1.0E-4) {
                        float x4 = Math.abs(BB.getFloat(i + 12));
                        if (x4 < 2.0 && x4 > 1.0E-4) {
                            System.out.print("Seg" + segType + ":");
                            System.out.print("FilePos = " + start + " ");
                            if (tickNo >= 0)
                                System.out.print("TickNo = " + tickNo);
                            System.out.println(":" + i + ":" + x1 + ":" + x2
                                    + ":" + x3 + ":" + x4);
                        }
                    }
                }
            }
        }
    }

    static Date gpsEpoch = new Date("06 January 1980 00:00:00 GMT");

    static long gE = gpsEpoch.getTime() / 1000;

    static Date date1 = new Date("11 September 2015 17:44:14 GMT");

    static long d1 = (date1.getTime() / 1000) - gE;

    static Date date2 = new Date("11 November 2015 17:44:14 GMT");

    static long d2 = (date2.getTime() / 1000) - gE;;

    static long xxx = (long) (0xff & 0x02) + (256 * (long) (0xff & 0x00))
            + (65536 * (long) (0xff & 0x01)) + (65536 * 256 * (long) (0x05));

    public void lookforDate() {
        //        if (segType == 207 || segType == 44 || segType == 225 || segType == 187
        //                || segType == 127)
        //            return;
        for (int i = 0; i < length - 4; i++) {
            long x = getUnsignedInt(BB, i);
            if (x == xxx) {
                System.out.print("Seg" + segType + ":");
                System.out.print("FilePos = " + start + " ");
                if (tickNo >= 0)
                    System.out.print("TickNo = " + tickNo);
                System.out.println();
            }
        }
    }

    public void lookForString() {
        int runLength = 0;
        for (int i = 0; i < length; i++) {
            byte b = BB.get(i);
            if (0x20 <= b && b <= 0x7E) {
                runLength++;

            } else {
                if (runLength > 3) {
                    System.out.print("Seg" + segType + ":");
                    System.out.print("FilePos = " + start + " ");
                    if (tickNo >= 0)
                        System.out.print("TickNo = " + tickNo + " ");
                    for (int j = i - runLength; j < i; j++) {
                        System.out
                                .print(String.format("%C", (0xff & BB.get(j))));
                    }
                    System.out.println();
                }
                runLength = 0;
            }
        }
    }

    public void printBB() {
        printBB(System.out);
    }

    public void lookforBat() {
        for (int i = 0; i < length - 2; i++) {
            int x1 = BB.getShort(i);
            if (segType != 103 && segType != 207 && (x1 == -13910 || x1 ==-11984 )) {
                System.out.print("Rec" + segType + ":");
                System.out.print(" I " + i+ ":volts "+x1 + " ");
                System.out.print("FilePos = " + start + " ");
                if (tickNo >= 0)
                    System.out.print("TickNo = " + tickNo);
                System.out.println("");
            }
        }
    }
}
