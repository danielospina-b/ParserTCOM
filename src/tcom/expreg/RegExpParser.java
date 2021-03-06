// PROGRAMADOR: DANIEL OSPINA - 2110242

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
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import tcom.ui.AstI;
import tcom.ui.ParserTreeI;
import tcom.ui.RegLexer;
import tcom.ui.VisorException;
import tcom.ui.VisorExpr;

/**
 * Clase RegExpParser especificada para el proyecto que implementa ParserTreeI
 * 
 * BNF utilizada: 
 * 
 *      symbol = 'a'..'z' | '0'..'1';
 *      
 *      palabra = symbol {symbol};
 *      
 *      expminima = '\' | '(' exp ')' [ '*' ] | palabra [ '*' ];
 * 
 *      exp = expminima {'+' expminima | expminima};
 * 
 * 
 * 
 * @author Daniel Ospina - 2110242
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
                throw new VisorException("RegParsingException\n" + e.getMessage());
            } catch (Exception e) {
                //Otros errores
                throw new VisorException("Exception... " + e.toString());
        }
        return tree;
    }
    
    /**
     * Método parse encargado de hacer el primer nextToken, invocar el primer método del BNF y comprobar que el EOF
     * @return Árbol de Expresión Regular
     * @throws RegParsingException Error de parsing con los detalles de columna, carácteres esperados y encontrados
     */
    public AstI parse() throws RegParsingException{
        token = lexer.nextToken();
        AstTree tree = pExp();
        if (token.getType() != EOF) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba carácter final de línea pero se encontró el carácter: " + token.getText());
        return tree;
    }
    
    /**
     * BNF: exp ::= expminima {'+' expminima | expminima};
     * @return Árbol de Expresión Regular
     * @throws RegParsingException Error de parsing con los detalles de columna, carácteres esperados y encontrados
     */
    public AstTree pExp() throws RegParsingException {
        AstTree first = pExpMinima();
        AstTree res = null;
        AstTree brothers = new AstTree(new CommonToken(2, "."));
        brothers.addChild(first);
        while (token.getType() == LPAR || token.getType() == LAMBDA || token.getType() == OR || token.getType() == SYMBOL) {
            if (token.getType() == OR) {
                if (res == null) res = new AstTree(token);
                token = lexer.nextToken();
                AstTree value = pExpMinima();
                res.addChild(value);
            }
            if (token.getType() == LPAR || token.getType() == LAMBDA || token.getType() == SYMBOL) {
                brothers.addChild(pExpMinima());
            }
        }
        if (res == null) {
            if (brothers.getChildCount() > 1) {
                return brothers;
            }
            return first;
        }
        else {
            res.insertChild(first, 0);
            return res;
        }
    }
    
    /**
     * BNF: expminima ::= '\' | '(' exp ')' [ '*' ] | palabra [ '*' ];
     * @return Árbol de Expresión Regular
     * @throws RegParsingException Error de parsing con los detalles de columna, carácteres esperados y encontrados
     */
    public AstTree pExpMinima() throws RegParsingException {
        if (token.getType() == LAMBDA) {
            AstTree res = new AstTree(token);
            token = lexer.nextToken();
            return res;
        }
        else if (token.getType() == LPAR) {
            token = lexer.nextToken();
            AstTree tree = pExp();
            if (token.getType() != RPAR) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba un paréntesis derecho (RPAR) pero se encontró el carácter: " + token.getText());
            token = lexer.nextToken();
            AstTree parent;
            if (token.getType() == STAR) {
                parent = new AstTree(token);
                parent.addChild(tree);
                token = lexer.nextToken();
            }
            else {
                parent = tree;
            }
            return parent;
        }
        else if (token.getType() == SYMBOL){
            AstTree tree = pPalabra();
            AstTree last = null;
            AstTree star;
            if (token.getType() == STAR) {
                if (tree.getChildCount() > 1) {
                    last = (AstTree) tree.getChild(tree.getChildCount() - 1);
                    tree.removeChild(last);
                    star = new AstTree(token);
                    star.addChild(last);
                    tree.addChild(star);
                }
                else {
                    star = new AstTree(token);
                    star.addChild(tree);
                    tree = star;
                }
                token = lexer.nextToken();
            }
            return tree;
        }
        else {
            throw new RegParsingException("ERROR: Columna: " + token.getCharPositionInLine() + ", Carácter inesperado, se esperaba LAMBDA o \n"
                    + "PARENTESIS IZQUIERDO (LPAR) o Un carácter alfanumérico (SYMBOL) pero se encontró el carácter: " + token.getText());
        }
    }
    
    /**
     * BNF: palabra ::= symbol {symbol};
     * @return Árbol de Expresión Regular
     * @throws RegParsingException Error de parsing con los detalles de columna, carácteres esperados y encontrados
     */
    public AstTree pPalabra() throws RegParsingException {
        AstTree tree = new AstTree(new CommonToken(2, "."));
        boolean isSymbol = true;
        if (token.getType() != SYMBOL) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba un símbolo alfanumérico (SYMBOL) pero se encontró el carácter: " + token.getText());
        while (isSymbol)  {
            tree.addChild(pSymbol());
            token = lexer.nextToken();
            if (token.getType() != SYMBOL) isSymbol = false;
        }
        if (tree.getChildCount() == 1) {
            tree = (AstTree) tree.getChild(0);
        }
        return tree;
    }
    
    /**
     * BNF: symbol ::= 'a'..'z' | '0'..'1';
     * @return Árbol de Expresión Regular
     * @throws RegParsingException Error de parsing con los detalles de columna, carácteres esperados y encontrados
     */
    public AstTree pSymbol() throws RegParsingException {
        return new AstTree(token);
    }       
    
}
