package DatRecords;

import java.io.PrintStream;

import DatRecords.RecordType.cvsTermType;

public class Record225 extends RecordType {

    public Record225() {
        addCSVTerm("REC225:X1", cvsTermType.FLOAT4, 36);
        addCSVTerm("REC225:X2", cvsTermType.FLOAT4, 40);
        addCSVTerm("REC225:X3", cvsTermType.FLOAT4, 44);
        addCSVTerm("REC225:Y1", cvsTermType.FLOAT4, 56);
        addCSVTerm("REC225:Y2", cvsTermType.FLOAT4, 60);
        addCSVTerm("REC225:Y3", cvsTermType.FLOAT4, 64);
        addCSVTerm("REC225:Y4", cvsTermType.FLOAT4, 68);

    }

    public void process(Payload _payload) {
        super.process(_payload);
    }

    public int getID() {
        return 225;
    }
}

// FLY305 the Y's start having values at tickno 5889 
//when the magnetometer shows variations in rectype 207
// 
