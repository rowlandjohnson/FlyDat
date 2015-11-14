package DatRecords;

import java.io.PrintStream;

import DatRecords.RecordType.cvsTermType;

public class Record68 extends RecordType {

    public int getID() {
        return 68;
    }

    public Record68() {
        addCSVTerm("REC68:I0", cvsTermType.SHORT, 0);
        addCSVTerm("REC68:I1", cvsTermType.SHORT, 2);
        addCSVTerm("REC68:I2", cvsTermType.SHORT, 4);
        addCSVTerm("REC68:I3", cvsTermType.SHORT, 6);
    }

    float current = (float) 0.0;

    float volt1 = (float) 0.0;

    float volt2 = (float) 0.0;

    float volt3 = (float) 0.0;

    float volt4 = (float) 0.0;

    public void process(Payload _payload) {
        super.process(_payload);
        payloadBB = _payload.getBB();
        byte[] flags = _payload.getFlags();
        if (flags[2] != 0x00) {
            //_payload.lookForString();
            //_payload.printBB();
        } else {
            current = -(float) (((float) (payloadBB.getShort(8))) / 1000.0);
            volt1 = (float) (((float) (payloadBB.getShort(18))) / 1000.0);
            volt2 = (float) (((float) (payloadBB.getShort(20))) / 1000.0);
            volt3 = (float) (((float) (payloadBB.getShort(22))) / 1000.0);
            volt4 = (float) (((float) (payloadBB.getShort(24))) / 1000.0);
            //            System.out.println("Rec68 Volts " + volt1 + " " + volt2 + " "
            //                    + volt3 + " " + volt4 + " " + current);
            //            _payload.printBB();
        }
    }

    public void printCsvHeader(PrintStream _csv) {
        _csv.print(",Current" + ",Volt1" + ",Volt2" + ",Volt3" + ",Volt4");
        super.printCsvHeader(_csv);
    }

    public void printCsvLine(PrintStream _csv) {
        _csv.print("," + current + "," + volt1 + "," + volt2 + "," + volt3
                + "," + volt4);
        super.printCsvLine(_csv);
    }
}
