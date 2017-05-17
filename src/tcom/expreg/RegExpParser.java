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
                throw new VisorException("RegParsingException\n" + e.getMessage());
            } catch (Exception e) {
                //Otros errores
                throw new VisorException("Exception... " + e.toString());
        }
        return tree;
    }

    public AstI parse() throws RegParsingException{
        token = lexer.nextToken();
        AstTree tree = pExp();
        //if (token.getType() != EOF) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba carácter de final de línea");
        System.out.println("TREE " + tree.getName() + " " + tree.getChild(0).getName());
        return tree;
    }

    public AstTree pExp() throws RegParsingException {
        AstTree first = pExpMinima();
        AstTree res = null;
        AstTree brothers = new AstTree(new CommonToken(2, "."));
        brothers.addChild(first);
        while (token.getType() == LPAR || token.getType() == LAMBDA || token.getType() == OR) {
            if (token.getType() == OR) {
                if (res == null) res = new AstTree(token);
                token = lexer.nextToken();
                System.out.println("Res.addchild(pexpminima) con token actual = " + token.getText());
                AstTree value = pExpMinima();
                System.out.println("name de value: " + value.getChild(0).getName());
                res.addChild(value);
            }
            if (token.getType() == LPAR) {
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
    
    public AstTree pExpMinima() throws RegParsingException {
        System.out.println("tcom.expreg.RegExpParser.pExpMinima()");
        if (token.getType() == LAMBDA) {
            token = lexer.nextToken();
            return new AstTree(token);
        }
        else if (token.getType() == LPAR) {
            System.out.println("Call por lpar en expminima");
            token = lexer.nextToken();
            AstTree tree = pExp();
            if (token.getType() != RPAR) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba un paréntesis derecho (RPAR)");
            System.out.println("Parentesis Derecho Salio ");
            token = lexer.nextToken();
            return tree;
        }
        else if (token.getType() == SYMBOL){
            AstTree parent;
            AstTree tree = pPalabra();
            if (token.getType() == STAR) {
                parent = new AstTree(token);
                parent.addChild(tree);
            }
            else {
                parent = tree;
            }
            return parent;
        }
        else {
            System.out.println("Token: " + token.getText());
            throw new RegParsingException("Error que no tengo una puta idea");
        }
    }
    
    public AstTree pPalabra() throws RegParsingException {
        System.out.println("tcom.expreg.RegExpParser.pPalabra()");
        AstTree tree = new AstTree(new CommonToken(2, "."));
        boolean isSymbol = true;
        System.out.println("Token: " + token.getText() + " Column: " + token.getCharPositionInLine());
        if (token.getType() != SYMBOL) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba un símbolo alfanumérico (SYMBOL)");
        while (isSymbol)  {
            tree.addChild(pSymbol());
            token = lexer.nextToken();
            if (token.getType() != SYMBOL) isSymbol = false;
        }
        
        return tree;
    }
    
    public AstTree pSymbol() throws RegParsingException {
        System.out.println("tcom.expreg.RegExpParser.pSymbol()");
        return new AstTree(token);
    }       
    
}
