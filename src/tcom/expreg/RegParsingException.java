package tcom.expreg;

/**
 *
 * @author 2110242
 */
class RegParsingException extends Exception {

    public RegParsingException() {
        super();
    }
    
    public RegParsingException(String message) {
        super(message);
    }
    
    public RegParsingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RegParsingException(Throwable cause) {
        super(cause);
    }
}
