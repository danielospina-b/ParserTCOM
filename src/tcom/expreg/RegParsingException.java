//PROGRAMADOR: DANIEL OSPINA - 2110242

package tcom.expreg;

/**
 * Clase RegParsingException que es lanzada en casos de Error de Parsing
 * @author Daniel Ospina - 2110242
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
