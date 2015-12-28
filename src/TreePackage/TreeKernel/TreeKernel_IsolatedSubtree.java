

package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;
import MyPackage.StatOnline;
import org.w3c.dom.*;
import TreePackage.*;
import java.util.*;

//-----------------------------------------------------------------------------------
// TreeCompare: Isolated subtree distance: algorithm presented in ALGORITHMS FOR THE CONSTRAINED EDITING Distance_Zhang_1995
//-----------------------------------------------------------------------------------
public class TreeKernel_IsolatedSubtree extends AbstractKernelM{
    final double W_delete=1;
    final double W_insert=1;
    final double W_rename=1;
    
    private double[][] TreeDistanceMatrix;
    private double[][] ForestDistanceMatrix;
    
    private ArrayList<Node> controlNodeList=new ArrayList();   
    private ArrayList<Node> testNodeList=new ArrayList();

    
           
    private void FillDistanceMatrix(int controlNodeIndex, int testNodeIndex){
        
        NodeCompare nodeComp=new NodeCompare();
        Node controlNode=controlNodeList.get(controlNodeIndex);
        Node testNode=testNodeList.get(testNodeIndex);
        NodeList controlChildList=controlNode.getChildNodes();
        NodeList testChildList=testNode.getChildNodes();
        
//        if(controlChildList.getLength()==0 && testChildList.getLength()==0){
//            double cost=1-nodeComp.Compare(controlNode, testNode, NodeCompare.STRUCTURE);
//            cost=cost*this.W_rename;
//            TreeDistanceMatrix[controlNodeIndex][testNodeIndex]=cost;
//            ForestDistanceMatrix[controlNodeIndex][testNodeIndex]=0;
//            return;
//        }
     
        
        
        double[][] E=new double[controlChildList.getLength()+1][testChildList.getLength()+1];
        E[0][0]=0;
        //E init
        for(int c=1;c<=controlChildList.getLength();c++){
            Node node=controlChildList.item(c-1);
            int id=controlNodeList.indexOf(node);
            E[c][0]=E[c-1][0]+TreeDistanceMatrix[id][0];
        }
        for(int t=1;t<=testChildList.getLength();t++){
            Node node=testChildList.item(t-1);
            int id=testNodeList.indexOf(node);
            E[0][t]=E[0][t-1]+TreeDistanceMatrix[0][id];
        }
        
        //E fillout
        for(int c=1;c<=controlChildList.getLength();c++){
            
            Node controlChild=controlChildList.item(c-1);
            int controlChildIndex=controlNodeList.indexOf(controlChild);
                
            for(int t=1;t<=testChildList.getLength();t++){
                Node testChild=testChildList.item(t-1);
                int testChildIndex=testNodeList.indexOf(testChild);
                //E
                double temp1=E[c][t-1]+TreeDistanceMatrix[0][testChildIndex];
                double temp2=E[c-1][t]+TreeDistanceMatrix[controlChildIndex][0];
                double temp3=E[c-1][t-1]+TreeDistanceMatrix[controlChildIndex][testChildIndex];
                E[c][t]=Math.min(temp1, Math.min(temp2, temp3));
                
            }
        }
        
        //ForestDistanceMatrix
        StatOnline stat=new StatOnline();    
        for (int t = 1; t <= testChildList.getLength(); t++) {
            Node testChild = testChildList.item(t - 1);
            int testChildIndex = testNodeList.indexOf(testChild);
            double value=ForestDistanceMatrix[controlNodeIndex][testChildIndex]-ForestDistanceMatrix[0][testChildIndex];
            stat.Update(value);
        }
        double temp1;
        if(stat.GetCount()==0)
            temp1=999999999999.9;
        else
            temp1=ForestDistanceMatrix[0][testNodeIndex]+stat.GetMin();
        
        stat.Reset();
        for(int c=1;c<=controlChildList.getLength();c++){
            Node controlChild=controlChildList.item(c-1);
            int controlChildIndex=controlNodeList.indexOf(controlChild);
            double value=ForestDistanceMatrix[controlChildIndex][testNodeIndex]-ForestDistanceMatrix[controlChildIndex][0];
            stat.Update(value);           
        }
        double temp2;
        if(stat.GetCount()==0)
            temp2=999999999999.9;
        else
            temp2=ForestDistanceMatrix[controlNodeIndex][0]+stat.GetMin();

        double temp3=E[controlChildList.getLength()][testChildList.getLength()];
        ForestDistanceMatrix[controlNodeIndex][testNodeIndex]=Math.min(temp1, Math.min(temp2, temp3));
        
        //TreeDistanceMatrix
        stat.Reset();  
        for (int t = 1; t <= testChildList.getLength(); t++) {
            Node testChild = testChildList.item(t - 1);
            int testChildIndex = testNodeList.indexOf(testChild);
            double value=TreeDistanceMatrix[controlNodeIndex][testChildIndex]-TreeDistanceMatrix[0][testChildIndex];
            stat.Update(value);
        }
        if(stat.GetCount()==0)
            temp1=999999999999.9;
        else
            temp1=TreeDistanceMatrix[0][testNodeIndex]+stat.GetMin();
        
        
        stat.Reset();
        for(int c=1;c<=controlChildList.getLength();c++){
            Node controlChild=controlChildList.item(c-1);
            int controlChildIndex=controlNodeList.indexOf(controlChild);
            double value=TreeDistanceMatrix[controlChildIndex][testNodeIndex]-TreeDistanceMatrix[controlChildIndex][0];
            stat.Update(value);           
        }
        if(stat.GetCount()==0)
            temp2=999999999999.9;
        else
            temp2=TreeDistanceMatrix[controlNodeIndex][0]+stat.GetMin();
        
        
        double cost=1-nodeComp.Compare(controlNode, testNode, NodeCompare.STRUCTURE);
        cost=cost*(this.W_delete+this.W_insert);//this.W_rename;
        temp3=ForestDistanceMatrix[controlNodeIndex][testNodeIndex]+cost;
        TreeDistanceMatrix[controlNodeIndex][testNodeIndex]=Math.min(temp1, Math.min(temp2, temp3));
    }
    
    
    private int FindTreeSize(ArrayList<Node> nodeList, Node root){    
        TreeTraversal travers=new TreeTraversal(root,TreeTraversal.POSTORDER);
        Node leftLeaf=travers.Next();        
        return nodeList.indexOf(root)-nodeList.indexOf(leftLeaf)+1;       
    }
    
