
package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;
import TreePackage.TreeTraversal;
import java.util.*;
import org.w3c.dom.Node;

//-----------------------------------------------------------------------------------
// TreeCompare: path compare
//-----------------------------------------------------------------------------------
public class TreeKernel_Path extends AbstractKernelM{

    public class HashValue{
        int NumRepeat=0;
        ArrayList ListNode=new ArrayList();
    }

    public HashMap TreeToHash(Node node){
        HashMap hashTree=new HashMap();

        TreeTraversal nmove=new TreeTraversal(node);

        Node cnode, cnode2;
        String path;
        HashValue value;
        
        while((cnode=nmove.Next())!=null){
            TreeTraversal nmove2=new TreeTraversal(cnode);
            while((cnode2=nmove2.Next())!=null){
                path=nmove2.GetPath();
                if((value=(HashValue)hashTree.get(path))==null){//no such path available
                    value=new HashValue();
                    value.NumRepeat=1;
                    value.ListNode.add(cnode2);
                    hashTree.put(path, value);
                }
                else{
                    value.NumRepeat++;
                    value.ListNode.add(cnode2);
                }
            }
        }

        return hashTree;
    }

    private void HashPrint(HashMap hash){
        Set set=hash.keySet();
        Iterator itr=set.iterator();
        while(itr.hasNext()){
            String key=(String)itr.next();
            HashValue value=(HashValue)hash.get(key);
            System.out.println(key+"  ("+value.NumRepeat+")");
        }
    }

    public double HashGetTotalSize(HashMap hmap){
        double totalSize=0;
        HashValue hvalue;
        Collection set=hmap.values();
        Iterator itr=set.iterator();

        //get total size
        while(itr.hasNext()){
            hvalue=(HashValue)itr.next();
            totalSize+=hvalue.NumRepeat;
        }

        return totalSize;
    }

    /*
    private double CompareHashValue(HashValue controlHvalue, HashValue testHvalue, int option){
        double result=0;//between 0 and Math.min(controlHvalue.NumRepeat, testHvalue.NumRepeat)

        if(option==NodeCompare.STRUCTURE)//if we only need to compare structure
            result= Math.min(controlHvalue.NumRepeat, testHvalue.NumRepeat);
        else if(option==NodeCompare.STRUCTURE_CONTENT || option==NodeCompare.STRUCTURE_CONTENT_Ad){
            //define nodeListA,B. A is the one with afewer number of node
            ArrayList nodeListA, nodeListB;
            if(controlHvalue.NumRepeat<=testHvalue.NumRepeat){
                nodeListA=controlHvalue.ListNode;
                nodeListB=testHvalue.ListNode;
            }
            else{
                nodeListA=testHvalue.ListNode;
                nodeListB=controlHvalue.ListNode;
                nodeListA.size();
            }
            //compare
            
            Iterator itrA=nodeListA.iterator();
            int[] used=new int[nodeListB.size()];

            while(itrA.hasNext()){
                Iterator itrB=nodeListB.iterator();
                result+=this.FindBestMatchNode(itrB, (Node)itrA.next(), used, option);
            }
        }
        
        return result;

    }
    
    
    private double FindBestMatchNode(Iterator itr, Node node, int[] used, int option){
        double max=0;
        int max_index=-1;
        NodeCompare ncomp=new NodeCompare();
        Node cnode;
        int k=0;
       
        while(itr.hasNext()){
            cnode=(Node)itr.next();

            if(used[k]==0){//not used yet
                double sim=ncomp.Compare(node, cnode, option);
                if(sim>max){
                    max=sim;
                    max_index=k;
                }
            }
            k++;
        }

        if(max_index==-1){
            return 0;
        }
        else{
            used[max_index]=1;
            return max;
        }
    }
    * 
    */

    private double getIntersectionNormalized(Node controlNode, Node testNode){
        return this.getIntersectionNormalized(controlNode, testNode, NodeCompare.STRUCTURE_CONTENT);
    }
    private double getIntersectionNormalized(Node controlNode, Node testNode, int option){

        HashMap controlhmap=this.TreeToHash(controlNode);
        HashMap testhmap=this.TreeToHash(testNode);

        double result=0;
        double controlTotalSize=this.HashGetTotalSize(controlhmap);
        double testTotalSize=this.HashGetTotalSize(testhmap);

        String controlHkey;

        Set controlset=controlhmap.keySet();
        Iterator controlItr=controlset.iterator();


        HashValue controlHvalue;
        HashValue testHvalue;

        //go through control
        while(controlItr.hasNext()){
            controlHkey=(String)controlItr.next();

            testHvalue=(HashValue)testhmap.get(controlHkey);
            if(testHvalue!=null){
                controlHvalue=(HashValue)controlhmap.get(controlHkey);

                //result+=CompareHashValue(controlHvalue,testHvalue, option);//for advanced comparison considering node similarities
                result+=Math.min(controlHvalue.NumRepeat, testHvalue.NumRepeat);//simple analysis=node are similar or different
            }

        }

        //normalization
        double result_norm= result/Math.max(controlTotalSize, testTotalSize);
        return result_norm;
    }
    
    
    public double getSimilarityNormalized(Node controlNode, Node testNode){
        return this.getIntersectionNormalized(controlNode, testNode);
    }
    public double getDistanceNormalized(Node controlNode, Node testNode){
        return 1-this.getSimilarityNormalized(controlNode, testNode);
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
        return "Path";
    }

}
