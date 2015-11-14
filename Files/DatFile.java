package Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

public class DatFile {

    MappedByteBuffer memory = null;

    long memSize = 8 * 1000000;

    private long filePos = 0;

    File file = null;

    FileInputStream inputStream = null;

    FileChannel _channel = null;

    long fileLength = 0;

    private DecimalFormat df = new DecimalFormat("000.#############");

    long nextFilPos = 128; //beginning filePos of next tick group

    long nextTickGroup = 0; //tickNo of next group

    private FileEnd _fileEnd = new FileEnd();

    int numCorrupted = 0;

    AnalyzeDatResults results = null;

    long startOfRecord = 0;

    public class tickGroup {
        public int numElements = 0;

        public long start[] = new long[30];

        public byte flags[][] = new byte[30][3];

        public int length[] = new int[30];

        public short payloadType[] = new short[30];

        public long tickNo = -1;

        public void reset() {
            numElements = 0;
        }

        public void add(long _tickNo, long _start, int _length, short _ptType,
                byte flag0, byte flag1, byte flag2) {
            numElements++;
            tickNo = _tickNo;
            start[numElements - 1] = _start;
            length[numElements - 1] = _length;
            payloadType[numElements - 1] = _ptType;
            flags[numElements - 1][0] = flag0;
            flags[numElements - 1][1] = flag1;
            flags[numElements - 1][2] = flag2;
        }

        public void add(long _start, int _length, short _ptType, byte flag0,
                byte flag1, byte flag2) {
            numElements++;
            start[numElements - 1] = _start;
            length[numElements - 1] = _length;
            payloadType[numElements - 1] = _ptType;
            flags[numElements - 1][0] = flag0;
            flags[numElements - 1][1] = flag1;
            flags[numElements - 1][2] = flag2;
        }
    }

    public tickGroup tickGroups[] = new tickGroup[2];

    private int tgIndex = 1;

    public tickGroup getTickGroup() throws FileEnd, IOException, Corrupted {

        tickGroup thisTickGroup = null;
        tickGroup nextTickGroup = null;
        if (tgIndex == 1) {
            tgIndex = 0;
            thisTickGroup = tickGroups[0];
            nextTickGroup = tickGroups[1];
        } else {
            tgIndex = 1;
            thisTickGroup = tickGroups[1];
            nextTickGroup = tickGroups[0];
        }
        long thisTickNo = thisTickGroup.tickNo;
        nextTickGroup.numElements = 0; // reset the nextTickGroup to be empty

        boolean done = false;
        int length = 0;
        long nextStartOfRecord = 0;
        while (!done) {
            try {
                setPosition(startOfRecord);
                if (getByte(startOfRecord) != 0x55) { // if not positioned at next 0x55, then its corrupted
                    throw (new Corrupted(thisTickNo, startOfRecord));
                }
                length = (0xFF & getByte(startOfRecord + 1));
                if (length == 0) {
                    throw (new Corrupted(thisTickNo, startOfRecord));
                }
                nextStartOfRecord = startOfRecord + length; // the next 0x55 , we hope

                byte flag0 = (byte) getByte(startOfRecord + 2);
                byte flag1 = (byte) getByte(startOfRecord + 4);
                byte flag2 = (byte) getByte(startOfRecord + 5);
                short segtype = (short) (0xFF & getByte(startOfRecord + 3));
                long thisRecordsickNo = getUnsignedInt(startOfRecord + 6);
                if (thisTickNo == -1) { //thisTickGroup doesn't yet have a tickNo
                    thisTickNo = thisRecordsickNo;
                    thisTickGroup.tickNo = thisRecordsickNo;
                }
                //                System.out.println("DatFile start:" + startOfRecord
                //                        + " length: " + length + " recType: " + segtype);

                if (nextStartOfRecord > fileLength)
                    throw (_fileEnd);
                if (getByte(nextStartOfRecord) != 0x55) { // if not positioned at next 0x55, then its corrupted
                    throw (new Corrupted(thisTickNo, startOfRecord));
                }
                if (thisRecordsickNo > thisTickGroup.tickNo) { //start next group
                    nextTickGroup.reset();
                    nextTickGroup.add(thisRecordsickNo, startOfRecord + 10,
                            length, segtype, flag0, flag1, flag2);
                    done = true;
                } else if (thisRecordsickNo == thisTickGroup.tickNo) {
                    thisTickGroup.add(startOfRecord + 10, length, segtype,
                            flag0, flag1, flag2);
                } else { // (tickNo < thisTickGroup.tickNo) in the last group
                    //for now, just ignore
                }
                startOfRecord = nextStartOfRecord;
            } catch (Corrupted c) {
                if (getPos() > fileLength - 600) {
                    throw (_fileEnd);
                }
                numCorrupted++;
                results.addMessage(c.toString() + "\n");
                results.setResultCode(AnalyzeDatResults.ResultCode.SOME_ERRORS);
                if (numCorrupted > 20) {
                    results.setResultCode(AnalyzeDatResults.ResultCode.CORRUPTED);
                    throw (new Corrupted(thisTickNo, startOfRecord));
                }
                setPosition(startOfRecord + 1);
                byte fiftyfive = readByte();
                while (fiftyfive != 0X55) {
                    if (getPos() > fileLength - 1000) {
                        throw (_fileEnd);
                    }
                    fiftyfive = readByte();
                }
                //set position right before the next 0x55
                startOfRecord = getPos() - 1;
            }
        }
        return thisTickGroup;
    }

