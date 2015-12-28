/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TreePackage.ParserPackage;

import MachineLearning.DataSet;
import MachineLearning.Record;
import TreePackage.*;
import java.io.File;
import org.w3c.dom.*;
/**
 *
 * @author Ali
 */
public class DataSetParser_ACMSIGMOD {
    
    XMLParser_Java xmlparser=new XMLParser_Java();
    CustomFilterDOMTree filter=new CustomFilterDOMTree();        
    
    public DataSet parse(String folderAddr){//D:\\Ali\\AnyCode\\Java\\DOMTree\\DataSet\\ACM_SIGMOD\\XMLSigmodRecordMar1999
        DataSet set0=parseAFolder(folderAddr+"\\OrdinaryIssuePage", "OrdinaryIssuePage"); //size 51
        DataSet set1=parseAFolder(folderAddr+"\\ProceedingsPage", "ProceedingsPage"); //size 17
        DataSet set2=parseAFolder(folderAddr+"\\IndexTermsPage", "IndexTermsPage"); //size 920
        //DataSet set2=parseAFolder(folderAddr+"\\IndexTermsPage100", 2); //size 100
        
        DataSet set=new DataSet();
        set.addAll(set0);
        set.addAll(set1);
        set.addAll(set2);
        
        return set;
    }
    private DataSet parseAFolder(String folderAddr, String label){
        
        DataSet dataset=new DataSet();
        File dir = new File(folderAddr);
        String[] files = dir.list();
        
        for(int i=0;i<files.length;i++){
            
            Node tree=this.parseAFile(folderAddr+"\\"+files[i]);
            
            Record<Node> rec=new Record(tree, label);
            dataset.add(rec);
        }
        return dataset;
    }
    private Node parseAFile(String fileAddr){
        Node tree=(Node)xmlparser.parse(fileAddr);
        filter.filter(tree);
        return tree.getFirstChild();
    }
}



//-----------------------------------------------------------------------------------
// Filter Tree
//-----------------------------------------------------------------------------------
class CustomFilterDOMTree{

    private boolean removeNode(Node node){//remove all node types exept Elements, removes all elements' attributes

        if(node.getNodeType()!=Node.ELEMENT_NODE){
            return true;
        }
   
        return false;
    }
    
    private void removeAttr(Node node){//remove all node types exept Elements, removes all elements' attributes

        if(node.getNodeType()==Node.ELEMENT_NODE){
            Element elem=(Element)node;
            NamedNodeMap attr=elem.getAttributes();
            for(int i=attr.getLength()-1;i>=0;i--){
                elem.removeAttribute(attr.item(i).getNodeName());
            }
        }

    }

    public void filter(Node tree){
        TreeTraversal nmove=new TreeTraversal(tree, TreeTraversal.PREORDER);

        Node cnode;
        while((cnode=nmove.Next())!=null){
            removeAttr(cnode);
            NodeList list=cnode.getChildNodes();
            for(int i=list.getLength()-1;i>=0;i--){
                Node child=list.item(i);
                if(this.removeNode(child)){
                    cnode.removeChild(child);
                }
            }
        }

    }

}


