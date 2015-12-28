package MachineLearning.Classification.libsvm;

import java.io.Serializable;
import MachineLearning.Kernel.AbstractKernelM;

public class SVMParam implements Cloneable, Serializable{
    /*
     * svm_type: C_SVC
     */

    //public static final int C_SVC = 0;
    //public static final int NU_SVC = 1;
    //public static final int ONE_CLASS = 2;
    //public static final int EPSILON_SVR = 3;
    //public static final int NU_SVR = 4;

    public AbstractKernelM kernel;
    
    
    // these are for training only
    public double eps;	// stopping criteria
    public double C;	// for C_SVC, EPSILON_SVR and NU_SVR
    


    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
