

package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;
import TreePackage.DOMProperties;
import TreePackage.TreeTraversal;
import java.util.ArrayList;
import org.w3c.dom.Node;

//-----------------------------------------------------------------------------------
// TreeCompare: Tree Edit Distance
//-----------------------------------------------------------------------------------
public class TreeKernel_TreeEditDistance extends AbstractKernelM{
    final double W_delete=1;
    final double W_insert=1;
    final double W_rename=1;
    
    private double[][] TreeDistanceMatrix;
    
    private ArrayList<Node> controlNodeList=new ArrayList();
    private ArrayList<Integer> controlLeftLeaf=new ArrayList();
    private ArrayList<Integer> controlKeyRoot=new ArrayList();
        
    private ArrayList<Node> testNodeList=new ArrayList();
    private ArrayList<Integer> testLeftLeaf=new ArrayList();
    private ArrayList<Integer> testKeyRoot=new ArrayList();
    
           
    private void ForestDistance(int controlNodeNum, int testNodeNum){
        
        NodeCompare nodeComp=new NodeCompare();
        int controlNodeNumLeaf=controlLeftLeaf.get(controlNodeNum).intValue();
        int testNodeNumLeaf=testLeftLeaf.get(testNodeNum).intValue();
        double[][] FD=new double[controlNodeNum-controlNodeNumLeaf+1+1][testNodeNum-testNodeNumLeaf+1+1];
        
        
        FD[0][0]=0;
        for(int c=1;c<controlNodeNum-controlNodeNumLeaf+1+1;c++){
            FD[c][0]=FD[c-1][0]+W_delete;
        }
        for(int t=1;t<testNodeNum-testNodeNumLeaf+1+1;t++){
            FD[0][t]=FD[0][t-1]+W_insert;
        }
        
        int cc,tt,cc_left,tt_left;
        double s1,s2,s3;
        for(int c=controlNodeNumLeaf;c<=controlNodeNum;c++){
            for(int t=testNodeNumLeaf;t<=testNodeNum;t++){
                
                cc=c-controlNodeNumLeaf+1;
                tt=t-testNodeNumLeaf+1;
                
                
                
                if(controlNodeNumLeaf==controlLeftLeaf.get(c).intValue() && testNodeNumLeaf==testLeftLeaf.get(t).intValue()){
                //both are a single tree (not some tree)
                    double W_rename_cof=1-nodeComp.Compare(controlNodeList.get(c), testNodeList.get(t), NodeCompare.STRUCTURE);
                    
                    s1=FD[cc-1][tt]+W_delete;
                    s2=FD[cc][tt-1]+W_insert;
                    s3=FD[cc-1][tt-1]+(W_rename*W_rename_cof);
                    
                    FD[cc][tt]=Math.min(s1, Math.min(s2, s3));
                    
                    TreeDistanceMatrix[c][t]=FD[cc][tt];
                }
                else{//forest=some tree
                    cc_left=controlLeftLeaf.get(c).intValue()-controlNodeNumLeaf+1;
                    tt_left=testLeftLeaf.get(t).intValue()-testNodeNumLeaf+1;
                
                    s1=FD[cc-1][tt]+W_delete;
                    s2=FD[cc][tt-1]+W_insert;
                    s3=FD[cc_left-1][tt_left-1]+ TreeDistanceMatrix[c][t];
                    
                    FD[cc][tt]=Math.min(s1, Math.min(s2, s3));
                }
            }
        }
        
        /*
        //debug
        System.out.println("----------"+controlNodeNum+"-"+testNodeNum+"----------");
        for(int c=0;c<FD.length;c++){
            for(int t=0;t<FD[0].length;t++){
                System.out.print(FD[c][t]+"\t");
            }
            System.out.println();
        }
         * 
         */
    }
    
    
    private int FindLeftLeafNodeNumber(ArrayList<Node> nodeList, Node root){
    
        TreeTraversal travers=new TreeTraversal(root,TreeTraversal.POSTORDER);
        Node leftLeaf=travers.Next();
        
        for(int i=0;i<nodeList.size();i++){
            if(leftLeaf==nodeList.get(i))
                return i;
        }
        
        //no node found
        System.err.println("Error: TreeEditDistance: FindLeftLeafNodeNumber: no node found");
        return 0;
        
    }
    
    public double getDistance(Node controlTree, Node testTree){
        //reset global var
        controlNodeList.clear();
        controlLeftLeaf.clear();
        controlKeyRoot.clear();
        testNodeList.clear();
        testLeftLeaf.clear();
        testKeyRoot.clear();
    
    
        // pre process ////////////////////////////////////////////////////////////////////////
        DOMProperties prop=new DOMProperties();
        TreeTraversal controlTraverse=new TreeTraversal(controlTree, TreeTraversal.POSTORDER);
        TreeTraversal testTraverse=new TreeTraversal(testTree, TreeTraversal.POSTORDER);
        
        Node temp;
        while((temp=controlTraverse.Next())!=null){
            controlNodeList.add(temp);
            controlLeftLeaf.add(FindLeftLeafNodeNumber(controlNodeList, temp));
            //key root
            if(temp.getPreviousSibling()!=null || temp==controlTree)//keyroot
                controlKeyRoot.add(controlNodeList.size()-1);
        }
        while((temp=testTraverse.Next())!=null){
            testNodeList.add(temp);
            testLeftLeaf.add(FindLeftLeafNodeNumber(testNodeList, temp));
            //key root
            if(temp.getPreviousSibling()!=null || temp==testTree)//keyroot
                testKeyRoot.add(testNodeList.size()-1);
        }
        
        // tree distance /////////////////////////////////////////////////////////////////
        TreeDistanceMatrix=new double[controlNodeList.size()][testNodeList.size()];
        for(int c=0;c<controlKeyRoot.size();c++){
            for(int t=0;t<testKeyRoot.size();t++){
                ForestDistance(controlKeyRoot.get(c), testKeyRoot.get(t));
            }
        }
        
        /*
        //debug
        System.out.println("----------Final----------");
        for(int c=0;c<TreeDistanceMatrix.length;c++){
            for(int t=0;t<TreeDistanceMatrix[0].length;t++){
                System.out.print(TreeDistanceMatrix[c][t]+"\t");
            }
            System.out.println();
        }
        */
        
        return TreeDistanceMatrix[controlNodeList.size()-1][testNodeList.size()-1];  
        //the output is edit distance and is not normalized
    }
    
    public double getDistanceNormalized(Node controlTree, Node testTree){
        DOMProperties pro=new DOMProperties();
        
        double distance=this.getDistance(controlTree, testTree);
        double controlSize=pro.GetTreeSize(controlTree);
        double testSize=pro.GetTreeSize(testTree);
        
        //normalize on size1+size2
        double distance_norm=distance/(controlSize+testSize);
        //normalize on max(size1,size2)
        //double distance_norm=distance/Math.max(controlSize, testSize);

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
        return "TED";
    }
}

