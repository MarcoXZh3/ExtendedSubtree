/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MachineLearning;


import java.io.Serializable;

/**
 *
 * @author Ali
 */
public class Record<T> implements Serializable{
    static final long serialVersionUID = -3955041559381058569L;
    
    private String label;//0, 1, 2, 3 ,label starts from 0
    //private double weight=1;
    private T data;
    private int ID=0;
    //public Record(){
        
    //}
    public Record(Record rec){
        this.label=rec.label;
        this.data=(T)rec.data;
        this.ID=rec.ID;
        //this.weight=rec.weight;
    }
    public Record(T data, String label){
        this.label=label;
        this.data=data;
        //this.weight=weight;
    }
    public Record(T data){
        this.data=data;
        //this.weight=weight;
    }

    public String getLabel(){
        return this.label;        
    }
    
    public void setLabel(String label){
        this.label=label;    
    }
    
    public T getData(){
        return this.data;        
    }
    
    public void setData(T data){
        this.data=data;        
    }
    
    public int getID(){
        return ID;        
    }
    public void setID(int ID){
        this.ID=ID;       
    }
    
    @Override
    public String toString(){
        String str;
        //str="label="+this.label+", weight="+this.weight;
        str="label="+this.label;
        return str;
    }

}
