/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;



/**
 *
 * @author Ali
 */
public class DataSet extends ArrayList<Record> implements Serializable{
    
    private List<String> labelSet=new ArrayList();
    
    //--------------------------------------
    // label functions
    // --------------------------------------
    public List<String> getLabelSet(){
        List<String> set=new ArrayList();
        for(int i=0;i<labelSet.size();i++){
            set.add(labelSet.get(i));
        }
        return set;
    }
    
    public void updateLabelSet(){
        this.labelSet.clear();
        for(Record rec: this){
            this.addLabel(rec.getLabel());
        }
    }
    private void addLabel(String label){
        if(labelSet.contains(label)==false) {
            labelSet.add(label);
        }
    }
    private void removeLabel(String label){
        if(this.getSizeLabel(label)==0) {
            labelSet.remove(label);
        }
    }
    
    //--------------------------------------
    // 
    // --------------------------------------
    public void randomize(){
        Random rand=new Random();

        for(int i=0;i<this.size();i++){
            int randIndex=rand.nextInt(this.size());
            Record tempRec=this.get(randIndex);
            this.set(randIndex, this.get(i));
            this.set(i, tempRec);
        }
        
        System.out.println("DataSet is randomized");
    }
    
    public DataSet getFold(int size, int startIndex, DataSet setLeft){//setLeft should be build DataSet, it also can be null
        //size is the size of returned datset start at startIndex of main dataset. startIndex==-1 means random selection.
        
        
        DataSet set=new DataSet();
        //return set
        if(startIndex==-1){
            if(size>this.size()){
                throw new IllegalArgumentException("DataSet.getFold: size is larger than dataset!");
            }
            Random rand=new Random();
            DataSet tempSet=this.clone(false);
            
            for(int i=0;i<size;i++){
                int randInt=rand.nextInt(tempSet.size());
                set.add(tempSet.get(randInt));
                tempSet.remove(randInt);               
            }
        }
        else if(startIndex>=0){
            if(size+startIndex>this.size()){
                throw new IllegalArgumentException("DataSet.getFold: size+startIndex is larger than dataset!");
            }
            for(int i=startIndex; i<startIndex+size; i++) {
                set.add(this.get(i));
            }
            
        }
        else{
            throw new IllegalArgumentException("DataSet.getFold: startIndex should be -1 or >=0");
        }

        
        
        
        //leftSet
        if(setLeft!=null){
            setLeft.clear();
            setLeft.addAll(this);           
            setLeft.removeAll(set);
        }
        
        return set;
    }
    
    public void setIDs(int start){
        for(int i=0;i<this.size();i++){
            this.get(i).setID(i+start);
        }
    }
    public void setIDs(){
        this.setIDs(0);
    }
    
    
    //-------------------------------------------
    // get data set
    //-------------------------------------------
    public DataSet getDataSetLabel(String label){ //just shalow copy
        DataSet set=new DataSet();
        for(int i=0;i<this.size();i++){
            if(label.equals(this.get(i).getLabel())){
                set.add(this.get(i));
            }
        }
        return set;
    }   
    public double getSizeLabel(String label){ //number of records with this label
        int num=0;
        for(int i=0;i<this.size();i++){
            if(label.equals(this.get(i).getLabel())) {
                num++;
            }
        }
        return (double)num;
    }   
    
    public DataSetPRes getEmptyDataSetPRes(){ 
        DataSetPRes set=new DataSetPRes();
        for(int i=0;i<this.size();i++){
            set.add(new RecordPRes(this.get(i),""));
        }
        return set;
    }  
    
    public void relabel(String[] oldLabels, String[] newLabels){//the newLabels can contain repetitive labels to change a few old label to a new label
        if(oldLabels.length!=newLabels.length) {
            throw new IllegalArgumentException("DataSet.relabel: length of new labels is "+newLabels.length+", while the length of the old labels is "+oldLabels.length);
        }
        
        List<String> oldLabelsList=Arrays.asList(oldLabels);
        
        //change record labels
        for(int i=0;i<this.size();i++){
            String oldlabel=this.get(i).getLabel();
            //System.err.print(i+" oldlabel:"+oldlabel);
            
            int labelIndex=oldLabelsList.indexOf(oldlabel);
           
            if(labelIndex>=0){
                String newLabel=newLabels[labelIndex];
                this.get(i).setLabel(newLabel);        
            } 

            //System.err.println(" newlabel:"+newLabel+" size"+this.size());
            
        }
        //change labelSet and remove repeatition
        this.updateLabelSet();
    }
    
//    public void relabel(String[] newLabels){//the newLabels can contain repetitive labels to change a few old label to a new label
//        if(this.labelSet.size()!=newLabels.length) {
//            throw new IllegalArgumentException("DataSet.relabel: length of new labels is "+newLabels.length+", while the length of the old labels is "+this.labelSet.size());
//        }
//        
//        //change record labels
//        for(int i=0;i<this.size();i++){
//            String oldlabel=this.get(i).getLabel();
//            //System.err.print(i+" oldlabel:"+oldlabel);
//    
//            int labelIndex=this.labelSet.indexOf(oldlabel);
//           
//            if(labelIndex==-1){
//                throw new IllegalArgumentException("DataSet.relabel: The oldlabel is not in the labelSet");
//            } 
//            //System.err.print(" labelIndex:"+labelIndex);
//            
//            String newLabel=newLabels[labelIndex];
//            
//            
//            //System.err.println(" newlabel:"+newLabel+" size"+this.size());
//            this.get(i).setLabel(newLabel);
//        }
//        //change labelSet and remove repeatition
//        this.updateLabelSet();
//    }
    
    @Override
    public DataSet clone(){ 
        DataSet set=new DataSet();
        for(int i=0;i<this.size();i++){
            set.add(this.get(i));
        }
        return set;
    }  
    
    
    public DataSet clone(boolean deep){ 
        if(deep==false){
            return this.clone();
        }
        else{
            DataSet set=new DataSet();
            for(int i=0;i<this.size();i++){
                set.add(new Record(this.get(i)));
            }
            return set;
        }
        
    }  
    
    @Override
    public boolean add(Record rec){
        super.add(rec);
        this.addLabel(rec.getLabel());
        return true;
    }
    
    @Override
    public boolean addAll(Collection<? extends Record> set){
        for(Record rec: set){
            this.add(rec);
        }   
        return true;
    }

    @Override
    public boolean remove(Object rec){
        boolean res=super.remove(rec);
        this.removeLabel(((Record)rec).getLabel());
        return res;
    }
    
    @Override
    public boolean removeAll(Collection<?> set){
        for(Object rec: set){
            this.remove(rec);
        }   
        return true;
    }
    
    
}
