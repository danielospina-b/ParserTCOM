//PROGRAMADOR: DANIEL OSPINA - 2110242

package tcom.expreg;

import java.util.ArrayList;
import org.antlr.runtime.Token;
import tcom.ui.AstI;

/**
 * Clase AstTree que representa un árbol de una expresión regular implementando AstI
 * @author Daniel Ospina - 2110242
 */
public class AstTree implements AstI {
    
    private ArrayList<AstI> childs = new ArrayList<>();
    private Token token;
    
    /**
     * Constructor de AstI que recibe un Token
     * @param token token asignado como nodo del arbol
     */
    public AstTree(Token token) {
        this.token = token;
    }
    
    /**
     * Retorna el numero de hijos de este nodo
     * @return numero de hijos
     */
    @Override
    public int getChildCount() {
        return childs.size();
    }
    
    /**
     * Retorna un nodo el cual es el i-esimo hijo de este nodo
     * @param i hijo a seleccionar
     * @return nodo que representa un hijo de este nodo
     */
    @Override
    public AstI getChild(int i) {
        return childs.get(i);
    }
    
    /**
     * Agrega un hijo al final de la lista de hijos
     * @param tree Nodo a agregar
     */
    public void addChild(AstTree tree) {
        childs.add(tree);
    }
    
    /**
     * Obtiene el nombre de este nodo
     * @return nombre del token de este nodo
     */
    @Override
    public String getName() {
        return token.getText();
    }
    
    /**
     * Remueve un hijo especifico de este nodo
     * @param child hijo a remover
     */
    public void removeChild(AstTree child) {
        childs.remove(child);
    }
    
    /**
     * Inserta un hijo en un posición específica
     * @param child hijo a insertar
     * @param position posicion
     */
    public void insertChild(AstTree child, int position) {
        childs.add(position, child);
    }
}
