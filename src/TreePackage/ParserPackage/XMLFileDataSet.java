/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TreePackage.ParserPackage;

import MachineLearning.DataSet;
import MachineLearning.Record;
import TreePackage.NewDocNode;
import TreePackage.TreeTraversal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



//-----------------------------------------------------------------------------------
// Filter Tree
//-----------------------------------------------------------------------------------
public class XMLFileDataSet {
    
    XMLParser_Java xmlparser=new XMLParser_Java();
    CustomFilterDOMTreeDataSet filter=new CustomFilterDOMTreeDataSet();
    
    public void saveDataSet(DataSet dataset, String filename){
        System.out.print("Saving tree dataset as XML file: "+filename+" ... ");
        
        NewDocNode factory=new NewDocNode();
        Node parent=factory.createElement("DataSet");
        factory.getDoc().appendChild(parent);
        
        for(int i=0;i<dataset.size();i++){
            Element rec=(Element)(dataset.get(i).getData());
            rec.setAttribute("label", dataset.get(i).getLabel());
            parent.appendChild(nodeMove(rec, factory.getDoc()));
        }
        xmlparser.save(factory.getDoc(), filename);
        factory=null;
        parent=null;
        
        System.out.println("done");
    }
    
    public DataSet getDataSet(String filename){
        
        System.out.print("Loading tree dataset from XML file: "+filename+" ... ");
        DataSet dataset=new DataSet();
        
        Document doc=xmlparser.parse(filename);
        Node datasetTree=doc.getFirstChild();
        NodeList recList=datasetTree.getChildNodes();
        
        for(int i=0;i<recList.getLength();i++){
            Element rec=(Element)(recList.item(i));
            String label=rec.getAttribute("label");
            filter.filter((Node)rec);
            Record record=new Record((Node)rec,label);
            dataset.add(record);
        }
        
        System.out.println("done");
        return dataset;
    }
    
    private static Node nodeMove(Node oneToMakeFree, Document doc){
        
        Node freeNode = doc.importNode(oneToMakeFree, true);
        //oneToMakeFree.getParentNode().removeChild(oneToMakeFree);
        return freeNode;
    }
}
class CustomFilterDOMTreeDataSet{

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
