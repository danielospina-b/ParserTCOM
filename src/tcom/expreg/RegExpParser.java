package tcom.expreg;

import static tcom.ui.RegLexer.LAMBDA;
import static tcom.ui.RegLexer.LPAR;
import static tcom.ui.RegLexer.OR;
import static tcom.ui.RegLexer.RPAR;
import static tcom.ui.RegLexer.STAR;
import static tcom.ui.RegLexer.SYMBOL;
import static tcom.ui.RegLexer.EOF;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.Token;
import tcom.ui.AstI;
import tcom.ui.ParserTreeI;
import tcom.ui.RegLexer;
import tcom.ui.VisorException;
import tcom.ui.VisorExpr;

/**
 *
 * @author 2110242
 */
public class RegExpParser implements ParserTreeI {
    
    RegLexer lexer;
    Token token;
    
    public static void main(String[] args) {
        VisorExpr visor = new VisorExpr();
        visor.setParserTree(new RegExpParser());
        visor.setVisible(true);
    }

    @Override
    public AstI analizar(String srcexpr) throws VisorException {
        AstI tree = null;
        try {
            //Instanciar el analizador lexico y conectarlo con una cadena de caracteres.
            ANTLRInputStream in = new ANTLRInputStream(new ByteArrayInputStream(srcexpr.getBytes()));
            lexer = new RegLexer(in);
            //Iniciar el proceso de parsing
            tree = parse();
            }catch(IllegalArgumentException e){
                // Caracter invalido en la entrada
                throw new VisorException("IllegalArgumentException");
            } catch (UnsupportedEncodingException e) {
                //Problemas con la codificacion de caracteres
                throw new VisorException("UnsupportedEncodingException");
            } catch (IOException e) {
                //Error de I/O inesperado
                throw new VisorException("IOException");
            } catch (RegParsingException e) {
                // Error detectado por su parser
                throw new VisorException("RegParsingException");
            } catch (Exception e) {
                //Otros errores
                throw new VisorException("Exception... " + e.toString());
        }
        return tree;
    }

    private AstI parse() throws RegParsingException{
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
