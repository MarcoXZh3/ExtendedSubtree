/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import MachineLearning.Classification.KNNClassifier;
import MachineLearning.Classification.libsvm.SVMClassifierMultiClass;
import MachineLearning.Clustering.KMedoidCluster;
import MachineLearning.DataSet;
import MachineLearning.DataSetPRes;
import MachineLearning.Kernel.AbstractKernelM;
import MyPackage.*;
import TreePackage.ParserPackage.*;
import TreePackage.TreeKernel.*;  

import java.util.*;



public class Main {
    
    static String resultFilePrefix="";
    static boolean KmedoidEnabled;
    static boolean KNNEnabled;
    static boolean SVMEnabled;
    static String[] codeStructureClasses;
    
    
    
    public static void main(String[] args) throws Exception {
         
        //TreeBank------------------
        resultFilePrefix="TreeBank6Class_";
        for(int i=0;i<100;i++){
            getDataSetsAndPredict(i);
        }       

        //SIGMOD dataset------------------
//        resultFilePrefix="SIGMOD_";
//        getDataSetsAndPredict(0);
        
        //CSLOG dataset------------------
//        resultFilePrefix="CSLOG_";
//        getDataSetsAndPredict(0);
        
        //Random dataset------------------
//        resultFilePrefix="Random_";
//        for(int i=0;i<100;i++){
//            getDataSetsAndPredict(i);
//        } 
        
        

    }//end of main function
    
    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    public static void getDataSetsAndPredict(int runIndex){
        DataSet[] datasets=getDataSets(runIndex);
        performPredictions(datasets[0], datasets[1]);
    }
    public static DataSet[] getDataSets(int runIndex){//index may or may not be used
        //return array of DataSets index 0=trainSet, index 1=testSet
        
        KmedoidEnabled=true;
        KNNEnabled=true;
        SVMEnabled=true;
        
        boolean ClusteringEnabled=KmedoidEnabled;
        boolean ClassificationEnabled=KNNEnabled || SVMEnabled;
        //------------
        // Get dataset
        //------------
        DataSet trainSet=null;
        DataSet testSet=null;
        
        //TreeBank------------------ //clustering and classification at the same time
        PredictionEngine.C_svm=2;
        String dataSetAddr="D:\\Ali\\AnyCode\\Java\\Paper_TKDE_TreeSimilarity\\DataSet\\TreeBank\\6Class2\\RandomSelection\\"+runIndex+".xml";
        XMLFileDataSet file=new XMLFileDataSet();
        testSet=file.getDataSet(dataSetAddr); 
        
       
  
        //SIGMOD------------------ //clustering and classification at the same time
//        PredictionEngine.C_svm=2;
//        DataSetParser_ACMSIGMOD setParser=new DataSetParser_ACMSIGMOD();
//        testSet=setParser.parse("D:\\Ali\\AnyCode\\Java\\DOMTree\\DataSet\\ACM_SIGMOD\\XMLSigmodRecordMar1999");

        
        //CSLOG-------------------- //only clustering or classification
//        PredictionEngine.C_svm=2;
//        DataSetParser_CSLOG cslog=new DataSetParser_CSLOG();
//        if(ClassificationEnabled==true && ClusteringEnabled==false){
//            trainSet= cslog.parse("D:\\Ali\\AnyCode\\Java\\Paper_TKDE_TreeSimilarity\\DataSet\\CSLog\\week1.asc");
//            testSet= cslog.parse("D:\\Ali\\AnyCode\\Java\\Paper_TKDE_TreeSimilarity\\DataSet\\CSLog\\week2.asc");
//        }
//        else if(ClassificationEnabled==false && ClusteringEnabled==true){
//            testSet= cslog.parse("D:\\Ali\\AnyCode\\Java\\Paper_TKDE_TreeSimilarity\\DataSet\\CSLog\\week2.asc");
//        }
//        else{
//            throw new IllegalArgumentException("You cannot run clustering and classification together for CSLOG dataset");
//        }

        
        //Random------------------- //only clustering or classification
//        PredictionEngine.C_svm=4;
//        final int labelNum=3;
//        String classificationDestSetAddr="D:\\Ali\\AnyCode\\Java\\Paper_TKDE_TreeSimilarity\\DataSet\\SyntheticRandom\\Classification\\"+labelNum+"Class\\";
//        String clustringDataSetAddr="D:\\Ali\\AnyCode\\Java\\Paper_TKDE_TreeSimilarity\\DataSet\\SyntheticRandom\\Clustering\\"+labelNum+"Class\\";
//        XMLFileDataSet file=new XMLFileDataSet();
//        if(ClassificationEnabled==true && ClusteringEnabled==false){
//            trainSet=file.getDataSet(classificationDestSetAddr+"train"+runIndex+".xml");
//            testSet=file.getDataSet(classificationDestSetAddr+"test"+runIndex+".xml");
//        }
//        else if(ClassificationEnabled==false && ClusteringEnabled==true){
//            testSet=file.getDataSet(clustringDataSetAddr+runIndex+".xml");
//        }
//        else{
//            throw new IllegalArgumentException("You cannot run clustering and classification together for Random dataset");
//        }
        
        /////////////////////////////////////////////////
        testSet.setIDs();
        if(trainSet!=null) {
            trainSet.setIDs();
        }
        return new DataSet[]{trainSet, testSet};
    }
    
