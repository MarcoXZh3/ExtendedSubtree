/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning.Clustering;

import MachineLearning.DataSet;
import MachineLearning.DataSetPRes;
import MachineLearning.Kernel.AbstractKernelM;
import MachineLearning.Record;
import MachineLearning.RecordPRes;
import MyPackage.StatOnlineExtended;
import java.util.*;

/**
 *
 * @author Ali
 */

public class KMedoidCluster {
    
    final int MaxIteration=100;
    int K;//num of clusters
    AbstractKernelM kernel;
    int repeatNum;//repeat and get the result with lowest cost
    
    public KMedoidCluster(int k, AbstractKernelM kernel, int repeatNum){
        this.K=k;
        this.kernel=kernel;
        this.repeatNum=repeatNum;
    }
    
    
    public DataSetPRes cluster(DataSet dataset){//cluster repeatNum times and return best
        
        
        DataSetPRes res=null;
        double minCost=999999999999999999999999D;
        
        for(int i=0; i<repeatNum;i++){
            Object[] obj=clusterOnce(dataset);
            DataSetPRes temp=(DataSetPRes)obj[0];
            double cost=(Double)obj[1];
            
            if(cost<minCost){
                res=temp;
                minCost=cost;
            }
        }

        
        return res;
    }
    
    public Object[] clusterOnce(DataSet dataset){

        int[] initialMedoids=this.getInitialMedoids(dataset);
        Object[] obj=this.clusterOnce(dataset, initialMedoids);
        DataSetPRes dataClustered=(DataSetPRes)obj[0];
        Double minCost=(Double)obj[1];
        
        return new Object[]{dataClustered, minCost};
    }
    
    private Object[] clusterOnce(DataSet dataset, int[] initialMedoids){//clusters will be labeded from "0", "1", "2",... 
               
        int[] medoidsIndex=initialMedoids.clone();
        DataSetPRes data=dataset.getEmptyDataSetPRes();
        
        //assign records to medoids for initialization
        assignRecordsToClusters(data, medoidsIndex);
        double minCost=this.getTotalCost(data, medoidsIndex);
        //print
        String print="---------- iteration "+0+" ... -- Cost: "+minCost+"   ClusterSize: ";
        for(int k=0;k<K;k++) {
            print+=data.getDataSetPredicted(k+"").size()+", ";
        }
        print+="  centerIndex: ";
        for(int k=0;k<K;k++) {
            print+=medoidsIndex[k]+", ";
        }
        System.out.println(print);
        //
           
        
        for(int i=0;i<MaxIteration;i++){ 
            boolean change=false;
            for(int j=0;j<data.size();j++){
                int[] tempMedoidsIndex=medoidsIndex.clone();
                //change one medoid
                tempMedoidsIndex[Integer.parseInt(data.get(j).labelPredicted)]=j;
                //reassign records to medoids
                this.assignRecordsToClusters(data, tempMedoidsIndex);
                //get new cost, if lower verify the change
                double cost=this.getTotalCost(data, tempMedoidsIndex);
                if(cost<minCost){
                    minCost=cost;
                    medoidsIndex=tempMedoidsIndex;
                    change=true;
                }
            }
            
            //reassign records to medoids
            this.assignRecordsToClusters(data, medoidsIndex);
                
            //print
            print="---------- iteration "+(i+1)+" ... -- Cost: "+minCost+"   ClusterSize: ";
            for(int k=0;k<K;k++) {
                print+=data.getDataSetPredicted(k+"").size()+", ";
            }
            print+="  centerIndex: ";
            for(int k=0;k<K;k++) {
                print+=medoidsIndex[k]+", ";
            }
            System.out.println(print);
            //
            if(change==false) {
                break;
            }
            
        }

        return new Object[]{data, minCost};
    }
    

    
    private void assignRecordsToClusters(DataSetPRes dataset, int[] medoidsIndex){
        //assign records to clusters
        StatOnlineExtended stat=new StatOnlineExtended(0,1);
                    
        for(int i=0; i<dataset.size(); i++){
            stat.Reset();
            
            for(int j=0;j<K;j++){
                Record rec1=dataset.get(i).record;
                Record rec2=dataset.get(medoidsIndex[j]).record;
                double distance=this.kernel.getK(rec1, rec2);
                stat.Update(distance, j+"");
            }
            
            dataset.get(i).labelPredicted =(String)stat.GetMinObj().get(0);
        }
    }
    
    
    private double getAClusterCost(DataSetPRes dataset, int medoidIndex){
        
        double cost=0;
        
        for(int i=0;i<dataset.size();i++){
            if(dataset.get(i).labelPredicted.equals(dataset.get(medoidIndex).labelPredicted)){
                Record rec1=dataset.get(i).record;
                Record rec2=dataset.get(medoidIndex).record;
                cost+=kernel.getK(rec1, rec2);            
            }
        }
        
        return cost;
    }
    private double getTotalCost(DataSetPRes dataset, int[] medoidsIndex){
        
        double cost=0;
        
        for(int i=0;i<K;i++){
            cost+=this.getAClusterCost(dataset, medoidsIndex[i]);
        }
        
        return cost;
    }
    
    private int[] getInitialMedoids(DataSet dataset){
        List<Integer> initialMedoids=new ArrayList();
        Random rand=new Random();
        //-------------------
        // Random
        //-------------------
//        for(int i=0;i<K;i++){
//            initialMedoids.add(rand.nextInt(dataset.size()));
//        }
        
        //-------------------
        // random and max distances
        //-------------------
        initialMedoids.add(rand.nextInt(dataset.size()));
        for(int i=1;i<K;i++){
            int newMedoid=getMaxDistance(dataset, initialMedoids);
            initialMedoids.add(newMedoid);
        }
        
        //-------------------
        // output
        //-------------------
        int[] medoids=new int[K];
        for(int i=0;i<initialMedoids.size();i++) {
            medoids[i]=initialMedoids.get(i);
        }
                
        return medoids;
    }
    
    private int getMaxDistance(DataSet dataset, List<Integer> pointsIndex){//return index of a data with max distance 
        
        
        StatOnlineExtended statMax=new StatOnlineExtended(1,0);
        StatOnlineExtended statMin=new StatOnlineExtended(0,1);
        
        for(int i=0;i<dataset.size();i++){
            statMin.Reset();
            for(int j=0;j<pointsIndex.size();j++){
                Record rec1=dataset.get(i);
                Record rec2=dataset.get(pointsIndex.get(j).intValue());
                statMin.Update(kernel.getK(rec1, rec2), j);
            }  
            statMax.Update(statMin.GetMinKey().get(0), i);
        }
        return (Integer)statMax.GetMaxObj().get(0);
        
    }
    
    
    
}