    public double getDistance(Node controlTree, Node testTree){
        //reset global var
        controlNodeList.clear();
        controlNodeList.add(null);
        testNodeList.clear();
        testNodeList.add(null);
    
        // pre process ////////////////////////////////////////////////////////////////////////
        TreeTraversal controlTraverse=new TreeTraversal(controlTree, TreeTraversal.POSTORDER);
        TreeTraversal testTraverse=new TreeTraversal(testTree, TreeTraversal.POSTORDER);        
        
        Node temp;
        while((temp=controlTraverse.Next())!=null){
            controlNodeList.add(temp);
        }
        while((temp=testTraverse.Next())!=null){
            testNodeList.add(temp);
        }
        

        // tree distance /////////////////////////////////////////////////////////////////
        TreeDistanceMatrix=new double[controlNodeList.size()][testNodeList.size()];
        ForestDistanceMatrix=new double[controlNodeList.size()][testNodeList.size()];
        
        TreeDistanceMatrix[0][0]=0;
        ForestDistanceMatrix[0][0]=0;
        
        for(int c=1;c<controlNodeList.size();c++){
            TreeDistanceMatrix[c][0]=this.W_delete*FindTreeSize(controlNodeList, controlNodeList.get(c));
            ForestDistanceMatrix[c][0]=TreeDistanceMatrix[c][0]-W_delete;
        }
        for(int t=1;t<testNodeList.size();t++){
            TreeDistanceMatrix[0][t]=this.W_insert*FindTreeSize(testNodeList, testNodeList.get(t));
            ForestDistanceMatrix[0][t]=TreeDistanceMatrix[0][t]-W_insert;
        }

        
        
        for(int c=1;c<controlNodeList.size();c++){
            for(int t=1;t<testNodeList.size();t++){
                FillDistanceMatrix(c,t);
            }
        }
        
        
        return TreeDistanceMatrix[controlNodeList.size()-1][testNodeList.size()-1];  
        //the output is isolated subtree edit distance and is not normalized
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
    public String toString(){
        String str="";
        str+="TreeDistanceMatrix\r\n";
        for(int c=0;c<TreeDistanceMatrix.length;c++){
            for(int t=0;t<TreeDistanceMatrix[0].length;t++){
                str+=String.format("%.0f   ",TreeDistanceMatrix[c][t]);
            }
            str+="\r\n";
        }
        
        str+="\r\nForestDistanceMatrix\r\n";
        
        for(int c=0;c<ForestDistanceMatrix.length;c++){
            for(int t=0;t<ForestDistanceMatrix[0].length;t++){
                str+=String.format("%.0f   ",ForestDistanceMatrix[c][t]);
            }
            str+="\r\n";
        }
        
        return str;
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
        return "IsolatedSubtree";
    }
}

