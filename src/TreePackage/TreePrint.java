
package TreePackage;

import org.w3c.dom.*;
import java.io.*;
//-----------------------------------------------------------------------------------
// TreePrint
//-----------------------------------------------------------------------------------
public class TreePrint{

    public static String getNodeType(Node node){
        switch(node.getNodeType()) {
        case Node.CDATA_SECTION_NODE:
            return "CDATA_SECTION_NODE";
        case Node.COMMENT_NODE:
            return "COMMENT_NODE";
        case Node.DOCUMENT_FRAGMENT_NODE:
            return "DOCUMENT_FRAGMENT_NODE";
        case Node.DOCUMENT_NODE:
            return "DOCUMENT_NODE";
        case Node.DOCUMENT_TYPE_NODE:
            return "DOCUMENT_TYPE_NODE";
        case Node.ELEMENT_NODE:
            return "ELEMENT_NODE";
        case Node.ENTITY_NODE:
            return "ENTITY_NODE";
        case Node.ENTITY_REFERENCE_NODE:
            return "ENTITY_REFERENCE_NODE";
        case Node.NOTATION_NODE:
            return "NOTATION_NODE";
        case Node.PROCESSING_INSTRUCTION_NODE:
            return "PROCESSING_INSTRUCTION_NODE";
        case Node.TEXT_NODE:
            return "TEXT_NODE";
        case Node.ATTRIBUTE_NODE:
            return "ATTRIBUTE_NODE";
        default:
            return "Others";
        }
    }

    public static String print(Node node){
        TreeTraversal nmove=new TreeTraversal(node, TreeTraversal.PREORDER);
        Node cnode;
        int k=1;
        String res="";
        
        while((cnode=nmove.Next())!=null){
            res+=k + ".\t" + nmove.GetPath()+"     (Level="+nmove.GetLevel()+" Type="+getNodeType(cnode)+"): ";
            k++;
            if(cnode.getNodeType()==Node.ELEMENT_NODE){

                NamedNodeMap nodeAttrs=cnode.getAttributes();
                for(int i=0;i<nodeAttrs.getLength();i++){
                    Node attr = nodeAttrs.item(i);
                    res+=attr.getNodeName()+"="+ "\"" + attr.getNodeValue()+ "\"  ";
                }
                res+="\r\n";
            }
            else{
                res+=cnode.getNodeValue()+"\r\n";
            }
        }
        
        return res;
    }

    public static void print(Node node, PrintStream stream){
        String str=print(node);
        stream.print(str);
    }

    
    public static void print(Node node, String fileAddr){
        try{
            FileOutputStream fileStream = new FileOutputStream(fileAddr);
            PrintStream file = new PrintStream(fileStream);
            
            print(node, file);

            file.close();
            fileStream.close();
        }
        catch(Exception e){
            System.err.println(e.toString());
        }
    }
    
    
}
