package DatRecords;


public class Record163 extends RecordType {

 // 50Hz
    public Record163() {
        addCSVTerm("REC163:X1", cvsTermType.FLOAT4, 11);
        addCSVTerm("REC163:X2", cvsTermType.FLOAT4, 15);
        addCSVTerm("REC163:X3", cvsTermType.FLOAT4, 19);
        addCSVTerm("REC163:X4", cvsTermType.FLOAT4, 23);
        addCSVTerm("REC163:X5", cvsTermType.FLOAT4, 27);
        addCSVTerm("REC163:X6", cvsTermType.FLOAT4, 31);
        addCSVTerm("REC163:X7", cvsTermType.FLOAT4, 35);
    }

    public void process(Payload _payload) {
        super.process(_payload);
    }
    public int getID() {
        return 163;
    }
}
