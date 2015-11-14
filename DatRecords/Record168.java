package DatRecords;

import java.io.PrintStream;
import java.nio.ByteBuffer;

public class Record168 extends RecordType {

    ByteBuffer payload = null;
    
    String message = "";

    @Override
    public int getID() {
        // TODO Auto-generated method stub
        return 168;
    }

    @Override
    public void process(Payload _payload) {
        super.process(_payload);
        payload = _payload.getBB();
        char characters[] = new char[29];
        for (int i = 0; i < 29; i++) {
            characters[i] = (char) payload.get(i);
        }
        message = new String(characters);
        //System.out.println("168 Message "+message);
    }

}