    public static void performPredictions(DataSet trainSet, DataSet testSet){
        
        //------------------------------------------------------------
        // classification, Clustering, SIGMOD and CSLOG datasets
        //--------------------------------------------------------------        
        TimemSec timer=new TimemSec();
        
        //print to file
        ResultPrint file_knn=null;
        ResultPrint file_svm=null;
        ResultPrint file_kmedoid=null;
        
        if(KNNEnabled){
            file_knn=new ResultPrint(resultFilePrefix+"result_knn.csv");
            file_knn.preprint(testSet.getLabelSet());
        }
        if(SVMEnabled){
            file_svm=new ResultPrint(resultFilePrefix+"result_svm.csv");
            file_svm.preprint(testSet.getLabelSet());
        }
        if(KmedoidEnabled && trainSet==null){
            file_kmedoid=new ResultPrint(resultFilePrefix+"result_kmedoid.csv");
            file_kmedoid.preprint(testSet.getLabelSet());
        }
        //--------
        // Clustring, Classification
        //--------
        //double[] betaArray=new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        //double[] alfaArray=new double[]{1, 1.2, 1.4, 1.5, 1.6, 1.7, 1.8, 2, 2.2, 2.4, 2.6};        
        double[] betaArray=new double[]{0.5};
        double[] alfaArray=new double[]{1.6};
        
        for(int betaIndex=0;betaIndex<betaArray.length;betaIndex++){
            for(int alfaIndex=0;alfaIndex<alfaArray.length;alfaIndex++){
                for(int k=0;k<TreeKernelFactory.kernelNum;k++){
                    timer.Start();
                    //get kernel
                    AbstractKernelM kernel=TreeKernelFactory.getKernel(k);
                    
                    if(kernel.getName().equalsIgnoreCase("ESubtree")){
                        ((TreeKernel_ExtendedSubtree)kernel).alfa=alfaArray[alfaIndex];
                        ((TreeKernel_ExtendedSubtree)kernel).beta=betaArray[betaIndex];
                    }
                    
                    //build kernel
                    if(trainSet==null) {
                        kernel.buildKernelMatrix(testSet, testSet, true);
                    }
                    else {
                        kernel.buildKernelMatrix(trainSet, testSet, true);
                    }
                    
                    double time_all=timer.GetTime();
                    
                    //prediction
                    DataSetPRes pres;
                    //knn
                    if(KNNEnabled){
                        timer.Start();
                        pres=PredictionEngine.knn(trainSet, testSet, kernel);
                        file_knn.printResults(pres, testSet.getLabelSet(), kernel, timer.GetTime()+time_all);
                    }
                    //svm
                    if(SVMEnabled){
                        timer.Start();
                        pres=PredictionEngine.svm(trainSet, testSet, kernel);
                        file_svm.printResults(pres, testSet.getLabelSet(), kernel, timer.GetTime()+time_all);
                    }
                    //kmedoid
                    if(KmedoidEnabled && trainSet==null){
                        timer.Start();
                        pres=PredictionEngine.kmedoid(testSet, kernel, 10);
                        file_kmedoid.printResults(pres, testSet.getLabelSet(), kernel, timer.GetTime()+time_all);
                    }
                }
            }
        }
        
        
    }//end of function
    
}//end of main class

class PredictionEngine{
    //parameters
    //KNN
    public static int k_knn=9;
    //SVM
    public static double C_svm=2;
    public static double eps_svm=1e-3;
    //
    public static int kfold=10;

    public PredictionEngine(){
        super(); 
    }
    public static DataSetPRes knn(DataSet trainSet, DataSet testSet, AbstractKernelM kernel){
        if(trainSet==null) {
            return knn_kfold(testSet, kernel);
        }
        else {
            return knn_TrainTest(trainSet, testSet, kernel);
        }
    }
    
