package DatRecords;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

public abstract class RecordType {

    ByteBuffer payloadBB = null;

    protected enum cvsTermType {
        FLOAT4, DOUBLE, BYTE, SHORT
    };

    public abstract int getID();
    private int maxNumCsvTerms = 100;

    protected int numCsvTerms = 0;

    protected String csvLabels[] = new String[maxNumCsvTerms];

    protected cvsTermType csvTermTypes[] = new cvsTermType[maxNumCsvTerms];

    protected int csvTermOffses[] = new int[maxNumCsvTerms];

    protected String csvBuffers[] = makeCsvBuffers();//new String[maxNumCsvTerms];

    static DecimalFormat df = new DecimalFormat("000.#############");

    protected void addCSVTerm(String label, cvsTermType _type, int offset) {
        csvLabels[numCsvTerms] = label;
        csvTermTypes[numCsvTerms] = _type;
        csvTermOffses[numCsvTerms] = offset;
        numCsvTerms++;
    }

    //public abstract void read(Payload _xorBB);
    public void process(Payload _record) {
        payloadBB = _record.getBB();
        for (int i = 0; i < numCsvTerms; i++) {
            switch (csvTermTypes[i]) {
            case FLOAT4:
                csvBuffers[i] = df.format(payloadBB.getFloat(csvTermOffses[i]));
                break;
            case DOUBLE:
                csvBuffers[i] = df.format(payloadBB.getDouble(csvTermOffses[i]));
                break;
            case BYTE:
                csvBuffers[i] = df.format(payloadBB.get(csvTermOffses[i]));
                break;
            case SHORT:
                csvBuffers[i] = df.format(payloadBB.getShort(csvTermOffses[i]));
                break;
            }
        }
    }

    protected String[] makeCsvBuffers() {
        String retv[] = new String[maxNumCsvTerms];
        for (int i = 0; i < maxNumCsvTerms; i++) {
            retv[i] = "";
        }
        return retv;
    }

    public void printCsvHeader(PrintStream _csv) {
        String header = csvHeader();
        _csv.print(header);
    }

    public String csvHeader() {
        String retv = "";
        for (int i = 0; i < numCsvTerms; i++)
            retv += "," + csvLabels[i];
        return retv;
    }

    public void printCsvLine(PrintStream _csv) {
        String line = csvLine();
        _csv.print(line);
        //        for(int i = 0; i < maxNumCsvTerms;i++){
        //            csvBuffers[i] = "";
        //        }
    }

    public String csvLine() {
        String retv = "";
        for (int i = 0; i < numCsvTerms; i++) {
            retv += "," + csvBuffers[i];
        }
        return retv;
    }
}
