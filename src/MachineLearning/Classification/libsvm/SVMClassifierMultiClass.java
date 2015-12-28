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
import MyPackage.EStatOnline;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ali
 */
public class SVMClassifierMultiClass {
    double C;
    double eps;
    AbstractKernelM kernelTrain;
    AbstractKernelM kernelTrainTest;
    
    List<SVMClassifier2Class> svmList=new ArrayList();
    String[] labelSet;
    
    public SVMClassifierMultiClass(double C, double eps, AbstractKernelM kernelTrain, AbstractKernelM kernelTrainTest){//kernels can have kernelMatrix( recommended)
        this.C=C;
        this.eps=eps;
        this.kernelTrain=kernelTrain;
        this.kernelTrainTest=kernelTrainTest;
    }
    
    private void trainMClass(DataSet trainSet){

        for(int i=0;i<labelSet.length;i++){
            //gen new labels, one class 1 and others -1
            String[] newLabelSet=new String[labelSet.length];
            for(int j=0;j<labelSet.length;j++){
                if(i==j) {
                    newLabelSet[j]="1";
                }
                else {
                    newLabelSet[j]="-1";
                }
            }
            //gen new dataset
            DataSet newTrainSet=trainSet.clone(true);
            newTrainSet.relabel(labelSet, newLabelSet);
            //new SVM 
            SVMClassifier2Class svm=new SVMClassifier2Class(C, eps, kernelTrain, kernelTrainTest);
            svm.train(newTrainSet);
            svmList.add(svm);
            
        }
        

    }
    
    private void train2Class(DataSet trainSet){
   

        //gen new dataset
        DataSet newTrainSet=trainSet.clone(true);
        newTrainSet.relabel(labelSet, new String[]{"1","-1"});
        //new SVM 
        SVMClassifier2Class svm=new SVMClassifier2Class(C, eps, kernelTrain, kernelTrainTest);
        svm.train(newTrainSet);
        svmList.add(svm);

    }
    public void train(DataSet trainSet){
        //get labels
        labelSet=trainSet.getLabelSet().toArray(new String[0]);
        
        if(labelSet.length==2) {
            this.train2Class(trainSet);
        }
        else {
            this.trainMClass(trainSet);
        }
        
    }
    private RecordPRes predictMClass(Record testRec){
        RecordPRes res=new RecordPRes();
        
        EStatOnline stat=new EStatOnline(1, EStatOnline.MAX);
        
        for(int i=0;i<labelSet.length;i++){
            double x=svmList.get(i).predictCoef(testRec);
            stat.Update(x, labelSet[i]);
        }
        
        String resLabel=(String)stat.GetObjects().get(0);
        res.labelPredicted=resLabel;
        res.record=testRec;
        return res;
    }
    
    private RecordPRes predict2Class(Record testRec){
        RecordPRes res=new RecordPRes();

        double x=svmList.get(0).predictCoef(testRec);
        if(x>0) {
            res.labelPredicted=labelSet[0];
        }
        else {
            res.labelPredicted=labelSet[1];
        }

        res.record=testRec;
        return res;
    }
    
    public RecordPRes predict(Record testRec){
        if(labelSet.length==2) {
            return this.predict2Class(testRec);
        }
        else {
            return this.predictMClass(testRec);
        }
    }
    
    public DataSetPRes predict(DataSet testSet){
        System.out.println("SVMMultiClass prediction ... ");
        
        DataSetPRes resSet=new DataSetPRes();

        for(int i=0;i<testSet.size();i++){
            RecordPRes res=this.predict(testSet.get(i));
            resSet.add(res);
            if(i%100 ==0) {
                System.out.println("SVMMultiClass Prediction "+kernelTrainTest.getName()+ " "+ i +"- "+ testSet.get(i).getLabel()+ " - "+res.labelPredicted);
            }
//                //for debug
//                if(testSet.get(i).classID != testSet.get(i).classIDDetected){
//                    int x=2;                
//                }           
        }
        
        System.out.println("SVMMultiClass prediction is completed");
        return resSet;
    }
    
    public DataSetPRes KFoldTrainPredict(DataSet dataSet, int k){
        dataSet.randomize();
        
        DataSetPRes result=new DataSetPRes();
        
        int foldSize=dataSet.size()/k;
        
        for(int i=0;i<k;i++){
            DataSet trainSet1=new DataSet();
            DataSet testSet=dataSet.getFold(foldSize, i*foldSize, trainSet1);
            this.train(trainSet1);
            DataSetPRes res=this.predict(testSet);
            result.addAll(res);
        }
        
        return result;
    }
}
