/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;

/**
 *
 * @author Ali
 */
public class RecordPRes {//RecordPredictionResult
    public String labelPredicted;
    public double weight=1;
    public Record record=null;//a reference to original record
    
    public RecordPRes(){
        
        
    }
    public RecordPRes(Record record, String labelPredicted){
        this.record=record;
        this.labelPredicted=labelPredicted;       
    }
    public RecordPRes(Record record, String labelPredicted, double weight){
        this.record=record;
        this.labelPredicted=labelPredicted;  
        this.weight=weight;
    }
    public RecordPRes(RecordPRes rec){
        this.record=rec.record;
        this.labelPredicted=rec.labelPredicted;       
        this.weight=rec.weight;
    }
    
    @Override
    public String toString(){
        String str;
        str="label="+this.record.getLabel()+", labelPredicted="+this.labelPredicted+", weight="+this.weight;
        return str;
    }
}
