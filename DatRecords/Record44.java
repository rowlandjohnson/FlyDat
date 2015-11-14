package DatRecords;

import DatRecords.RecordType.cvsTermType;

public class Record44 extends RecordType {


    public Record44() {
        addCSVTerm("REC44:quatW", cvsTermType.FLOAT4, 78);
        addCSVTerm("REC44:quatX", cvsTermType.FLOAT4, 82);
        addCSVTerm("REC44:quatY", cvsTermType.FLOAT4, 86);
        addCSVTerm("REC44:quatZ", cvsTermType.FLOAT4, 90);
        addCSVTerm("REC44:roll", cvsTermType.FLOAT4, 98); // note: out of sequence
        addCSVTerm("REC44:pitch", cvsTermType.FLOAT4, 94); //
        addCSVTerm("REC44:yaw", cvsTermType.FLOAT4, 102);
    }

    public void process(Payload _payload) {
        super.process(_payload);
        // Following is left in to be un-commented at some point
        /*
        super.read(_payload);
        payload = _payload.getBB();
        // following seem to quaterion bird orientation 
        float quatW = payload.getFloat(78);
        float quatX = payload.getFloat(82);
        float quatY = payload.getFloat(86);
        float quatZ = payload.getFloat(90);
        // following seem to bird orientation (in radians), roll and pitch are opposite sign
        float yaw = payload.getFloat(94);
        float roll = payload.getFloat(98);
        float pitch = payload.getFloat(102);
        Quaternion q = new Quaternion(quatW, quatX, quatY, quatZ);
        double[] eu = q.toEuler();
        */
    }

    @Override
    public int getID() {
        return 44;
    }
}
