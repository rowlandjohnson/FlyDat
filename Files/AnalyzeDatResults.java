package Files;

import Files.AnalyzeDatResults.ResultCode;

public class AnalyzeDatResults {
    public enum ResultCode {NO_ERRORS, SOME_ERRORS, CORRUPTED, NOT_DAT}
    
    private ResultCode resultCode = ResultCode.NO_ERRORS;
    
    private String messages = "";
    
    public void setResultCode(ResultCode _resultCode){
        resultCode = _resultCode;
    }
           
    public void addMessage (String msg){
        messages += msg;
    }
    
    public void resetMessages() {
        messages = "";
    }
    
    public ResultCode getResultCode() {
        return resultCode;
    }
    
    public String getMessages(){
        return messages;
    }
    
    public String toString() {
        String retv = "";
        switch(resultCode) {
        case NO_ERRORS:
            retv += "NO_ERRORS";
            break;
        
        case SOME_ERRORS:
            retv += "SOME_ERRORS";
            break;
        case CORRUPTED:
            retv += "CORRUPTED";
            break;
        case NOT_DAT:
            retv += "NOT_DAT";
            break;
        }
        retv += "\n" +messages;
        return retv;
    }

}