    public static DataSetPRes knn_kfold(DataSet testSet, AbstractKernelM kernel){
        KNNClassifier knn=new KNNClassifier(k_knn, kernel);
        DataSetPRes pres=knn.KFoldTrainPredict(testSet,kfold);
        
        return pres;
    }
    public static DataSetPRes knn_TrainTest(DataSet trainSet, DataSet testSet, AbstractKernelM kernel){
        KNNClassifier knn=new KNNClassifier(k_knn, kernel);
        knn.train(trainSet);
        DataSetPRes pres=knn.predict(testSet);
        
        return pres;
    }
    
    public static DataSetPRes svm(DataSet trainSet, DataSet testSet, AbstractKernelM kernel){
        if(trainSet==null) {
            return svm_kfold(testSet, kernel);
        }
        else {
            return svm_TrainTest(trainSet, testSet, kernel);
        }
    }
    
    public static DataSetPRes svm_kfold(DataSet testSet, AbstractKernelM kernel){
        SVMClassifierMultiClass svm=new SVMClassifierMultiClass(C_svm, eps_svm, kernel, kernel);
        DataSetPRes pres=svm.KFoldTrainPredict(testSet,kfold);
        return pres;
    }
    public static DataSetPRes svm_TrainTest(DataSet trainSet, DataSet testSet, AbstractKernelM kernel){
        AbstractKernelM kernelTrainTest=kernel;
        
        AbstractKernelM kernelTrain=TreeKernelFactory.getKernel(kernel.getName());
        if(kernel.getName().equalsIgnoreCase("ESubtree")){
            ((TreeKernel_ExtendedSubtree)kernelTrain).alfa=((TreeKernel_ExtendedSubtree)kernel).alfa;
            ((TreeKernel_ExtendedSubtree)kernelTrain).beta=((TreeKernel_ExtendedSubtree)kernel).beta;
        }

        kernelTrain.buildKernelMatrix(trainSet, trainSet, true);

        SVMClassifierMultiClass svm=new SVMClassifierMultiClass(C_svm, eps_svm, kernelTrain, kernelTrainTest);
        svm.train(trainSet);
        DataSetPRes pres=svm.predict(testSet);
        
        return pres;
    }
    
    public static DataSetPRes kmedoid(DataSet testSet, AbstractKernelM kernel, int repeat){
        
        List<String> labelSet=testSet.getLabelSet();
        //Cluster, K-Medoid
        KMedoidCluster clust=new KMedoidCluster(labelSet.size(), kernel, repeat);
        DataSetPRes pres=clust.cluster(testSet);
        pres.relabelResult_OneToOne_AccuracyOpt();
        //pres.relabelResult_ManyToOne();
        for(int i=0;i<labelSet.size();i++){
            double clusterSize=(double)pres.getDataSetPredicted(labelSet.get(i)).size();
            double clusterCorrectSize=(double)pres.getDataSetPredicted(labelSet.get(i)).getDataSetTrue().size();
            System.out.println("predictedLabel="+labelSet.get(i) +", clusterSize="+clusterSize+", clusterCorrectSize="+clusterCorrectSize);
        }
        
        return pres;
    }

}


class ResultPrint{
    public RWFile file;
    
    public ResultPrint(String filename){
        super();
        file=new RWFile(filename);
        
    }
    
    public void preprint(List<String> labelSet){
        file.println("");
        file.print("Kernel, Alfa, Beta, Accuracy, ");
        for(int k=0;k<labelSet.size();k++) {
            file.print("FMeasure"+k+", ");
        }
        file.print("FMeasureAvg, ");
        file.print("FMeasureWeightedAvg, ");
        file.println("Time(Sec), ");
    }
    
    public void printResults(DataSetPRes pres, List<String> labelSet, AbstractKernelM kernel, double time){
        //result calc
        double accuracy=pres.getAccuracy();
        List<Double> fmeasure=pres.getFMeasure(labelSet);
        double fmeasureAvg=pres.getFMeasureAvg(labelSet);
        double fmeasureWeightedAvg=pres.getFMeasureWeightedAvg(labelSet);   

        time=time/1000;

        System.out.println("Kernel="+kernel.getName()+" Accuracy="+accuracy+" FMeasureAvg="+fmeasureAvg+" FMeasureWeighetAvg="+fmeasureWeightedAvg+ " Time(Sec)="+time);

        //print to file
        if(kernel.getName().equalsIgnoreCase("ESubtree")){

            file.print(kernel.getName()+", "+((TreeKernel_ExtendedSubtree)kernel).alfa+", "+((TreeKernel_ExtendedSubtree)kernel).beta+", "+accuracy+", ");
        }
        else{
            file.print(kernel.getName()+", "+", "+", "+accuracy+", ");
        }

        for(int i=0;i<fmeasure.size();i++) {
            file.print(fmeasure.get(i) +", ");
        }
        file.print(fmeasureAvg+", ");
        file.print(fmeasureWeightedAvg+", ");
        file.println(time+", ");
    }
    
}