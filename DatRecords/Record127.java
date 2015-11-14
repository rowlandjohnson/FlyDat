package DatRecords;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

public class Record127 extends RecordType {

    int ID = 127;

    @Override
    public int getID() {
        return 127;
    }

    public Record127() {
        addCSVTerm("REC127:X1", cvsTermType.FLOAT4, 0);
        addCSVTerm("REC127:X2", cvsTermType.FLOAT4, 4);
        addCSVTerm("REC127:X3", cvsTermType.FLOAT4, 8);
        addCSVTerm("REC127:X4", cvsTermType.FLOAT4, 12);
        addCSVTerm("REC127:X5", cvsTermType.FLOAT4, 16);
        addCSVTerm("REC127:X6", cvsTermType.FLOAT4, 20);
        addCSVTerm("REC127:X7", cvsTermType.FLOAT4, 24);
        addCSVTerm("REC127:X8", cvsTermType.FLOAT4, 28);
        addCSVTerm("REC127:X9", cvsTermType.FLOAT4, 32);
        addCSVTerm("REC127:X10", cvsTermType.FLOAT4, 36);
        addCSVTerm("REC127:X11", cvsTermType.FLOAT4, 40);
        addCSVTerm("REC127:X12", cvsTermType.FLOAT4, 44);
    }

    public void process(Payload _payload) {
        super.process(_payload);
    }
}
