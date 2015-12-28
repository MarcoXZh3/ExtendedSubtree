

package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;
import org.w3c.dom.*;
import TreePackage.*;

//-----------------------------------------------------------------------------------
// TreeCompare: Simple Tree Matching (top down distance)-- my version: weight is removed
//-----------------------------------------------------------------------------------
public class TreeKernel_TopDown extends AbstractKernelM{
    
    
    public double getSimilarityNormalized(Node controlNode, Node testNode){
        double num_match=this.getIntersectionSize(controlNode, testNode);
        DOMProperties pro=new DOMProperties();
        double normal= num_match/( Math.max(pro.GetTreeSize(controlNode), pro.GetTreeSize(testNode))) ;
        
        return normal;
    }
    public double getSimilarity(Node controlNode, Node testNode){
        return this.getIntersectionSize(controlNode, testNode);
    }
    public double getDistanceNormalized(Node controlNode, Node testNode){
        return 1-this.getSimilarityNormalized(controlNode, testNode);
    }
    
    private double getIntersectionSize(Node controlNode, Node testNode){

        double num_match= (double)this.getIntersectionSizeLoop(controlNode, testNode);
        return num_match;
        
    }
    
 
    private int getIntersectionSizeLoop(Node controlNode, Node testNode) {

        NodeCompare nodeCompare=new NodeCompare();

        if(nodeCompare.CompareStructure(controlNode, testNode)==0)
            return 0; //not similar


        

        NodeList controlNodeList = controlNode.getChildNodes();
        int m = controlNodeList.getLength();
        NodeList testNodeList = testNode.getChildNodes();
        int n = testNodeList.getLength();

        int i,j;
        int[][] MAT = new int[m+1][n+1];
        for (i=0; i<=m; i++) { MAT[i][0]=0; }
        for (j=0; j<=n; j++) { MAT[0][j]=0; }

        for (i=1; i<=m; i++) {
            for (j=1; j<=n; j++) {
                int wij = getIntersectionSizeLoop(controlNodeList.item(i-1), testNodeList.item(j-1));
                MAT[i][j] = Math.max(MAT[i][j-1], MAT[i-1][j]);
                MAT[i][j] = Math.max(MAT[i][j], MAT[i-1][j-1] + wij);
            }
        }

        double node_compare_res=nodeCompare.Compare(controlNode, testNode, NodeCompare.STRUCTURE_CONTENT);
        if(node_compare_res>= 1)
            return MAT[m][n]+1;
        else
            return MAT[m][n];

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
        return "TopDown";
    }
}



