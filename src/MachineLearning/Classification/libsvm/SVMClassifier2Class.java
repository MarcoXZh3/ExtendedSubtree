/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning.Classification.libsvm;

import MachineLearning.DataSet;
import MachineLearning.DataSetPRes;
import MachineLearning.Kernel.AbstractKernelM;
import MachineLearning.Record;
import MachineLearning.RecordPRes;

/**
 *
 * @author Ali
 */
public class SVMClassifier2Class {
    
    //param
    SVM svm;
    SVMParam svmparam;
    SVMModel svmmodel=null;
    AbstractKernelM kernelTrainTest;
    
    public SVMClassifier2Class(double C, double eps, AbstractKernelM kernelTrain, AbstractKernelM kernelTrainTest){//kernels can have kernelMatrix( recommended)
        //kernelTrain is used for training, must be Train*Train
        //kernelTrainTest is used for prediction, must be Train*Test
        //kernelTrainTest and kernelTrain can be equal when we do not have kernel matrix or when test and train have one integrated kernelMatrix
        
        this.kernelTrainTest=kernelTrainTest;
        
        svmparam = new SVMParam();
        svm=new SVM();

        svmparam.kernel=kernelTrain;
        svmparam.C = C;
        svmparam.eps = eps;
    }
    
    public void train(DataSet prob){//labels should be "1", "-1"
        this.svmmodel=svm.train(prob, svmparam);      
    }
    
    public double predictCoef(Record testRec) {//labels should be "1", "-1"
        if(svmmodel!=null){
            return svm.predict(svmmodel, testRec, kernelTrainTest);
        }
        else {
            throw new IllegalArgumentException("svmmodel is null");
        }
    }
    public RecordPRes predict(Record testRec) {//labels should be "1", "-1"
        RecordPRes res=new RecordPRes();
        double x=predictCoef(testRec);
        if(x>0)               
            res.labelPredicted="1";
        else
            res.labelPredicted="-1";

        res.record=testRec;
        return res;

    }
    
    public DataSetPRes predict(DataSet testSet){
        System.out.println("SVM prediction ... ");
        
        DataSetPRes resSet=new DataSetPRes();

        for(int i=0;i<testSet.size();i++){
            RecordPRes res=this.predict(testSet.get(i));
            resSet.add(res);
            if(i%100 ==0) {
                System.out.println("SVMPrediction "+kernelTrainTest.getName()+ " "+ i +"- "+ testSet.get(i).getLabel()+ " - "+res.labelPredicted);
            }
//                //for debug
//                if(testSet.get(i).classID != testSet.get(i).classIDDetected){
//                    int x=2;                
//                }           
        }
        
        System.out.println("SVM prediction is completed");
        return resSet;
    }
    
    public static double kernelConvert(double in){
        return 1-in;//Math.exp(-4*(in));
    }
}
