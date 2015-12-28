
package TreePackage;

import org.w3c.dom.*;

//-----------------------------------------------------------------------------------
// NodeProperties
//-----------------------------------------------------------------------------------
public class DOMProperties{

    public int GetTreeSize(Node node){//get the size of subtree
        TreeTraversal nmove=new TreeTraversal(node);
        int count=0;
        Node cnode;
        while((cnode=nmove.Next())!=null){
            count++;
        }
        return count;
    }
    
    public int GetTreeDepth(Node node){//get the size of subtree
        TreeTraversal nmove=new TreeTraversal(node);
        int maxDepth=0;
        Node cnode;
        while((cnode=nmove.Next())!=null){
            int depth=nmove.GetLevel();
            if(depth>maxDepth)
                maxDepth=depth;
        }
        return maxDepth+1;
    }

    public String GetPath(Node node, int level){
        String str="";
        for(int i=0;i<=level;i++){
            str="/"+node.getNodeName()+str;
            node=node.getParentNode();
        }
        return str;
    }
    public String GetPath(Node node){
        String str="/"+node.getNodeName();

        while((node=node.getParentNode())!=null)
            str="/"+node.getNodeName()+str;

        return str;
    }
    
    public Node LeftLeafDescendant(Node node){
        Node LeftLeaf=node;
        
        while(true){
            if(LeftLeaf.hasChildNodes()==true){
                LeftLeaf=node.getFirstChild();
            }
            else{
                break;
            }
        }
        
        return LeftLeaf;
    }
    public Node LeftLeafDescendant(Node node, int[] level){//level: array of length 1 that returns level
        Node LeftLeaf=node;
        int le=0;
        while(true){
            if(LeftLeaf.hasChildNodes()==true){
                LeftLeaf=LeftLeaf.getFirstChild();
                le++;
            }
            else{
                break;
            }
        }
        level[0]=le;
        return LeftLeaf;
    }
}