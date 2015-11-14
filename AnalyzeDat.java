import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import DatRecords.Payload;
import DatRecords.Record122;
import DatRecords.Record127;
import DatRecords.Record163;
import DatRecords.Record168;
import DatRecords.Record207;
import DatRecords.Record225;
import DatRecords.Record42;
import DatRecords.Record44;
import DatRecords.Record68;
import DatRecords.RecordType;
import Files.AnalyzeDatResults;
import Files.Corrupted;
import Files.DatFile;
import Files.FileEnd;

public class AnalyzeDat {

    DatFile _datFile = null;

    private boolean _debug = false;

    private FileEnd _fileEnd = new FileEnd();

    static RecordType availableRecTypes[] = { (new Record42()),
            (new Record44()), (new Record68()), (new Record127()),
            (new Record163()), (new Record207()), (new Record225()) };

    public AnalyzeDat(DatFile datFile) {
        _datFile = datFile;
    }

    private PrintStream _csv = null;

    PrintStream _printStream = System.out;

    long start = 0;

    int length = 0;

    long next = 0;

    long tickNo = 0;

    int numCorrupted = 0;

    long tickRangeLower = 0;

    long tickRangeUpper = Long.MAX_VALUE;

    long filePositionLower = 0;

    long filePositionUpper = Long.MAX_VALUE;

    public short _recTypeIDs[] = new short[0];

    public AnalyzeDatResults analyze() throws IOException {
        try {
            _datFile.setStartOfRecord(128);
            long fileLength = _datFile.getLength();
            RecordType recTypes[] = determineRecTypes();
            // If there is a .csv being produced go ahead and output the first row containg the 
            // column headings
            if (_csv != null) {
                _csv.print("Tick#");
                for (int i = 0; i < recTypes.length; i++) {
                    recTypes[i].printCsvHeader(_csv);
                }
                _csv.println();
            }
            // Main loop that gets a tick#Group and processes all the records in that group
            while (true) {
                if (_datFile.getPos() > fileLength - 8) {
                    throw (_fileEnd);
                }
                // Get the next tick#Group
                DatFile.tickGroup tG = _datFile.getTickGroup();
                boolean processedSomePayloads = false;
                tickNo = tG.tickNo;
                if (tickRangeLower <= tickNo && tickNo <= tickRangeUpper) {
                    for (int tgIndex = 0; tgIndex < tG.numElements; tgIndex++) {
                        int payloadType = tG.payloadType[tgIndex];
                        long payloadStart = tG.start[tgIndex];
                        int payloadLength = tG.length[tgIndex];
                        if (filePositionLower <= payloadStart
                                && payloadStart <= filePositionUpper) {
                            byte[] flags = tG.flags[tgIndex];
                            //                    Payload payload2 = new Payload(_datFile, payloadStart,
                            //                            payloadLength, payloadType, tickNo, flags);
                            //payload2.lookforBat();
                            for (int i = 0; i < recTypes.length; i++) {
                                // For each record found in this tick#Group is it something that we want to process
                                if (payloadType == recTypes[i].getID()) {
                                    Payload payload = new Payload(_datFile,
                                            payloadStart, payloadLength,
                                            payloadType, tickNo, flags);
                                    ((RecordType) recTypes[i]).process(payload);
                                    processedSomePayloads = true;
                                }
                            }
                        }
                    }
                    // if some payloads in this tick#Group were processed then output the .csv line
                    if (processedSomePayloads && (_csv != null)) {
                        _csv.print(tickNo);
                        for (int i = 0; i < recTypes.length; i++) {
                            recTypes[i].printCsvLine(_csv);
                        }
                        _csv.println();
                    }
                }
            }
        } catch (FileEnd ex) {
        } catch (Corrupted ex) {
        }
        return _datFile.getResults();
    }

    private RecordType[] determineRecTypes() {
        int length = _recTypeIDs.length;
        if (length == 0) {
            RecordType retv[] = new RecordType[2];
            retv[0] = new Record207();
            retv[1] = new Record68();
            return retv;
        }
        ArrayList<RecordType> rt = new ArrayList<RecordType>();
        for (int j = 0; j < _recTypeIDs.length; j++) {
            for (int i = 0; i < availableRecTypes.length; i++) {
                if (availableRecTypes[i].getID() == _recTypeIDs[j]) {
                    rt.add(availableRecTypes[i]);
                }
            }
        }
        RecordType retv[] = new RecordType[rt.size()];
        for (int i = 0; i < rt.size(); i++) {
            retv[i] = rt.get(i);
        }
        return retv;
    }

    public void dump(PrintStream dumpPS) throws IOException {
        long fileLength = _datFile.getLength();
        try {
            _datFile.setStartOfRecord(128);
            while (true) {
                if (_datFile.getPos() > fileLength - 8) {
                    throw (_fileEnd);
                }
                DatFile.tickGroup tG = _datFile.getTickGroup();
                boolean readSomeRecs = false;
                tickNo = tG.tickNo;
                if (tickRangeLower <= tickNo && tickNo <= tickRangeUpper) {
                    for (int tgIndex = 0; tgIndex < tG.numElements; tgIndex++) {
                        short payloadType = tG.payloadType[tgIndex];
                        long payloadStart = tG.start[tgIndex];
                        int payloadLength = tG.length[tgIndex];
                        if (filePositionLower <= payloadStart
                                && payloadStart <= filePositionUpper
                                && isValidRecType(payloadType)) {
                            byte[] flags = tG.flags[tgIndex];
                            Payload xorBB = new Payload(_datFile, payloadStart,
                                    payloadLength, payloadType, tickNo, flags);
                            xorBB.printBB(dumpPS);
                        }
                    }
                }
            }
        } catch (FileEnd ex) {
        } catch (Corrupted ex) {
        }

    }

    public static void main(String[] args) throws IOException {
        String fileName = "FLY276";
        PrintStream csvPS = null;
        DatFile datFile = null;
        try {
            datFile = new DatFile("/Users/rowland/phantom/FLYLOGS/" + fileName
                    + ".DAT");
        } catch (Files.NotDatFile e) {
            System.out.println(datFile.fileName()
                    + " Not a recognized structure");
            System.exit(0);
        }

        try {
            csvPS = new PrintStream("/Users/rowland/phantom/test/n" + fileName
                    + ".CSV");
        } catch (FileNotFoundException ex) {
            System.out.println("/Users/rowland/phantom/test/n" + fileName
                    + ".CSV" + " in use");
            System.exit(0);
        }
        AnalyzeDat analyzeDat = null;
        System.out.println(fileName);
        analyzeDat = new AnalyzeDat(datFile);
        analyzeDat.setCsv(csvPS);
        AnalyzeDatResults results = analyzeDat.analyze();
        System.out.println(results.toString());
        if (csvPS != null)
            csvPS.close();
    }

    public void setCsv(PrintStream csv) {
        _csv = csv;
    }

    public void setDebug(boolean b) {
        _debug = b;
    }

    public void setPrintStream(PrintStream printStream) {
        _printStream = printStream;
    }

    public void setTickNoRange(long lower, long upper) {
        tickRangeLower = lower;
        tickRangeUpper = upper;
    }

    public void setFileLimits(long lower, long upper) {
        filePositionLower = lower;
        filePositionUpper = upper;
    }

    public void setRecTypes(short[] recTypes) {
        _recTypeIDs = recTypes;
    }

    private boolean isValidRecType(short recType) {
        for (int i = 0; i < _recTypeIDs.length; i++) {
            if (recType == _recTypeIDs[i])
                return true;
        }
        return false;
    }
}
