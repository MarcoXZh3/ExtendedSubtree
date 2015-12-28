/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Ali
 */
public class DataSetPRes extends ArrayList<RecordPRes> implements Serializable{
    
    
    public DataSetPRes(){
        
    }

    //-------------------------------------------
    // evaluation
    //-------------------------------------------
    public double getAccuracy(){
        //return the percent of correctness
        
        double result=0;
        for(RecordPRes recRes: this){
            if(recRes.labelPredicted.equals(recRes.record.getLabel())) {
                result++;
            }
        }
        result=result/this.size();
        return result;
    }
    
    public double getPrecision(String label){
        //Precision is calculated for each class, in getDataSetPredicted= true predicted/size
        
        DataSetPRes x=this.getDataSetPredicted(label);
        double tp=x.getDataSetTrue().size();       
        return tp/x.size();
    }
    
    public double getRecall(String label){
        //Recall is calculated for each class, in getDataSetLabel= true predicted/size
        
        DataSetPRes x=this.getDataSetLabel(label);
        double tp=x.getDataSetTrue().size();       
        return tp/x.size();
    }
    public double getFMeasure(String label){
        double recall=this.getRecall(label);
        double precision=this.getPrecision(label);
        if(recall==0 && precision==0) {
            return 0;
        }
        else {
            return 2*(recall*precision)/(recall+precision);
        }
    }
    
    public double getPrecisionAvg(List<String> labelSet){//average on getPrecision(int label)
        double res=0;
        
        for(String label:labelSet){
            res+=this.getPrecision(label);
        }
   
        return res/labelSet.size();
    }
    
    public double getRecallAvg(List<String> labelSet){//average on getRecall(int label)
        double res=0;
        
        for(String label:labelSet){
            res+=this.getRecall(label);
        }
   
        return res/labelSet.size();
    }
    public double getFMeasureAvg(List<String> labelSet){//average on getRecall(int label)
        double res=0;
        
        for(String label:labelSet){
            res+=this.getFMeasure(label);
        }
   
        return res/labelSet.size();
    }
    public double getFMeasureWeightedAvg(List<String> labelSet){//average on getRecall(int label)
        double res=0;
        
        for(String label:labelSet){
            res+=this.getFMeasure(label)*this.getDataSetLabel(label).size();
        }
   
        return res/this.size();
    }
    
    public List<Double> getPrecision(List<String> labelSet){

        List<Double> res=new ArrayList();
        for(String label:labelSet){
            res.add(this.getPrecision(label));
        }
   
        return res;
    }
    
    public List<Double> getRecall(List<String> labelSet){

        List<Double> res=new ArrayList();        
        for(String label:labelSet){
            res.add(this.getRecall(label));
        }
   
        return res;
    }
    public List<Double> getFMeasure(List<String> labelSet){

        List<Double> res=new ArrayList();        
        for(String label:labelSet){
            res.add(this.getFMeasure(label));
        }
   
        return res;
    }
    
    //-------------------------------------------
    // get data set
    //-------------------------------------------
    public DataSetPRes getDataSetLabel(String label){ //just shalow copy
        DataSetPRes set=new DataSetPRes();
        for(int i=0;i<this.size();i++){
            if(label.equals(this.get(i).record.getLabel())) {
                set.add(this.get(i));
            }
        }
        return set;
    }  

    
    public DataSetPRes getDataSetPredicted(String labelPredicted){
        DataSetPRes set=new DataSetPRes();
        for(int i=0;i<this.size();i++){
            if(labelPredicted.equals(this.get(i).labelPredicted)) {
                set.add(this.get(i));
            }
        }
        return set;
    }

    
    public DataSetPRes getDataSetTrue(){ //data set that truly classified
        DataSetPRes set=new DataSetPRes();
        for(int i=0;i<this.size();i++){
            if(this.get(i).labelPredicted.equals(this.get(i).record.getLabel())) {
                set.add(this.get(i));
            }
        }
        return set;
    }  
    public DataSetPRes getDataSetFalse(){ //data set that truly classified
        DataSetPRes set=new DataSetPRes();
        for(int i=0;i<this.size();i++){
            if(this.get(i).labelPredicted.equals(this.get(i).record.getLabel())==false) {
                set.add(this.get(i));
            }
        }
        return set;
    }  
    
