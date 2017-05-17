package tcom.expreg;

import java.util.ArrayList;
import org.antlr.runtime.Token;
import tcom.ui.AstI;

/**
 *
 * @author 2110242
 */
public class AstTree implements AstI {
    
    private ArrayList<AstI> childs = new ArrayList<>();
    private Token token;

    public AstTree(Token token) {
        this.token = token;
    }
    
    @Override
    public int getChildCount() {
        return childs.size();
    }

    @Override
    public AstI getChild(int i) {
        return childs.get(i);
    }
    
    public void addChild(AstTree tree) {
        childs.add(tree);
    }

    @Override
    public String getName() {
        return token.getText();
    }

    public void removeChild(AstTree child) {
        childs.remove(child);
    }
    
    public void insertChild(AstTree child, int position) {
        childs.add(position, child);
    }
}
