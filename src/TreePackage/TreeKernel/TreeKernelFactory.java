/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TreePackage.TreeKernel;

import MachineLearning.Kernel.AbstractKernelM;

/**
 *
 * @author Ali
 */
public class TreeKernelFactory {
    //kernelID
    public final static int ESUBTREE=0;
    public final static int TED=1;
    public final static int ENTROPY=2;
    public final static int PATH=3;
    public final static int MULTISET=4;
    public final static int ISOLATEDSUBTREE=5;
    //
    public final static int TOPDOWN=20;
    public final static int EXACTMATCH=21;
    
    public final static int kernelNum=6;

    
    public static AbstractKernelM getKernel(int kernelID){
        
        if(kernelID==ESUBTREE)
            return new TreeKernel_ExtendedSubtree();
        else if(kernelID==TED)
            return new TreeKernel_TreeEditDistance();
        else if(kernelID==ENTROPY)
            return new TreeKernel_Entropy();
        else if(kernelID==PATH)
            return new TreeKernel_Path();
        else if(kernelID==MULTISET)
            return new TreeKernel_Multisets();
        else if(kernelID==ISOLATEDSUBTREE)
            return new TreeKernel_IsolatedSubtree();       
        else if(kernelID==TOPDOWN)
            return new TreeKernel_TopDown();
        else if(kernelID==EXACTMATCH)
            return new TreeKernel_ExactMatch();
        else
            return null;
    }
    
    public static AbstractKernelM getKernel(String kernelName){
        
        if(kernelName.equalsIgnoreCase("ESubtree"))
            return new TreeKernel_ExtendedSubtree();
        else if(kernelName.equalsIgnoreCase("TED"))
            return new TreeKernel_TreeEditDistance();
        else if(kernelName.equalsIgnoreCase("Entropy"))
            return new TreeKernel_Entropy();
        else if(kernelName.equalsIgnoreCase("Path"))
            return new TreeKernel_Path();
        else if(kernelName.equalsIgnoreCase("Multiset"))
            return new TreeKernel_Multisets();
        else if(kernelName.equalsIgnoreCase("IsolatedSubtree"))
            return new TreeKernel_IsolatedSubtree();       
        else if(kernelName.equalsIgnoreCase("TopDown"))
            return new TreeKernel_TopDown();
        else if(kernelName.equalsIgnoreCase("ExactMatch"))
            return new TreeKernel_ExactMatch();
        else
            return null;
    }
    
}
