/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning.Classification;

import MachineLearning.DataSet;
import MachineLearning.DataSetPRes;
import MachineLearning.Kernel.AbstractKernelM;
import MachineLearning.Record;
import MachineLearning.RecordPRes;
import MyPackage.StatOnlineExtended;
import java.util.List;

/**
 *
 * @author Ali
 */
public class KNNClassifier {
    int K=9;//deafult
    AbstractKernelM kernel;
    DataSet trainSet;
    
    public KNNClassifier(int k, AbstractKernelM kernel){
        this.K=k;
        this.kernel=kernel;
    }

    public void train(DataSet trainSet){//it has no training
        this.trainSet=trainSet;
    }
    
    
    public RecordPRes predict(Record testRec){
        StatOnlineExtended stat=new StatOnlineExtended(0, K);
        RecordPRes res=new RecordPRes();
        
        if(kernel.useKernelMatrix()==false){
            for(Record trainRec: trainSet){
                stat.Update( kernel.getK(trainRec.getData(), testRec.getData()) , trainRec);
            }
        }
        else{
            for(Record trainRec: trainSet){
                stat.Update( kernel.getK(trainRec.getID(), testRec.getID()) , trainRec);
            }            
        }
        res.labelPredicted=this.voting(stat, K, testRec);
        res.record=testRec;
        return res;
    }
    
    
    public DataSetPRes predict(DataSet testSet){
        System.out.println("KNN classification ... ");
        
        DataSetPRes resSet=new DataSetPRes();
        
        for(int i=0;i<testSet.size();i++){
            RecordPRes res=this.predict(testSet.get(i));
            resSet.add(res);
            if(i%100 ==0) {
                System.out.println("KNNClassification "+kernel.getName()+ " "+ i +"- "+ testSet.get(i).getLabel()+ " - "+res.labelPredicted);
            }
//            //for debug
//            if(testSet.get(i).classID != testSet.get(i).classIDDetected){
//                int x=2;                
//            }           
        }

        System.out.println("knn classification is completed");
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
    
    
    private String voting(StatOnlineExtended stat, int N, Record testRec){//vote on first N of statMax, Record test is for debug

        List<String> labelSet=trainSet.getLabelSet();
        double[] classScore=new double[labelSet.size()];
        
        
        //claculate scores
        for(int i=0;i<N;i++){
            Record recTrain=(Record)(stat.GetMinObj().get(i));
            double similarity=1-stat.GetMinKey().get(i);
            
            String trainLabel=recTrain.getLabel();
            int trainLabelIndex=labelSet.indexOf(trainLabel);

            //classScore[trainLabelIndex]+=1;
            classScore[trainLabelIndex]+=similarity;//weighted knn, similarity or (1-distance)
            //classScore[trainLabelIndex]+=1/(1-similarity);//weighted knn, inverse distance
            
        }            
        //test
//        if(testRec.classID==0){
//            classScore0*=10;
//        }
        
        //analyse: get index of max score
        double maxScore=classScore[0];
        int maxScoreIndex=0;
        for(int i=0;i<classScore.length;i++){
            if(classScore[i]>maxScore){
                maxScore=classScore[i];
                maxScoreIndex=i;
            }
        }
        
        return labelSet.get(maxScoreIndex);
    }
    

    
    
}
