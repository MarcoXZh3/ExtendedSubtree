
package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.w3c.dom.Node;

//-----------------------------------------------------------------------------------
// TreeCompare: Entropy
//-----------------------------------------------------------------------------------
public class TreeKernel_Entropy extends AbstractKernelM{

    TreeKernel_Path kernelPath=new TreeKernel_Path();
    
    public double getDistanceNormalized(Node controlNode, Node testNode){
        HashMap controlHash=kernelPath.TreeToHash(controlNode);
        HashMap testHash=kernelPath.TreeToHash(testNode);

        double Cc=this.EntropyComplexity(controlHash);
        double Ct=this.EntropyComplexity(testHash);
        double Cct=this.EntropyComplexityCommon(controlHash, testHash);

        double distance=Cct/(Math.sqrt(Cc*Ct))-1;
        return distance;// no normalization required, result is between 0 and 1
    }
    public double getSimilarityNormalized(Node controlNode, Node testNode){
        return 1-this.getDistanceNormalized(controlNode, testNode);
    }

    private double EntropyComplexity(HashMap hmap){
        double result=0;
        
        TreeKernel_Path.HashValue hvalue;
        Collection set=hmap.values();
        Iterator itr=set.iterator();

        double totalSize=kernelPath.HashGetTotalSize(hmap);

        //entropy sum{-p log p}

        while(itr.hasNext()){
            hvalue=(TreeKernel_Path.HashValue)itr.next();
            result=result- (hvalue.NumRepeat/totalSize*Math.log10(hvalue.NumRepeat/totalSize));
        }
        result=Math.pow(10.0, result);

        return result;

    }

    private double EntropyComplexityCommon(HashMap controlhmap, HashMap testhmap){
        double result=0;
        double controlTotalSize=kernelPath.HashGetTotalSize(controlhmap);
        double testTotalSize=kernelPath.HashGetTotalSize(testhmap);
        TreeKernel_Path.HashValue controlHvalue;
        TreeKernel_Path.HashValue testHvalue;
        String controlHkey;
        String testHkey;
        Set controlset=controlhmap.keySet();
        Iterator controlItr=controlset.iterator();
        Set testset=testhmap.keySet();
        Iterator testItr=testset.iterator();


        //entropy
        //go through control
        while(controlItr.hasNext()){
            controlHkey=(String)controlItr.next();
            controlHvalue=(TreeKernel_Path.HashValue)controlhmap.get(controlHkey);
            testHvalue=(TreeKernel_Path.HashValue)testhmap.get(controlHkey);

            double Fxy;
            if(testHvalue==null)
                Fxy= (controlHvalue.NumRepeat/controlTotalSize)/2;
            else
                Fxy= (controlHvalue.NumRepeat/controlTotalSize + testHvalue.NumRepeat/testTotalSize)/2;

            result=result- (Fxy*Math.log10(Fxy));
        }

        //go through test, the ones that are not in control
        while(testItr.hasNext()){
            testHkey=(String)testItr.next();
            controlHvalue=(TreeKernel_Path.HashValue)controlhmap.get(testHkey);
            testHvalue=(TreeKernel_Path.HashValue)testhmap.get(testHkey);

            double Fxy;
            if(controlHvalue==null){
                Fxy= (testHvalue.NumRepeat/testTotalSize)/2;
                result=result- (Fxy*Math.log10(Fxy));
            }
        }

        result=Math.pow(10.0, result);

        return result;

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
        return "Entropy";
    }

}
