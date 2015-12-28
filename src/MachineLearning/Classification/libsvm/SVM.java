package MachineLearning.Classification.libsvm;


import MachineLearning.DataSet;
import MachineLearning.Kernel.AbstractKernelM;
import MachineLearning.Record;


public class SVM { 

    private static final int LIBSVM_VERSION = 400;


    private double[] solve_c_svc(DataSet prob, SVMParam param, Solver.SolutionInfo si,double Cp, double Cn) {
        
        double[] alpha = new double[prob.size()];
        int l = prob.size();
        double[] minus_ones = new double[l];
        byte[] y = new byte[l];

        int i;

        for (i = 0; i < l; i++) {
            alpha[i] = 0;
            minus_ones[i] = -1;
            if (prob.get(i).getLabel().equals("1")) {
                y[i] = +1;                
            } else {
                y[i] = -1;
            }
        }

        Solver s = new Solver();
        
        //param.kernel.buildKernelMatrix(prob, prob);
        
        s.Solve(l, param.kernel, prob, minus_ones, y,alpha, Cp, Cn, param.eps, si);

        double sum_alpha = 0;
        for (i = 0; i < l; i++) {
            sum_alpha += alpha[i];
        }

        if (Cp == Cn) {
            System.out.println("nu = " + sum_alpha / (Cp * prob.size()));
        }

        for (i = 0; i < l; i++) {
            alpha[i] *= y[i];
        }
        
        return alpha;
    }
    
    //
    // decision_function
    //
    class DecisionFunction {
        double[] alpha;
        double rho;
    };

    
    DecisionFunction svm_train_one(DataSet prob, SVMParam param, double Cp, double Cn) {
       
        Solver.SolutionInfo si = new Solver.SolutionInfo();

        double[] alpha=solve_c_svc(prob, param, si, Cp, Cn);

        System.out.println("obj = " + si.obj + ", rho = " + si.rho);

        // output SVs
        int numSV = 0;
        int nBSV = 0;
        for (int i = 0; i < prob.size(); i++) {
            if (Math.abs(alpha[i]) > 0) {
                numSV++;
                if (prob.get(i).getLabel().equals("1")) {
                    if (Math.abs(alpha[i]) >= si.upper_bound_p) {
                        ++nBSV;
                    }
                } else {
                    if (Math.abs(alpha[i]) >= si.upper_bound_n) {
                        ++nBSV;
                    }
                }
            }
        }

        System.out.println("numSV = " + numSV + ", numBSV = " + nBSV);

        DecisionFunction f = new DecisionFunction();
        f.alpha = alpha;
        f.rho = si.rho;
        return f;
    }
    

    public SVMModel train(DataSet prob, SVMParam param) {
        SVMModel model = new SVMModel();
        model.param = param;
        model.labels = new int[]{1,-1};
        model.numLabels=2;
        // classification
        int l = prob.size();


        DataSet x0=prob.getDataSetLabel("-1");
        DataSet x1=prob.getDataSetLabel("1");
        DataSet x=x1.clone();
        x.addAll(x0);

        // calculate weighted C
        double[] weighted_C = new double[model.numLabels];
        for (int i = 0; i < model.numLabels; i++) {
            weighted_C[i] = param.C;
        }
        
        


        //train model
        DataSet sub_prob=x.clone();

        DecisionFunction f = svm_train_one(sub_prob, param, weighted_C[0], weighted_C[1]);
        
        
        model.rho = f.rho;
        model.numSV_All=0;
        model.numSV = new int[model.numLabels];
        model.SVs=new DataSet();
        
        model.numSV[0]=0;
        model.numSV[1]=0;
        
        for(int k=0;k<x.size();k++){
            if (Math.abs(f.alpha[k]) > 0) {//non zero alfa
                if(k<x1.size()) {
                    model.numSV[0]++;
                }
                else {
                    model.numSV[1]++;
                }
                model.numSV_All++;
                model.SVs.add(x.get(k));
            }           
        }

        model.coefSV = new double[model.numSV_All];
        int i=0;
        for(int k=0;k<x.size();k++){
            if (Math.abs(f.alpha[k]) > 0) {//non zero alfa
                model.coefSV[i++]=f.alpha[k];
            }           
        }
        

        System.out.println("Total numSV = " + model.numSV_All);

        return model;
    }

    public double predict(SVMModel model, Record testRec,AbstractKernelM kernelTrainTest) {
        int i;

        int numLabels = model.numLabels;
        int l = model.numSV_All;

        double[] kvalue = new double[l];
        for (i = 0; i < l; i++) {
            kvalue[i]=SVMClassifier2Class.kernelConvert(kernelTrainTest.getK(model.SVs.get(i), testRec));
        }

        int[] vote = new int[numLabels];


        double sum = 0;
        for (int k = 0; k < model.numSV_All; k++) {
            sum += model.coefSV[k] * kvalue[k];
        }

        sum -= model.rho;
        
        return sum;
        
//        if (sum > 0) {
//            return 1;
//        } else {
//            return -1;
//        }
            
//        if (sum > 0) {
//            ++vote[0];
//        } else {
//            ++vote[1];
//        }
//
//
//        int vote_max_idx = 0;
//        for (i = 1; i < numLabels; i++) {
//            if (vote[i] > vote[vote_max_idx]) {
//                vote_max_idx = i;
//            }
//        }
//
//        return model.labels[vote_max_idx];

    }

    
    
//    public int predict(SVMModel model, Record testRec, AbstractKernelM kernelTrainTest) {
//
//        double[] dec_values;
//
//        dec_values = new double[1];
//
//        int pred_result = predictValues(model, testRec, kernelTrainTest ,dec_values);
//        return pred_result;
//    }
    
    
}
