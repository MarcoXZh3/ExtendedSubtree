

package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;
import org.w3c.dom.*;
import TreePackage.*;
import java.util.*;

public class TreeKernel_Multisets extends AbstractKernelM{//based on : A Tree Distance Function Based on Multi-sets, 2009
    class NodeRepeat{
        int NumRepeat=0;
        Node node;
        public NodeRepeat(Node n){
            node=n;
            NumRepeat=1;
        }
    }
    
    private ArrayList<NodeRepeat> TreeToArray_Node(Node node){
        ArrayList<NodeRepeat> nodeList=new ArrayList();
        TreeTraversal nmove=new TreeTraversal(node);
        NodeCompare ncomp=new NodeCompare();
        Node cnode;
        boolean a=false;
        
        while((cnode=nmove.Next())!=null){
            a=false;
            for(int i=0;i<nodeList.size();i++){
                if(ncomp.Compare(cnode, nodeList.get(i).node, NodeCompare.STRUCTURE_CONTENT)==1){
                    nodeList.get(i).NumRepeat++;
                    a=true;
                    break;
                }
            }
            
            if(a==false)
                nodeList.add(new NodeRepeat(cnode));
                
                
            
        }

        return nodeList;
    }
    
    private ArrayList<NodeRepeat> TreeToArray_Subtree(Node node){
        ArrayList<NodeRepeat> nodeList=new ArrayList();
        TreeTraversal nmove=new TreeTraversal(node);
        //NodeCompare ncomp=new NodeCompare();
        TreeKernel_ExactMatch tcomp=new TreeKernel_ExactMatch();
        Node cnode;
        boolean a=false;
        
        while((cnode=nmove.Next())!=null){
            a=false;
            for(int i=0;i<nodeList.size();i++){
                if(tcomp.getSimilarityNormalized(cnode, nodeList.get(i).node)==1){
                    nodeList.get(i).NumRepeat++;
                    a=true;
                    break;
                }
            }
            if(a==false)
                nodeList.add(new NodeRepeat(cnode));
                               
            
        }

        return nodeList;
    }


    public double getDistance(Node controlTree, Node testTree){
        DOMProperties pro=new DOMProperties();
        TreeKernel_ExactMatch tcomp=new TreeKernel_ExactMatch();
        NodeCompare ncomp=new NodeCompare();
        
        double union_n=0, intersection_n=0, union_s=0, intersection_s=0, Ds=0, Dn=0, mtd=0, mtd_norm=0, similarity_norm=0;
        double controlSize=pro.GetTreeSize(controlTree);
        double testSize=pro.GetTreeSize(testTree);
        
        ArrayList<NodeRepeat> testNodeList=this.TreeToArray_Node(testTree);
        ArrayList<NodeRepeat> controlNodeList=this.TreeToArray_Node(controlTree);
        
        ArrayList<NodeRepeat> testSubtreeList=this.TreeToArray_Subtree(testTree);
        ArrayList<NodeRepeat> controlSubtreeList=this.TreeToArray_Subtree(controlTree);
        
        //intersection of nodes
        for(int i=0;i<controlNodeList.size();i++){
            for(int j=0;j<testNodeList.size();j++){
                if(ncomp.Compare(controlNodeList.get(i).node, testNodeList.get(j).node, NodeCompare.STRUCTURE_CONTENT)==1){
                    intersection_n=intersection_n+  Math.min(controlNodeList.get(i).NumRepeat, testNodeList.get(j).NumRepeat);
                    break;
                }
            }
        }
        //intersection of subtrees 
        for(int i=0;i<controlSubtreeList.size();i++){
            for(int j=0;j<testSubtreeList.size();j++){
                if(tcomp.getSimilarityNormalized(controlSubtreeList.get(i).node, testSubtreeList.get(j).node)==1){
                    intersection_s=intersection_s+  Math.min(controlSubtreeList.get(i).NumRepeat, testSubtreeList.get(j).NumRepeat);
                    break;
                }
            }
        }
        
        //union
        union_n = controlSize + testSize - intersection_n;
        union_s = controlSize + testSize - intersection_s;
        
        //distances
        Dn= union_n- intersection_n;
        Ds= union_s- intersection_s;
        
        mtd=(Dn+Ds)/2;
        return mtd;

    } 
    
    public double getDistanceNormalized(Node controlTree, Node testTree){
        DOMProperties pro=new DOMProperties();
        double controlSize=pro.GetTreeSize(controlTree);
        double testSize=pro.GetTreeSize(testTree);
        
        double distance=this.getDistance(controlTree, testTree);
        //normalization on sum
        double distance_norm= distance/(controlSize+testSize);
        return distance_norm;

    }
    public double getSimilarityNormalized(Node controlTree, Node testTree){
        return 1-this.getDistanceNormalized(controlTree, testTree);
    }
    
    @Override
    public double getK(Object objA, Object objB){
        if(!(objA instanceof Node && objB instanceof Node)){
            throw new IllegalArgumentException("The input object to getK in a TreeKernel is not a Node instance");
        }
        
        return this.getDistanceNormalized((Node)objA, (Node)objB);
    }

    @Override
    public String getName() {
        return "Multiset";
    }
    
}

