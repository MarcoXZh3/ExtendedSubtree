

package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;
import TreePackage.TreeTraversal;
import org.w3c.dom.Node;
//-----------------------------------------------------------------------------------
// TreeCompare: Exact match
//-----------------------------------------------------------------------------------
public class TreeKernel_ExactMatch extends AbstractKernelM{
    public int getSimilarityNormalized(Node controlTree, Node testTree){
        NodeCompare nodeComp=new NodeCompare();
        
        TreeTraversal controlMove=new TreeTraversal(controlTree, TreeTraversal.PREORDER);
        TreeTraversal testMove=new TreeTraversal(testTree, TreeTraversal.PREORDER);
        
        Node cnode, tnode;
        int res;
        
        while((cnode=controlMove.Next())!=null){
            
            if((tnode=testMove.Next())==null)
                return 0;
            
            res=(int)nodeComp.Compare(cnode, tnode, NodeCompare.STRUCTURE_CONTENT);
            if(res!=1 || testMove.GetLevel()!=controlMove.GetLevel())
                return 0;
            
        }
        
        if((tnode=testMove.Next())!=null)
            return 0;
        
        
        return 1;
    }
    
    public int getDistanceNormalized(Node controlTree, Node testTree){
        return 1-this.getSimilarityNormalized(controlTree, testTree);
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
        return "ExactMatch";
    }
}