    public DatFile(String fileName) throws IOException, NotDatFile {
        this(new File(fileName));
    }

    public DatFile(File _file) throws IOException, NotDatFile {
        file = _file;
        tickGroups[0] = new tickGroup();
        tickGroups[1] = new tickGroup();
        results = new AnalyzeDatResults();
        fileLength = file.length();
        inputStream = new FileInputStream(file);
        _channel = inputStream.getChannel();
        if (memSize > fileLength)
            memSize = fileLength;
        memory = _channel.map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
        memory.order(ByteOrder.LITTLE_ENDIAN);
        try {
            if (getByte(128) != 0x55) {
                close();
                throw (new NotDatFile());
            }
        } catch (FileEnd e) {
            close();
            throw (new NotDatFile());
        }
    }

    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
                if (inputStream.getChannel() != null) {
                    inputStream.getChannel().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        memory = null;
        System.gc();
        System.runFinalization();
    }

    public void skipOver(int num) throws IOException {
        filePos = filePos + num;
        if (filePos > fileLength)
            throw (new IOException());
        _channel.position(filePos);
    }

    public String bufferToString() throws FileEnd {
        return filePos + ":" + String.format("%02X", (0xff & getByte()))
                + " : " + (0xff & getByte()) + " :Shrt " + getShort()
                + " :UShrt " + getUnsignedShort() + " :I " + getInt() + " :UI "
                + getUnsignedInt() + " :L " + getLong() + " :F " + getFloat()
                + " :D " + getDouble();
    }

    public void setPosition(long pos) throws FileEnd, IOException {
        filePos = pos;
        if (filePos >= fileLength)
            throw (new FileEnd());
        _channel.position(pos);
    }

    public long getPos() {
        return filePos;
    }

    public long getLength() {
        return fileLength;
    }

    public byte getByte() {
        return memory.get((int) filePos);
    }

    public int getByte(long fp) throws FileEnd {
        if (fp >= fileLength)
            throw (new FileEnd());
        return memory.get((int) fp);
    }

    public byte readByte() throws IOException {
        byte rv = getByte();
        skipOver(1);
        return rv;
    }

    protected short getShort() {
        return memory.getShort((int) filePos);
    }


    public int getUnsignedShort() {
        return (int) (0xff & memory.get((int) filePos)) + 256
                * (int) (0xff & memory.get((int) (filePos + 1)));
    }

    public int getInt() {
        return memory.getInt((int) filePos);
    }

    public long getUnsignedInt() throws FileEnd {
        return getUnsignedInt(filePos);
    }

    private long getUnsignedInt(long fp) throws FileEnd {
        if (fp > fileLength - 4)
            throw (new FileEnd());
        return (long) (0xff & memory.get((int) fp))
                + (256 * (long) (0xff & memory.get((int) (fp + 1))))
                + (65536 * (long) (0xff & memory.get((int) (fp + 2))))
                + (65536 * 256 * (long) (0xff & memory.get((int) (fp + 3))));
    }

    public long getLong() {
        return memory.getLong((int) filePos);
    }

    public float getFloat() {
        return memory.getFloat((int) filePos);
    }

    public double getDouble() {
        return memory.getDouble((int) filePos);
    }

    public AnalyzeDatResults getResults() {
        return results;
    }

    public File getFile() {
        return file;
    }

    public void setStartOfRecord(long sor) {
        startOfRecord = sor;
    }

    public String fileName() {
        String retv = "Unknown";
        try {
            retv = file.getCanonicalPath();
        } catch (IOException e) {

        }
        return retv;
    }

}
