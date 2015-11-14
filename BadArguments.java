
public class BadArguments extends Exception {

    String cause = "";
    public BadArguments(String _cause) {
        cause = _cause;
    }
    
    public String getMsg(){
        return cause;
    }

}