    //-------------------------------------------
    // relabel predicated usualy from clustering
    //-------------------------------------------
    public void relabelResult_ManyToOne(){
        //assign each predicted class to the label with max population on the predicted class.
        //two label can be assigned to one predicated class. the approch is for when the number of predicted classes are more than original labels
        
        //get initial labels
        List<String> labelSet=new ArrayList();
        for(RecordPRes rec: this){
            if(labelSet.contains(rec.record.getLabel())==false){
                labelSet.add(rec.record.getLabel());
            }           
        }
        //get predicted labels
        List<String> labelSetPredicted=new ArrayList();
        for(RecordPRes rec: this){
            if(labelSetPredicted.contains(rec.labelPredicted)==false){
                labelSetPredicted.add(rec.labelPredicted);
            }           
        }
        
        //match the result label with original labels
        String[] newLabels=new String[labelSetPredicted.size()];//keeps the new labels for label preducted
        for(int i=0;i<newLabels.length;i++){
            DataSetPRes cluster_i=this.getDataSetPredicted(labelSetPredicted.get(i));
            //get max real label
            String labelMax="";
            int labelMaxCount=0;
            for(int j=0;j<labelSet.size();j++){
                int temp=cluster_i.getDataSetLabel(labelSet.get(j)).size();
                if(temp>labelMaxCount){
                    labelMaxCount=temp;
                    labelMax=labelSet.get(j);
                }
            }
            newLabels[i]=labelMax;
        }
        ///
        for(int i=0;i<this.size();i++){
            int index_labelPredicted=labelSetPredicted.indexOf(this.get(i).labelPredicted);
            this.get(i).labelPredicted=newLabels[index_labelPredicted];
        }
    }
    
    public void relabelResult_OneToOne_AccuracyOpt(){
        //ono-to-one relabel, 
        
        
        //get initial labels
        List<String> labelSet=new ArrayList();
        for(RecordPRes rec: this){
            if(labelSet.contains(rec.record.getLabel())==false){
                labelSet.add(rec.record.getLabel());
            }           
        }
        //get predicted labels
        List<String> labelSetPredicted=new ArrayList();
        for(RecordPRes rec: this){
            if(labelSetPredicted.contains(rec.labelPredicted)==false){
                labelSetPredicted.add(rec.labelPredicted);
            }           
        }
        
        
        //error check
        if(labelSetPredicted.size()!=labelSet.size()){
            throw new IllegalArgumentException("relabelResultBasedOnRecallOpt: labelPredictedSet size is not equal to labelSet size!");
        }
        
        //clone labelsets
        List<String> labelSetChanging=new ArrayList();
        labelSetChanging.addAll(labelSet);
        List<String> labelSetPredictedChanging=new ArrayList();
        labelSetPredictedChanging.addAll(labelSetPredicted);
        

        //match (one to one)the result label with original labels
        String[] newLabels=new String[labelSetPredicted.size()];////keeps the new labels for label preducted
        for(int k=0;k<labelSet.size();k++){

            //get max real label
            String labelPredictedMax=labelSetPredictedChanging.get(0);
            String LabelMax=labelSetChanging.get(0);
            int labelMaxCount=0;
            for(int j=0;j<labelSetPredictedChanging.size();j++){
                for(int i=0;i<labelSetChanging.size();i++){
                    int temp=this.getDataSetPredicted(labelSetPredictedChanging.get(j)).getDataSetLabel(labelSetChanging.get(i)).size();
                    if(temp>labelMaxCount){
                        labelMaxCount=temp;
                        labelPredictedMax=labelSetPredictedChanging.get(j);
                        LabelMax=labelSetChanging.get(i);
                    }
                    else if(temp==labelMaxCount){
                        //we can do something
                    }
                }
            }
            int labelPredictedIndex=labelSetPredicted.indexOf(labelPredictedMax);
            newLabels[labelPredictedIndex]=LabelMax;
            
            //remove res
            labelSetPredictedChanging.remove(labelPredictedMax);
            labelSetChanging.remove(LabelMax);
        }
        //relabel
        for(int i=0;i<this.size();i++){
            int index_labelPredicted=labelSetPredicted.indexOf(this.get(i).labelPredicted);
            this.get(i).labelPredicted=newLabels[index_labelPredicted];
        }
    }
    
    
    
    @Override
    public DataSetPRes clone(){ 
        DataSetPRes set=new DataSetPRes();
        for(int i=0;i<this.size();i++){
            set.add(new RecordPRes(this.get(i)));
        }
        return set;
    }  
    
}
