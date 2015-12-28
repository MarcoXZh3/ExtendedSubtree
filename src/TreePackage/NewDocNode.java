
package TreePackage;


import org.w3c.dom.*;
import javax.xml.parsers.*;//for tree building

//-----------------------------------------------------------------------------------
// New doc and Node -- used in tree generators
//-----------------------------------------------------------------------------------
public class NewDocNode{
    Document doc=null;
    public NewDocNode(){
        
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.newDocument();       
        } 
        catch (ParserConfigurationException e) {
        }
    }
    
    
    public Document getDoc(){
        return doc;
    }
    
    @Deprecated 
    public Document NewDoc(){
        return doc;
    }        
    
    @Deprecated
    public Node NewNodeElement(String name){
        return doc.createElement(name);
    }
    public Node createElement(String name){
        return doc.createElement(name);
    }
}
