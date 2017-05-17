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
        System.out.println("POST tree = pExp() en parse");
        if (token.getType() != EOF) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba carácter de final de línea pero se encontró el carácter: " + token.getText());
        System.out.println("TREE FINISHED");
        return tree;
    }

    public AstTree pExp() throws RegParsingException {
        System.out.println("tcom.expreg.RegExpParser.pExp()");
        AstTree first = pExpMinima();
        System.out.println("FIRST == NULL? : " + (first == null) + " first.getName(): " + first.getName());
        AstTree res = null;
        AstTree brothers = new AstTree(new CommonToken(2, "."));
        brothers.addChild(first);
        System.out.println("PostAddChild");
        while (token.getType() == LPAR || token.getType() == LAMBDA || token.getType() == OR || token.getType() == SYMBOL) {
            if (token.getType() == OR) {
                if (res == null) res = new AstTree(token);
                token = lexer.nextToken();
                System.out.println("Res.addchild(pexpminima) con token actual = " + token.getText());
                AstTree value = pExpMinima();
                System.out.println("name de value: " + value.getName());
                res.addChild(value);
            }
            if (token.getType() == LPAR || token.getType() == LAMBDA || token.getType() == SYMBOL) {
                brothers.addChild(pExpMinima());
            }
        }
        if (res == null) {
            System.out.println("entro por res == NULL");
            if (brothers.getChildCount() > 1) {
                System.out.println("Entro por childcount");
                return brothers;
            }
            System.out.println("Return first");
            return first;
        }
        else {
            System.out.println("entro por res != null");
            res.insertChild(first, 0);
            return res;
        }
    }
    
    public AstTree pExpMinima() throws RegParsingException {
        System.out.println("tcom.expreg.RegExpParser.pExpMinima()");
        if (token.getType() == LAMBDA) {
            AstTree res = new AstTree(token);
            System.out.println("PRENEXTTOKEN");
            token = lexer.nextToken();
            System.out.println("POSNEXTTOKEN");
            return res;
        }
        else if (token.getType() == LPAR) {
            System.out.println("Call por lpar en expminima");
            token = lexer.nextToken();
            AstTree tree = pExp();
            if (token.getType() != RPAR) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba un paréntesis derecho (RPAR) pero se encontró el carácter: " + token.getText());
            System.out.println("Parentesis Derecho Salio ");
            token = lexer.nextToken();
            AstTree parent;
            if (token.getType() == STAR) {
                System.out.println("Estrellitaaa donde estaaas!!!!!!!!!!");
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
            AstTree parent;
            AstTree tree = pPalabra();
            if (token.getType() == STAR) {
                System.out.println("Estrella!!!!!!!!!!");
                parent = new AstTree(token);
                parent.addChild(tree);
                token = lexer.nextToken();
            }
            else {
                parent = tree;
            }
            return parent;
        }
        else {
            System.out.println("Token: " + token.getText());
            throw new RegParsingException("ERROR: Columna: " + token.getCharPositionInLine() + ", Carácter inesperado, se esperaba LAMBDA o \n"
                    + "PARENTESIS IZQUIERDO (LPAR) o Un carácter alfanumérico (SYMBOL) pero se encontró el carácter: " + token.getText());
        }
    }
    
    public AstTree pPalabra() throws RegParsingException {
        System.out.println("tcom.expreg.RegExpParser.pPalabra()");
        AstTree tree = new AstTree(new CommonToken(2, "."));
        boolean isSymbol = true;
        System.out.println("Token: " + token.getText() + " Column: " + token.getCharPositionInLine());
        if (token.getType() != SYMBOL) throw new RegParsingException("ERROR: Columna " + token.getCharPositionInLine() + ", Se esperaba un símbolo alfanumérico (SYMBOL) pero se encontró el carácter: " + token.getText());
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
