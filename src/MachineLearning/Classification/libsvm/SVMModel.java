//
// svm_model
//
package MachineLearning.Classification.libsvm;

import java.io.Serializable;
import MachineLearning.DataSet;

public class SVMModel implements Serializable{

    public SVMParam param;	// parameters
    public int numLabels;	// number of classes(labels) = 2
    public int numSV_All;		// total #SV (Support vector)
    public DataSet SVs;	// SVs (SV[l])
    public double[] coefSV;	// coefficients for SVs in decision functions (sv_coef[k-1][l])
    public double rho;		// constants in decision functions (rho[k*(k-1)/2])

    // for classification only
    public int[] labels;		// label of each class (label[k])
    public int[] numSV;		// number of SVs for each class (nSV[k])
    // nSV[0] + nSV[1] + ... + nSV[k-1] = numSV
}
