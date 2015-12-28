
package MyPackage;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ali Shahbazi
 */

import java.lang.Math;
import java.util.*;


public class EStatOnline {
    
    
    private double Sum=0;
    private double Sum2=0;
    private int Count=0;
    
    private ArrayList<Double> Keys;
    private ArrayList Objects;

    
    private int NumofElements=1;
    
    public final static int MAX=0;
    public final static int MIN=1;
    private int type=0;//min or max
    
    public EStatOnline(int NumofElements, int type){//if NumofElements=-1: keep all data
        this.NumofElements=NumofElements;
        this.type=type;
        
        Keys=new ArrayList();
        Objects=new ArrayList();

    }
    
    public void Update(double key, Object obj){
        
        
        Count++;
        Sum+=key;
        Sum2+=(key*key);

        //max
        if(type==MAX){
            if(Keys.size()<NumofElements || NumofElements==-1){
                for(int i=0;i<Keys.size()+1;i++){
                    if(i==Keys.size()){
                        Keys.add(key);
                        Objects.add(obj);
                        break;
                    }
                    else{
                        if(key>Keys.get(i)){
                            Keys.add(i, key);
                            Objects.add(i, obj);
                            break;
                        }
                    }
                }
            }
            else{
                for(int i=0;i<NumofElements;i++){
                    if(key>Keys.get(i)){
                        Keys.add(i, key);
                        Objects.add(i, obj);
                        //remove additional records
                        Keys.remove(NumofElements);
                        Objects.remove(NumofElements);
                        break;
                    }
                }
            }
        
        }
        //min
        else if(type==MIN){
            if(Keys.size()<NumofElements || NumofElements==-1){
                for(int i=0;i<Keys.size()+1;i++){
                    if(i==Keys.size()){
                        Keys.add(key);
                        Objects.add(obj);
                        break;
                    }
                    else{
                        if(key<Keys.get(i)){
                            Keys.add(i, key);
                            Objects.add(i, obj);
                            break;
                        }
                    }
                }
            }
            else{
                for(int i=0;i<NumofElements;i++){
                    if(key<Keys.get(i)){
                        Keys.add(i, key);
                        Objects.add(i, obj);
                        //remove additional records
                        Keys.remove(NumofElements);
                        Objects.remove(NumofElements);
                        break;
                    }
                }
            }

        }
            
        
        
    }
    
    public double GetAverage(){
        if(Count==0)
            return 0;
        else
            return Sum/Count;
    }
    public double GetSD(){
        if(Count==0)
            return 0;
        
        double average=Sum/Count;
        double s=Sum2/Count;
        double sd=s-(average*average);
        if(sd<=0)
            return 0;
        else
            return Math.sqrt(sd);
    }
    public double GetSum(){
        
        return Sum;
    }
    
    public ArrayList<Double> GetKeys(){//index 0 has lowest       
        return Keys;
    }
    public ArrayList GetObjects(){//index 0 has lowest       
        return Objects;
    }
    
    public int GetCount(){
        return Count;
    }
    
    public void Reset(){
        Sum=0;
        Sum2=0;
        Count=0;
        
        Keys.clear();
        Objects.clear();

    }
    
    @Override
    public String toString(){
        String res="";
        res+="Count= "+this.GetCount()+"\r\n";
        res+="Average= "+this.GetAverage()+"\r\n";
        res+="SD= "+this.GetSD()+"\r\n";
        res+="Sum= "+this.GetSum()+"\r\n\r\n";
        res+="Max/Min ("+this.GetKeys().size()+")= \r\n";
        for(int i=0;i<this.GetKeys().size();i++){
            res+=i+"- key= "+this.GetKeys().get(i)+" -- " +this.GetObjects().get(i).toString()+"\r\n";
        }
        
        return res;
    }

}
